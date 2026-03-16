package com.glowxq.triflow.base.cms.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文本内容查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文本内容查询请求")
public class CmsTextQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "文本类型")
    private String textType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否置顶")
    private Integer top;

    @Schema(description = "是否推荐")
    private Integer recommend;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

}
