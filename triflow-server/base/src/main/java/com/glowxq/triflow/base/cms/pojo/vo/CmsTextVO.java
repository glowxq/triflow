package com.glowxq.triflow.base.cms.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.cms.entity.CmsText;
import com.glowxq.triflow.base.cms.enums.TextStatusEnum;
import com.glowxq.triflow.base.cms.enums.TextTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文本内容响应 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文本内容响应")
@AutoMapper(target = CmsText.class)
public class CmsTextVO implements BaseVO {

    /**
     * 文本ID
     */
    @Schema(description = "文本ID", example = "1")
    private Long id;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称", example = "公告")
    private String categoryName;

    /**
     * 文本标识
     */
    @Schema(description = "文本标识", example = "privacy-policy")
    private String textKey;

    /**
     * 文本标题
     */
    @Schema(description = "文本标题", example = "隐私政策")
    private String textTitle;

    /**
     * 副标题
     */
    @Schema(description = "副标题", example = "用户隐私保护说明")
    private String textSubtitle;

    /**
     * 摘要
     */
    @Schema(description = "摘要", example = "本政策说明了我们如何收集、使用您的个人信息...")
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
    @Schema(description = "文本类型", example = "article")
    private String textType;

    /**
     * 内容格式
     */
    @Schema(description = "内容格式", example = "html")
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
    @Schema(description = "显示排序", example = "0")
    private Integer sort;

    /**
     * 是否置顶
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否置顶", example = "0")
    private Integer top;

    /**
     * 是否推荐
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否推荐", example = "0")
    private Integer recommend;

    /**
     * 状态
     *
     * @see TextStatusEnum
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数", example = "100")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数", example = "10")
    private Integer likeCount;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    /**
     * 版本号
     */
    @Schema(description = "版本号", example = "1")
    private Integer version;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
