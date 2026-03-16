package com.glowxq.triflow.base.template.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.template.enums.ArticleStatus;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章实体类
 * <p>
 * 对应数据库表 article，存储文章内容信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Table("article")
@Schema(description = "文章")
public class Article implements BaseEntity {

    /**
     * 文章ID
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "文章ID")
    private Long id;

    /**
     * 文章标题
     */
    @Schema(description = "文章标题")
    private String title;

    /**
     * 文章内容
     */
    @Schema(description = "文章内容")
    private String content;

    /**
     * 文章摘要
     */
    @Schema(description = "文章摘要")
    private String summary;

    /**
     * 封面图片URL
     */
    @Schema(description = "封面图片URL")
    private String coverUrl;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 文章状态
     *
     * @see ArticleStatus
     */
    @Schema(description = "文章状态")
    private Integer status;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /**
     * 逻辑删除标识
     *
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;

    // ========== 审计字段（自动填充，无需手动设置）==========

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新者ID
     */
    @Schema(description = "更新者ID")
    private Long updateBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

}
