package com.glowxq.triflow.base.template.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.template.entity.Article;
import com.glowxq.triflow.base.template.enums.ArticleStatus;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章列表响应 VO
 * <p>
 * 用于文章列表展示，包含基本信息，不包含文章正文。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Schema(description = "文章列表响应")
@AutoMapper(target = Article.class)
public class ArticleVO implements BaseVO {

    /** 文章ID */
    @Schema(description = "文章ID", example = "1")
    private Long id;

    /** 文章标题 */
    @Schema(description = "文章标题", example = "Spring Boot 入门教程")
    private String title;

    /** 文章摘要 */
    @Schema(description = "文章摘要", example = "本文介绍Spring Boot的基础知识")
    private String summary;

    /** 封面图片URL */
    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    /** 分类ID */
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /**
     * 状态
     *
     * @see ArticleStatus
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 浏览次数 */
    @Schema(description = "浏览次数", example = "1024")
    private Integer viewCount;

    /** 创建时间 */
    @Schema(description = "创建时间", example = "2025-01-22T10:30:00")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间", example = "2025-01-22T15:45:00")
    private LocalDateTime updateTime;

}
