package com.glowxq.triflow.base.wallet.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.wallet.entity.WalletTransaction;
import com.glowxq.triflow.base.wallet.enums.WalletAction;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包变动记录 VO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Schema(description = "钱包变动记录")
@AutoMapper(target = WalletTransaction.class)
public class WalletTransactionVO implements BaseVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 交易类型
     *
     * @see WalletType
     */
    @Schema(description = "交易类型")
    private String type;

    /**
     * 交易类型描述
     */
    @Schema(description = "交易类型描述")
    private String typeDesc;

    /**
     * 操作类型
     *
     * @see WalletAction
     */
    @Schema(description = "操作类型")
    private String action;

    /**
     * 操作类型描述
     */
    @Schema(description = "操作类型描述")
    private String actionDesc;

    /**
     * 变动金额
     */
    @Schema(description = "变动金额")
    private BigDecimal amount;

    /**
     * 变动前金额
     */
    @Schema(description = "变动前金额")
    private BigDecimal beforeAmount;

    /**
     * 变动后金额
     */
    @Schema(description = "变动后金额")
    private BigDecimal afterAmount;

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
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;

    /**
     * 操作人ID
     */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /**
     * 操作人名称
     */
    @Schema(description = "操作人名称")
    private String operatorName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
