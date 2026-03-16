package com.glowxq.triflow.base.analytics.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 数据分析概览 VO
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Data
@Schema(description = "数据分析概览")
public class AnalyticsOverviewVO {

    // ========== 用户统计 ==========

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "今日新增用户")
    private Long todayNewUsers;

    // ========== 订单统计 ==========

    @Schema(description = "总订单数")
    private Long totalOrders;

    @Schema(description = "今日订单数")
    private Long todayOrders;

    // ========== 交易统计 ==========

    @Schema(description = "总交易流水")
    private BigDecimal totalTransactionAmount;

    @Schema(description = "今日交易流水")
    private BigDecimal todayTransactionAmount;

    // ========== 积分统计 ==========

    @Schema(description = "总积分发放")
    private Long totalPointsIssued;

    @Schema(description = "今日积分发放")
    private Long todayPointsIssued;

    // ========== AI 使用统计 ==========

    @Schema(description = "总 AI 调用次数")
    private Long totalAiCalls;

    @Schema(description = "今日 AI 调用次数")
    private Long todayAiCalls;

    @Schema(description = "总 token 消耗")
    private Long totalAiTokens;

    @Schema(description = "今日 token 消耗")
    private Long todayAiTokens;
}
