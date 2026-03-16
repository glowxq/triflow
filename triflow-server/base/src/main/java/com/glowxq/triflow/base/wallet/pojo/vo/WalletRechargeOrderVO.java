package com.glowxq.triflow.base.wallet.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeOrder;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单 VO
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Data
@Schema(description = "钱包充值订单")
@AutoMapper(target = WalletRechargeOrder.class)
public class WalletRechargeOrderVO implements BaseVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "充值配置ID")
    private Long configId;

    @Schema(description = "充值配置名称")
    private String configName;

    @Schema(description = "充值类型")
    private String type;

    @Schema(description = "充值类型描述")
    private String typeDesc;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "到账金额")
    private BigDecimal rewardAmount;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "微信支付订单号")
    private String wechatTransactionId;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "退款单号")
    private String refundNo;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退款原因")
    private String refundReason;

    @Schema(description = "退款时间")
    private LocalDateTime refundTime;

    @Schema(description = "退款人ID")
    private Long refundBy;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
