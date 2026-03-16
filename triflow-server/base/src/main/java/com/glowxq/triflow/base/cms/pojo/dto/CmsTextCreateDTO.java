package com.glowxq.triflow.base.cms.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.cms.entity.CmsText;
import com.glowxq.triflow.base.cms.enums.TextStatusEnum;
import com.glowxq.triflow.base.cms.enums.TextTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文本内容创建请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文本内容创建请求")
@AutoMapper(target = CmsText.class)
public class CmsTextCreateDTO implements BaseDTO {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /**
     * 文本标识
     */
    @Schema(description = "文本标识", example = "privacy-policy", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文本标识不能为空")
    @Size(max = 100, message = "文本标识长度不能超过100字符")
    private String textKey;

    /**
     * 文本标题
     */
    @Schema(description = "文本标题", example = "隐私政策", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文本标题不能为空")
    @Size(max = 200, message = "文本标题长度不能超过200字符")
    private String textTitle;

    /**
     * 副标题
     */
    @Schema(description = "副标题", example = "用户隐私保护说明")
    @Size(max = 200, message = "副标题长度不能超过200字符")
    private String textSubtitle;

    /**
     * 摘要
     */
    @Schema(description = "摘要", example = "本政策说明了我们如何收集、使用您的个人信息...")
    @Size(max = 500, message = "摘要长度不能超过500字符")
    private String textSummary;

    /**
     * 文本内容
     */
    @Schema(description = "文本内容", example = "<p>隐私政策内容</p>")
    private String textContent;

    /**
     * 文本类型
     *
     * @see TextTypeEnum
     */
    @Schema(description = "文本类型", example = "article", defaultValue = "article")
    private String textType;

    /**
     * 内容格式
     */
    @Schema(description = "内容格式", example = "html", defaultValue = "html")
    private String contentType;

    /**
     * 封面图片URL
     */
    @Schema(description = "封面图片URL", example = "https://cdn.example.com/cover.jpg")
    private String coverImage;

    /**
     * 作者
     */
    @Schema(description = "作者", example = "admin")
    private String author;

    /**
     * 来源
     */
    @Schema(description = "来源", example = "官方")
    private String source;

    /**
     * 原文链接
     */
    @Schema(description = "原文链接", example = "https://example.com/original")
    private String sourceUrl;

    /**
     * 跳转链接
     */
    @Schema(description = "跳转链接", example = "https://example.com/detail")
    private String linkUrl;

    /**
     * 关键词
     */
    @Schema(description = "关键词", example = "隐私,政策,用户")
    private String keywords;

    /**
     * 标签
     */
    @Schema(description = "标签", example = "法律,隐私")
    private String tags;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序", example = "0", defaultValue = "0")
    private Integer sort;

    /**
     * 是否置顶
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否置顶", example = "0", defaultValue = "0")
    private Integer top;

    /**
     * 是否推荐
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否推荐", example = "0", defaultValue = "0")
    private Integer recommend;

    /**
     * 状态
     *
     * @see TextStatusEnum
     */
    @Schema(description = "状态", example = "0", defaultValue = "0")
    private Integer status;

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
     * 备注说明
     */
    @Schema(description = "备注说明", example = "这是备注")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, CmsText.class);
    }

}
