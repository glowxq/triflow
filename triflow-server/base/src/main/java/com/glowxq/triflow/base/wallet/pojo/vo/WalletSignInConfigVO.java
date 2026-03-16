package com.glowxq.triflow.base.wallet.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 钱包签到配置 VO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包签到配置")
public class WalletSignInConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否开启")
    private boolean enabled;

    @Schema(description = "签到积分")
    private long points;
}
