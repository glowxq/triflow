package com.glowxq.triflow.base.wallet.pojo.query;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 钱包签到记录查询参数
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "钱包签到记录查询参数")
public class WalletSignInQuery extends PageQuery {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
