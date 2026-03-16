package com.glowxq.triflow.base.wallet.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.mysql.listener.EntityChangeListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 钱包签到记录实体
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Table(value = "wallet_signin", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "钱包签到记录")
public class WalletSignIn implements BaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "签到日期")
    private LocalDate signDate;

    @Schema(description = "获得积分")
    private Long points;

    @Schema(description = "连续签到天数")
    private Integer consecutiveDays;

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
