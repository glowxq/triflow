package com.glowxq.triflow.base.template.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.template.entity.Article;
import com.glowxq.triflow.base.template.enums.ArticleStatus;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文章更新请求 DTO
 * <p>
 * 用于更新文章时的参数传递。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Schema(description = "文章更新请求")
@AutoMapper(target = Article.class)
public class ArticleUpdateDTO implements BaseDTO {

    /** 文章ID */
    @Schema(description = "文章ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文章ID不能为空")
    private Long id;

    /** 文章标题 */
    @Schema(description = "文章标题", example = "Spring Boot 入门教程（修订版）")
    @Size(max = 200, message = "文章标题长度不能超过200字符")
    private String title;

    /** 文章内容 */
    @Schema(description = "文章内容", example = "这是更新后的文章正文内容...")
    private String content;

    /** 文章摘要 */
    @Schema(description = "文章摘要", example = "本文介绍Spring Boot的基础知识（更新版）")
    @Size(max = 500, message = "文章摘要长度不能超过500字符")
    private String summary;

    /** 封面图片URL */
    @Schema(description = "封面图片URL", example = "https://example.com/cover-new.jpg")
    private String coverUrl;

    /** 分类ID */
    @Schema(description = "分类ID", example = "2")
    private Long categoryId;

    /**
     * 状态
     *
     * @see ArticleStatus
     */
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1", "2"})
    private Integer status;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, Article.class);
    }

}
