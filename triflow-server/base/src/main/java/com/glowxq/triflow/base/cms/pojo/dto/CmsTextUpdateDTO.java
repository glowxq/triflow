package com.glowxq.triflow.base.cms.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.cms.entity.CmsText;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文本内容更新请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文本内容更新请求")
@AutoMapper(target = CmsText.class)
public class CmsTextUpdateDTO implements BaseDTO {

    @Schema(description = "文本ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文本ID不能为空")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "文本标识")
    @Size(max = 100, message = "文本标识长度不能超过100字符")
    private String textKey;

    @Schema(description = "文本标题")
    @Size(max = 200, message = "文本标题长度不能超过200字符")
    private String textTitle;

    @Schema(description = "副标题")
    @Size(max = 200, message = "副标题长度不能超过200字符")
    private String textSubtitle;

    @Schema(description = "摘要")
    @Size(max = 500, message = "摘要长度不能超过500字符")
    private String textSummary;

    @Schema(description = "文本内容")
    private String textContent;

    @Schema(description = "文本类型")
    private String textType;

    @Schema(description = "内容格式")
    private String contentType;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "原文链接")
    private String sourceUrl;

    @Schema(description = "跳转链接")
    private String linkUrl;

    @Schema(description = "关键词")
    private String keywords;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "显示排序")
    private Integer sort;

    @Schema(description = "是否置顶")
    private Integer top;

    @Schema(description = "是否推荐")
    private Integer recommend;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;

    @Schema(description = "失效时间")
    private LocalDateTime expireTime;

    @Schema(description = "备注说明")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, CmsText.class);
    }

}
