package com.glowxq.triflow.base.wallet.pojo.query;

import com.glowxq.common.core.common.entity.PageQuery;
import com.glowxq.triflow.base.wallet.enums.WalletRechargeStatus;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 充值订单查询参数
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "充值订单查询参数")
public class WalletRechargeOrderQuery extends PageQuery {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "订单号")
    private String orderNo;

    /**
     * @see WalletType
     */
    @Schema(description = "充值类型")
    private String type;

    /**
     * @see WalletRechargeStatus
     */
    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
