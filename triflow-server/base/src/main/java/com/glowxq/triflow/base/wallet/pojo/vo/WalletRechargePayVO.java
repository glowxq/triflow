package com.glowxq.triflow.base.wallet.pojo.vo;

import com.glowxq.common.wechat.pay.pojo.WechatPaymentData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 钱包充值支付结果 VO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包充值支付结果")
public class WalletRechargePayVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "充值类型")
    private String type;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "到账金额")
    private BigDecimal rewardAmount;

    @Schema(description = "微信支付参数")
    private WechatPaymentData paymentData;
}
