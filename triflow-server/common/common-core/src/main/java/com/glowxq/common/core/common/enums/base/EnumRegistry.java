package com.glowxq.common.core.common.enums.base;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 枚举注册器
 * <p>
 * 自动扫描所有实现 {@link BaseEnum} 接口的枚举类，并提供统一的查询接口。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Slf4j
@Component
public class EnumRegistry {

    /**
     * 枚举缓存：key=枚举类简称（如 StatusEnum），value=枚举选项列表
     */
    private final Map<String, List<EnumItemDTO>> enumCache = new ConcurrentHashMap<>();

    /**
     * 枚举类全名映射：key=简称，value=全限定名
     */
    private final Map<String, String> fullNameMapping = new ConcurrentHashMap<>();

    /**
     * 扫描的基础包
     */
    private static final String BASE_PACKAGE = "com.glowxq";

    /**
     * 初始化时扫描枚举
     */
    @PostConstruct
    public void init() {
        scanEnums();
        log.info("EnumRegistry initialized, found {} enum classes", enumCache.size());
    }

    /**
     * 扫描所有实现 BaseEnum 的枚举类
     */
    @SuppressWarnings("unchecked")
    private void scanEnums() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(BaseEnum.class));

        Set<BeanDefinition> candidates = scanner.findCandidateComponents(BASE_PACKAGE);

        for (BeanDefinition candidate : candidates) {
            String className = candidate.getBeanClassName();
            if (className == null) {
                continue;
            }

            try {
                Class<?> clazz = Class.forName(className);

                // 确保是枚举类
                if (!clazz.isEnum()) {
                    continue;
                }

                // 获取枚举常量
                Object[] enumConstants = clazz.getEnumConstants();
                if (enumConstants == null || enumConstants.length == 0) {
                    continue;
                }

                // 转换为枚举选项列表
                List<EnumItemDTO> items = new ArrayList<>();
                for (Object constant : enumConstants) {
                    if (constant instanceof BaseEnum baseEnum) {
                        items.add(new EnumItemDTO(baseEnum.getCode(), baseEnum.getName()));
                    }
                }

                // 使用简称作为 key
                String simpleName = clazz.getSimpleName();
                enumCache.put(simpleName, items);
                fullNameMapping.put(simpleName, className);

                log.debug("Registered enum: {} with {} items", simpleName, items.size());

            } catch (ClassNotFoundException e) {
                log.warn("Failed to load enum class: {}", className, e);
            }
        }
    }

    /**
     * 获取枚举选项列表
     *
     * @param enumClassName 枚举类简称（如 StatusEnum）
     * @return 枚举选项列表，若不存在则返回空列表
     */
    public List<EnumItemDTO> getEnumItems(String enumClassName) {
        return enumCache.getOrDefault(enumClassName, List.of());
    }

    /**
     * 获取所有可用的枚举名称
     *
     * @return 枚举名称集合
     */
    public Set<String> getAllEnumNames() {
        return enumCache.keySet();
    }

    /**
     * 获取所有枚举及其选项
     *
     * @return 枚举映射表
     */
    public Map<String, List<EnumItemDTO>> getAllEnums() {
        return new LinkedHashMap<>(enumCache);
    }

    /**
     * 检查枚举是否存在
     *
     * @param enumClassName 枚举类简称
     * @return 是否存在
     */
    public boolean hasEnum(String enumClassName) {
        return enumCache.containsKey(enumClassName);
    }

    /**
     * 获取枚举的全限定类名
     *
     * @param enumClassName 枚举类简称
     * @return 全限定类名，若不存在则返回 null
     */
    public String getFullClassName(String enumClassName) {
        return fullNameMapping.get(enumClassName);
    }

    /**
     * 枚举选项 DTO（内部使用）
     *
     * @param code 枚举编码
     * @param name 枚举名称
     */
    public record EnumItemDTO(String code, String name) {
    }
}
