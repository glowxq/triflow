package com.glowxq.common.excel.core;

import java.util.List;

/**
 * Excel 动态下拉数据源接口
 * <p>
 * 实现此接口以提供自定义的下拉选项数据源。
 * 常用于导出模板时生成动态的下拉选项。
 * </p>
 *
 * <pre>
 * 示例：
 * public class StatusSelect implements ExcelDynamicSelect {
 *     &#64;Override
 *     public List&lt;String&gt; getSource() {
 *         return Arrays.asList("启用", "禁用", "待审核");
 *     }
 * }
 * </pre>
 *
 * @author glowxq
 * @since 2023/12/28
 */
public interface ExcelDynamicSelect {

    /**
     * 获取下拉选项数据
     *
     * @return 下拉选项列表
     */
    List<String> getSource();
}
