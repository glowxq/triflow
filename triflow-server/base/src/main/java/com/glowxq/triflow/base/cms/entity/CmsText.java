package com.glowxq.triflow.base.cms.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.cms.enums.TextStatusEnum;
import com.glowxq.triflow.base.cms.enums.TextTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文本内容实体类
 * <p>
 * 对应数据库表 cms_text，存储各类文本内容（文章、公告、帮助等）。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Table("cms_text")
@Schema(description = "文本内容")
public class CmsText implements BaseEntity {

    /**
     * 文本ID（主键，自增）
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "文本ID")
    private Long id;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 文本标识（唯一键）
     */
    @Schema(description = "文本标识")
    private String textKey;

    /**
     * 文本标题
     */
    @Schema(description = "文本标题")
    private String textTitle;

    /**
     * 副标题
     */
    @Schema(description = "副标题")
    private String textSubtitle;

    /**
     * 摘要
     */
    @Schema(description = "摘要")
    private String textSummary;

    /**
     * 文本内容
     */
    @Schema(description = "文本内容")
    private String textContent;

    /**
     * 文本类型
     *
     * @see TextTypeEnum
     */
    @Schema(description = "文本类型")
    private String textType;

    /**
     * 内容格式（html:HTML, markdown:Markdown, text:纯文本）
     */
    @Schema(description = "内容格式")
    private String contentType;

    /**
     * 封面图片URL
     */
    @Schema(description = "封面图片URL")
    private String coverImage;

    /**
     * 作者
     */
    @Schema(description = "作者")
    private String author;

    /**
     * 来源
     */
    @Schema(description = "来源")
    private String source;

    /**
     * 原文链接
     */
    @Schema(description = "原文链接")
    private String sourceUrl;

    /**
     * 跳转链接
     */
    @Schema(description = "跳转链接")
    private String linkUrl;

    /**
     * 关键词（逗号分隔）
     */
    @Schema(description = "关键词")
    private String keywords;

    /**
     * 标签（逗号分隔）
     */
    @Schema(description = "标签")
    private String tags;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序")
    private Integer sort;

    /**
     * 是否置顶
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否置顶")
    private Integer top;

    /**
     * 是否推荐
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否推荐")
    private Integer recommend;

    /**
     * 状态
     *
     * @see TextStatusEnum
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数")
    private Integer likeCount;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    /**
     * 生效时间
     */
    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;

    /**
     * 失效时间
     */
    @Schema(description = "失效时间")
    private LocalDateTime expireTime;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;

    // ========== 必需标准字段 ==========

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 数据权限部门ID
     */
    @Schema(description = "数据权限部门ID")
    private Long deptId;

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
     * 逻辑删除标识
     *
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;

}
