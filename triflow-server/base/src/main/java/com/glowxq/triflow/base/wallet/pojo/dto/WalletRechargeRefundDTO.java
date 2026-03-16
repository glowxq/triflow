package com.glowxq.triflow.base.wallet.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 充值订单退款 DTO
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Data
@Schema(description = "充值订单退款")
public class WalletRechargeRefundDTO {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @Schema(description = "退款原因")
    private String reason;
}
