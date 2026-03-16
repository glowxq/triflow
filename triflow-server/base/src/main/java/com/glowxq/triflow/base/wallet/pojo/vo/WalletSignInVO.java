package com.glowxq.triflow.base.wallet.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.wallet.entity.WalletSignIn;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 钱包签到记录 VO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包签到记录")
@AutoMapper(target = WalletSignIn.class)
public class WalletSignInVO implements BaseVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "签到日期")
    private LocalDate signDate;

    @Schema(description = "获得积分")
    private Long points;

    @Schema(description = "连续签到天数")
    private Integer consecutiveDays;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
