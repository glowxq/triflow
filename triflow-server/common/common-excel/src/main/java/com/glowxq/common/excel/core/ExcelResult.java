package com.glowxq.common.excel.core;

import java.util.List;

/**
 * Excel 导入结果接口
 * <p>
 * 封装 Excel 导入后的数据列表、错误信息和分析结果。
 * </p>
 *
 * @param <T> 数据实体类型
 * @author glowxq
 * @since 2023/12/26
 */
public interface ExcelResult<T> {

    /**
     * 获取解析成功的数据列表
     *
     * @return 数据列表
     */
    List<T> getList();

    /**
     * 获取解析过程中的错误信息列表
     *
     * @return 错误信息列表
     */
    List<String> getErrorList();

    /**
     * 获取导入分析结果摘要
     *
     * @return 分析结果字符串
     */
    String getAnalysis();
}
