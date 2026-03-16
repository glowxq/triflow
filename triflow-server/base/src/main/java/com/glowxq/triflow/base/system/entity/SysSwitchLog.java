package com.glowxq.triflow.base.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 开关操作日志实体类
 * <p>
 * 对应数据库表 sys_switch_log，记录开关状态变更历史。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Table("sys_switch_log")
@Schema(description = "开关操作日志")
public class SysSwitchLog {

    /** 日志ID（主键，自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "日志ID")
    private Long id;

    /** 开关ID */
    @Schema(description = "开关ID")
    private Long switchId;

    /** 开关键 */
    @Schema(description = "开关键")
    private String switchKey;

    /** 变更前状态 */
    @Schema(description = "变更前状态")
    private Integer oldValue;

    /** 变更后状态 */
    @Schema(description = "变更后状态")
    private Integer newValue;

    /** 变更前配置（JSON格式） */
    @Schema(description = "变更前配置")
    private String oldConfig;

    /** 变更后配置（JSON格式） */
    @Schema(description = "变更后配置")
    private String newConfig;

    /** 变更类型（manual:手动, schedule:定时, auto:自动） */
    @Schema(description = "变更类型")
    private String changeType;

    /** 变更原因 */
    @Schema(description = "变更原因")
    private String changeReason;

    /** 操作人ID */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

}
