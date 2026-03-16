package com.glowxq.triflow.base.template.pojo.dto;

import com.glowxq.triflow.base.template.enums.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章查询请求 DTO
 * <p>
 * 用于列表查询和分页查询时的参数传递。
 * 查询 DTO 不需要转换为实体，因此直接实现 Serializable。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Schema(description = "文章查询请求")
public class ArticleQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（标题模糊匹配） */
    @Schema(description = "搜索关键词（标题模糊匹配）", example = "Spring")
    private String keyword;

    /** 分类ID筛选 */
    @Schema(description = "分类ID筛选", example = "1")
    private Long categoryId;

    /**
     * 状态筛选
     *
     * @see ArticleStatus
     */
    @Schema(description = "状态筛选", example = "1", allowableValues = {"0", "1", "2"})
    private Integer status;

    /** 创建时间-开始 */
    @Schema(description = "创建时间-开始", example = "2025-01-01T00:00:00")
    private LocalDateTime startTime;

    /** 创建时间-结束 */
    @Schema(description = "创建时间-结束", example = "2025-01-31T23:59:59")
    private LocalDateTime endTime;

    // ========== 分页参数 ==========

    /** 页码（从1开始） */
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页数量 */
    @Schema(description = "每页数量", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

}
