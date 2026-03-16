package com.glowxq.common.core.config;

import io.github.linpeilie.annotations.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct Plus 全局配置
 * <p>
 * 配置严格的类型映射规则，当 DTO 和 Entity 的字段类型不一致时，
 * 在编译期直接报错，而不是自动进行类型转换。
 * </p>
 *
 * <h3>配置说明</h3>
 * <ul>
 *   <li>{@code mappingControl = StrictTypeMapping.class} - 禁用内置类型转换</li>
 *   <li>{@code unmappedTargetPolicy = IGNORE} - 未映射的目标属性忽略</li>
 *   <li>{@code typeConversionPolicy = ERROR} - 有损类型转换报错</li>
 * </ul>
 *
 * <h3>生效范围</h3>
 * <p>
 * 此配置为全局默认配置，所有使用 {@code @AutoMapper} 注解的类都会继承这些设置。
 * 如需覆盖，可在 {@code @AutoMapper} 上单独指定 {@code mappingControl} 属性。
 * </p>
 *
 * @author glowxq
 * @see StrictTypeMapping
 * @see io.github.linpeilie.annotations.AutoMapper
 * @since 2025-01-22
 */
@MapperConfig(
    mappingControl = StrictTypeMapping.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    typeConversionPolicy = ReportingPolicy.ERROR
)
public class MapStructGlobalConfig {
}
