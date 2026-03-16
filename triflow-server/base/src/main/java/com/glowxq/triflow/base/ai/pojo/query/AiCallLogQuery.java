package com.glowxq.triflow.base.ai.pojo.query;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI 调用记录查询参数
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI 调用记录查询参数")
public class AiCallLogQuery extends PageQuery {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "AI 提供商")
    private String provider;

    @Schema(description = "模型")
    private String model;

    @Schema(description = "状态: 1-成功, 0-失败")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
