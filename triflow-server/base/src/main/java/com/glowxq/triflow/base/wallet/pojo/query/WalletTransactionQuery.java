package com.glowxq.triflow.base.wallet.pojo.query;

import com.glowxq.common.core.common.entity.PageQuery;
import com.glowxq.triflow.base.wallet.enums.WalletAction;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 钱包变动记录查询参数
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "钱包变动记录查询参数")
public class WalletTransactionQuery extends PageQuery {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名（模糊查询）
     */
    @Schema(description = "用户名（模糊查询）")
    private String username;

    /**
     * 交易类型
     *
     * @see WalletType
     */
    @Schema(description = "交易类型")
    private String type;

    /**
     * 操作类型
     *
     * @see WalletAction
     */
    @Schema(description = "操作类型")
    private String action;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private String bizId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
