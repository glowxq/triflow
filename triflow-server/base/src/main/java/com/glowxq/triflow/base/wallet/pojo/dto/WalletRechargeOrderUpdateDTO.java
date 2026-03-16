package com.glowxq.triflow.base.wallet.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeOrder;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 充值订单更新 DTO
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Data
@Schema(description = "充值订单更新")
@AutoMapper(target = WalletRechargeOrder.class)
public class WalletRechargeOrderUpdateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "到账金额")
    private BigDecimal rewardAmount;

    @Size(max = 500, message = "备注长度不能超过500字符")
    @Schema(description = "备注")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, WalletRechargeOrder.class);
    }
}
