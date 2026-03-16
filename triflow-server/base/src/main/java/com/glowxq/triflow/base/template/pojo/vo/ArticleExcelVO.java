package com.glowxq.triflow.base.template.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.excel.annotation.DictFormat;
import com.glowxq.triflow.base.template.entity.Article;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章 Excel 导入导出 VO
 * <p>
 * 专用于 Excel 导入导出，与 API 响应的 VO 分离。
 * 支持字典转换、下拉选项等 Excel 特有功能。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = Article.class)
public class ArticleExcelVO {

    /** 文章ID（导出时显示，导入时可忽略） */
    @ExcelProperty("文章ID")
    @ColumnWidth(12)
    private Long id;

    /** 文章标题 */
    @ExcelProperty("文章标题")
    @ColumnWidth(40)
    private String title;

    /** 文章摘要 */
    @ExcelProperty("文章摘要")
    @ColumnWidth(50)
    private String summary;

    /** 封面图片URL */
    @ExcelProperty("封面图片URL")
    @ColumnWidth(40)
    private String coverUrl;

    /** 分类ID */
    @ExcelProperty("分类ID")
    @ColumnWidth(12)
    private Long categoryId;

    /**
     * 状态
     * <p>
     * 使用表达式进行字典转换：0=草稿,1=已发布,2=已下架
     * isSelected=true 表示导出时生成下拉选项
     * </p>
     */
    @ExcelProperty("状态")
    @ColumnWidth(12)
    @DictFormat(readConverterExp = "0=草稿,1=已发布,2=已下架", isSelected = true)
    private String status;

    /** 浏览次数 */
    @ExcelProperty("浏览次数")
    @ColumnWidth(12)
    private Integer viewCount;

    /** 创建时间 */
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;

    /** 更新时间 */
    @ExcelProperty("更新时间")
    @ColumnWidth(20)
    private LocalDateTime updateTime;

    // ========== 以下字段不导出到 Excel ==========

    /** 文章内容（导入导出时忽略，内容太长） */
    @ExcelIgnore
    private String content;

}
