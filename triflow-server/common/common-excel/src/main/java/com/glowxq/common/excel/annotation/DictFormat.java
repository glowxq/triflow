package com.glowxq.common.excel.annotation;

import com.glowxq.common.excel.core.ExcelDynamicSelect;

import java.lang.annotation.*;

/**
 * Excel 字典格式化注解
 * <p>
 * 用于 Excel 导入导出时的字典转换和下拉选项生成。
 * 支持三种数据源：字典类型、表达式、动态数据源。
 * </p>
 *
 * @author glowxq
 * @since 2023/12/26
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DictFormat {

    /**
     * 字典类型（如: account_status）
     * <p>
     * 使用字典服务获取下拉选项和转换映射
     * </p>
     */
    String dictType() default "";

    /**
     * 转换表达式（如: 0=男,1=女,2=未知）
     * <p>
     * 当 dictType 为空时使用此表达式进行转换
     * </p>
     */
    String readConverterExp() default "";

    /**
     * 分隔符，用于分割转换表达式
     */
    String separator() default ",";

    /**
     * 是否生成下拉选项（导出时生效）
     */
    boolean isSelected() default false;

    /**
     * 动态下拉数据源类（适用于导出模板场景）
     */
    Class<? extends ExcelDynamicSelect>[] sourceClass() default {};

    /**
     * 是否使用字典别名进行映射
     * <p>
     * 为 true 时使用 alias 字段而非 id 字段做映射
     * </p>
     */
    boolean useAlias() default false;
}
