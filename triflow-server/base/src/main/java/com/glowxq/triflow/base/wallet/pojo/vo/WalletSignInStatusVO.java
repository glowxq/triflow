package com.glowxq.triflow.base.wallet.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 钱包签到状态 VO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包签到状态")
public class WalletSignInStatusVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "今日是否已签到")
    private boolean signedToday;

    @Schema(description = "连续签到天数")
    private int consecutiveDays;

    @Schema(description = "今日签到奖励积分")
    private long rewardPoints;

    @Schema(description = "最近一次签到日期")
    private LocalDate lastSignDate;
}
