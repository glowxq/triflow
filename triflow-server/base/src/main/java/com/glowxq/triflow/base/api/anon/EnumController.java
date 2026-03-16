package com.glowxq.triflow.base.api.anon;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.enums.base.EnumRegistry;
import com.glowxq.triflow.base.system.pojo.vo.EnumItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AnonApi;

/**
 * 枚举管理控制器
 * <p>
 * 提供统一的枚举查询接口，前端可以通过枚举类名获取枚举选项列表。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Tag(name = "枚举管理", description = "统一枚举查询接口")
@AnonApi
@RestController
@RequestMapping("/base/public/enums")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class EnumController extends BaseApi {

    private final EnumRegistry enumRegistry;

    /**
     * 获取指定枚举的选项列表
     *
     * @param enumClassName 枚举类简称（如 StatusEnum）
     * @return 枚举选项列表
     */
    @Operation(summary = "获取枚举选项列表", description = "根据枚举类名获取该枚举的所有选项")
    @GetMapping("/{enumClassName}")
    public ApiResult<List<EnumItem>> getEnumOptions(
            @Parameter(description = "枚举类简称", required = true, example = "StatusEnum")
            @PathVariable String enumClassName) {

        if (!enumRegistry.hasEnum(enumClassName)) {
            return ApiResult.error(400, "枚举类不存在: " + enumClassName);
        }

        List<EnumItem> items = enumRegistry.getEnumItems(enumClassName).stream()
                .map(dto -> new EnumItem(dto.code(), dto.name()))
                .collect(Collectors.toList());

        return ApiResult.success(items);
    }

    /**
     * 获取所有可用的枚举名称
     *
     * @return 枚举名称集合
     */
    @Operation(summary = "获取所有枚举名称", description = "获取系统中所有已注册的枚举类名称")
    @GetMapping
    public ApiResult<Set<String>> getAllEnumNames() {
        Set<String> enumNames = enumRegistry.getAllEnumNames();
        return ApiResult.success(enumNames);
    }

    /**
     * 批量获取多个枚举的选项列表
     *
     * @param enumClassNames 枚举类名列表（逗号分隔）
     * @return 枚举选项映射表
     */
    @Operation(summary = "批量获取枚举选项", description = "一次获取多个枚举的选项列表")
    @GetMapping("/batch/{enumClassNames}")
    public ApiResult<Map<String, List<EnumItem>>> getBatchEnumOptions(
            @Parameter(description = "枚举类名列表（逗号分隔）", required = true, example = "StatusEnum,GenderEnum")
            @PathVariable String enumClassNames) {

        String[] names = enumClassNames.split(",");
        Map<String, List<EnumItem>> result = new java.util.LinkedHashMap<>();

        for (String name : names) {
            String trimmedName = name.trim();
            if (enumRegistry.hasEnum(trimmedName)) {
                List<EnumItem> items = enumRegistry.getEnumItems(trimmedName).stream()
                        .map(dto -> new EnumItem(dto.code(), dto.name()))
                        .collect(Collectors.toList());
                result.put(trimmedName, items);
            }
        }

        return ApiResult.success(result);
    }
}
