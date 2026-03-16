package com.glowxq.triflow.base.wallet.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.mysql.listener.EntityChangeListener;
import com.glowxq.triflow.base.wallet.enums.WalletAction;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包变动记录实体
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Table(value = "wallet_transaction", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "钱包变动记录")
public class WalletTransaction implements BaseEntity {

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

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

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 数据权限部门ID
     */
    @Schema(description = "数据权限部门ID")
    private Long deptId;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long createBy;

    /**
     * 更新者ID
     */
    @Schema(description = "更新者ID")
    private Long updateBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     *
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;
}
