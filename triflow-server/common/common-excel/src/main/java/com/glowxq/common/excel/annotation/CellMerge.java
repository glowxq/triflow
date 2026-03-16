package com.glowxq.common.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel 单元格合并注解
 * <p>
 * 用于导出时自动合并相同值的连续单元格。
 * </p>
 *
 * @author glowxq
 * @since 2023/12/26
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {

    /**
     * 列索引（从 0 开始）
     * <p>
     * -1 表示使用字段声明顺序作为索引
     * </p>
     */
    int index() default -1;
}
