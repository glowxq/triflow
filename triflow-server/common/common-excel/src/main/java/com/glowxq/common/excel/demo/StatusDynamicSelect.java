package com.glowxq.common.excel.demo;

import com.glowxq.common.excel.core.ExcelDynamicSelect;

import java.util.Arrays;
import java.util.List;

/**
 * 状态动态下拉数据源示例
 * <p>
 * 演示如何实现 {@link ExcelDynamicSelect} 接口，
 * 为 Excel 导出模板提供动态的下拉选项。
 * </p>
 *
 * @author glowxq
 * @since 2024/01/01
 */
public class StatusDynamicSelect implements ExcelDynamicSelect {

    @Override
    public List<String> getSource() {
        return Arrays.asList("正常", "禁用", "待审核", "已删除");
    }

}
