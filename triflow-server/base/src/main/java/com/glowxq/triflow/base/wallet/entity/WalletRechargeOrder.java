package com.glowxq.triflow.base.wallet.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.mysql.listener.EntityChangeListener;
import com.glowxq.triflow.base.wallet.enums.WalletRechargeStatus;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包充值订单实体
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Table(value = "wallet_recharge_order", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "钱包充值订单")
public class WalletRechargeOrder implements BaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "充值配置ID")
    private Long configId;

    /**
     * @see WalletType
     */
    @Schema(description = "充值类型")
    private String type;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "到账金额/积分")
    private BigDecimal rewardAmount;

    /**
     * @see WalletRechargeStatus
     */
    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "微信支付订单号")
    private String wechatTransactionId;

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

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "数据权限部门ID")
    private Long deptId;

    @Schema(description = "创建者ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;
}
