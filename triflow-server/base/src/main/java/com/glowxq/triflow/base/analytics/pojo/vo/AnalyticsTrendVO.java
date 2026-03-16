package com.glowxq.triflow.base.analytics.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 数据分析趋势 VO
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Data
@Schema(description = "数据分析趋势")
public class AnalyticsTrendVO {

    @Schema(description = "日期列表")
    private List<String> dates;

    @Schema(description = "用户注册趋势")
    private List<Long> userRegistrations;

    @Schema(description = "订单数量趋势")
    private List<Long> orderCounts;

    @Schema(description = "交易金额趋势")
    private List<BigDecimal> transactionAmounts;

    /**
     * 数据点
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "趋势数据点")
    public static class TrendPoint {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "数值")
        private Long value;
    }
}
