package com.glowxq.triflow.base.wallet.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 钱包签到配置更新 DTO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包签到配置更新")
public class WalletSignInConfigUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "签到开关不能为空")
    @Schema(description = "是否开启")
    private Boolean enabled;

    @NotNull(message = "签到积分不能为空")
    @Min(value = 0, message = "签到积分不能小于0")
    @Schema(description = "签到积分")
    private Long points;
}
