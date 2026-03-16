package com.glowxq.triflow.base.template.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.template.entity.Article;
import com.glowxq.triflow.base.template.enums.ArticleStatus;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文章创建请求 DTO
 * <p>
 * 用于创建新文章时的参数传递。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Schema(description = "文章创建请求")
@AutoMapper(target = Article.class)
public class ArticleCreateDTO implements BaseDTO {

    /** 文章标题 */
    @Schema(description = "文章标题", example = "Spring Boot 入门教程", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200字符")
    private String title;

    /** 文章内容 */
    @Schema(description = "文章内容", example = "这是文章的正文内容...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /** 文章摘要 */
    @Schema(description = "文章摘要", example = "本文介绍Spring Boot的基础知识")
    @Size(max = 500, message = "文章摘要长度不能超过500字符")
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
    @Schema(description = "状态", example = "0", allowableValues = {"0", "1"}, defaultValue = "0")
    private Integer status;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, Article.class);
    }

}
