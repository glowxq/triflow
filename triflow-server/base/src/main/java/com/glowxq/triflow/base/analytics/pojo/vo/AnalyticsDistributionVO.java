package com.glowxq.triflow.base.analytics.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据分布统计 VO
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Data
@Schema(description = "数据分布统计")
public class AnalyticsDistributionVO {

    @Schema(description = "用户角色分布")
    private List<DistributionItem> userRoleDistribution;

    @Schema(description = "交易类型分布")
    private List<DistributionItem> transactionTypeDistribution;

    @Schema(description = "订单状态分布")
    private List<DistributionItem> orderStatusDistribution;

    /**
     * 分布项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分布项")
    public static class DistributionItem {
        @Schema(description = "名称")
        private String name;

        @Schema(description = "数值")
        private Long value;
    }
}
