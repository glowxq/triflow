package com.glowxq.triflow.base.wallet.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.mysql.listener.EntityChangeListener;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包充值配置实体
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Table(value = "wallet_recharge_config", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "钱包充值配置")
public class WalletRechargeConfig implements BaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "配置名称")
    private String configName;

    /**
     * @see WalletType
     */
    @Schema(description = "充值类型")
    private String type;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "到账金额/积分")
    private BigDecimal rewardAmount;

    @Schema(description = "赠送金额/积分")
    private BigDecimal bonusAmount;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "数据权限部门ID")
    private Long deptId;

    @Schema(description = "创建者ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;
}
