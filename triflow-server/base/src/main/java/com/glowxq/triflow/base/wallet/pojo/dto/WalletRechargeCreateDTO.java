package com.glowxq.triflow.base.wallet.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 钱包充值下单 DTO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包充值下单")
public class WalletRechargeCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "充值配置ID不能为空")
    @Schema(description = "充值配置ID")
    private Long configId;
}
