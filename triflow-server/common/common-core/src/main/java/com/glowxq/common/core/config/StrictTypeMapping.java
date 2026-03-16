package com.glowxq.common.core.config;

import org.mapstruct.control.MappingControl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 严格类型映射控制
 * <p>
 * 禁用 MapStruct 的内置类型转换（如 String ↔ Integer），
 * 当 DTO 和 Entity 的字段类型不一致时，编译期会直接报错。
 * </p>
 *
 * <h3>启用的映射方式</h3>
 * <ul>
 *   <li>{@code DIRECT} - 直接赋值（类型完全相同）</li>
 *   <li>{@code MAPPING_METHOD} - 使用自定义映射方法</li>
 * </ul>
 *
 * <h3>禁用的映射方式</h3>
 * <ul>
 *   <li>{@code BUILT_IN_CONVERSION} - 内置类型转换（如 String → Integer）</li>
 *   <li>{@code COMPLEX_MAPPING} - 复杂映射（两步映射）</li>
 * </ul>
 *
 * @author glowxq
 * @see MappingControl
 * @since 2025-01-22
 */
@Retention(RetentionPolicy.CLASS)
@MappingControl(MappingControl.Use.DIRECT)
@MappingControl(MappingControl.Use.MAPPING_METHOD)
public @interface StrictTypeMapping {
}
