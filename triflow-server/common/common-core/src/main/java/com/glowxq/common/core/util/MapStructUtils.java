package com.glowxq.common.core.util;

import io.github.linpeilie.Converter;

import java.util.List;
import java.util.Map;

/**
 * MapStruct Plus 对象转换工具类
 * <p>
 * 基于 MapStruct Plus 封装的统一对象转换工具，提供高性能的编译期代码生成转换。
 * 使用前需在 DTO/VO 类上添加 {@code @AutoMapper(target = XXX.class)} 注解定义转换关系。
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 1. 在 DTO 类上定义转换关系
 * @AutoMapper(target = User.class)
 * public class UserDTO { ... }
 *
 * // 2. 使用工具类进行转换
 * User user = MapStructUtils.convert(userDTO, User.class);
 * List<UserVO> voList = MapStructUtils.convert(userList, UserVO.class);
 * }</pre>
 *
 * @author glowxq
 * @see io.github.linpeilie.Converter
 * @see io.github.linpeilie.annotations.AutoMapper
 * @since 2025-01-22
 */
public class MapStructUtils {

    /** MapStruct Plus 核心转换器（懒加载） */
    private static volatile Converter CONVERTER;

    private MapStructUtils() {
        throw new IllegalStateException("MapStructUtils class Illegal");
    }

    /**
     * 获取 Converter 实例（线程安全的懒加载）
     *
     * @return Converter 实例
     */
    private static Converter getConverter() {
        if (CONVERTER == null) {
            synchronized (MapStructUtils.class) {
                if (CONVERTER == null) {
                    CONVERTER = SpringUtils.getBean(Converter.class);
                }
            }
        }
        return CONVERTER;
    }

    /**
     * 将源对象转换为目标类型的新实例
     * <p>
     * 源对象类或目标类需要使用 {@code @AutoMapper} 注解定义转换关系。
     * </p>
     *
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @param source      源对象
     * @param targetClass 目标类型的 Class
     *
     * @return 转换后的目标对象实例，如果源对象为 null 则返回 null
     */
    public static <S, T> T convert(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return getConverter().convert(source, targetClass);
    }

    /**
     * 将源对象的属性复制到目标对象
     * <p>
     * 适用于更新场景，将源对象的属性值复制到已存在的目标对象中。
     * </p>
     *
     * @param <S>    源对象类型
     * @param <T>    目标对象类型
     * @param source 源对象
     * @param target 目标对象（已存在的实例）
     *
     * @return 更新后的目标对象
     */
    public static <S, T> T convert(S source, T target) {
        if (source == null || target == null) {
            return target;
        }
        return getConverter().convert(source, target);
    }

    /**
     * 将源对象列表转换为目标类型的列表
     * <p>
     * 批量转换时使用，内部会对每个元素进行转换。
     * </p>
     *
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @param sourceList  源对象列表
     * @param targetClass 目标类型的 Class
     *
     * @return 转换后的目标对象列表，如果源列表为 null 则返回 null
     */
    public static <S, T> List<T> convert(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        return getConverter().convert(sourceList, targetClass);
    }

    /**
     * 将 Map 转换为目标类型的对象
     * <p>
     * Map 的 key 应与目标类的属性名对应。
     * </p>
     *
     * @param <T>         目标对象类型
     * @param map         包含属性键值对的 Map
     * @param targetClass 目标类型的 Class
     *
     * @return 转换后的目标对象实例，如果 Map 为 null 则返回 null
     */
    public static <T> T convert(Map<String, Object> map, Class<T> targetClass) {
        if (map == null) {
            return null;
        }
        return getConverter().convert(map, targetClass);
    }

    // ==================== 兼容性方法（与 BeanCopyUtils 保持 API 一致）====================

    /**
     * 将源对象转换为目标类型的新实例（兼容性方法）
     * <p>
     * 此方法与 {@link #convert(Object, Class)} 功能相同，保留此方法是为了与旧版 BeanCopyUtils 保持 API 兼容。
     * </p>
     *
     * @param <T>         目标对象类型
     * @param source      源对象
     * @param targetClass 目标类型的 Class
     *
     * @return 转换后的目标对象实例
     *
     * @deprecated 建议使用 {@link #convert(Object, Class)} 方法
     */
    @Deprecated
    public static <T> T copy(Object source, Class<T> targetClass) {
        return convert(source, targetClass);
    }

    /**
     * 将源对象列表转换为目标类型的列表（兼容性方法）
     * <p>
     * 此方法与 {@link #convert(List, Class)} 功能相同，保留此方法是为了与旧版 BeanCopyUtils 保持 API 兼容。
     * </p>
     *
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @param sourceList  源对象列表
     * @param targetClass 目标类型的 Class
     *
     * @return 转换后的目标对象列表
     *
     * @deprecated 建议使用 {@link #convert(List, Class)} 方法
     */
    @Deprecated
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        return convert(sourceList, targetClass);
    }

}
