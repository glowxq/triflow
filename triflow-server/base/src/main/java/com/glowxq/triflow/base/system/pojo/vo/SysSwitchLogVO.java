package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.system.entity.SysSwitchLog;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 开关操作日志响应 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "开关操作日志响应")
@AutoMapper(target = SysSwitchLog.class)
public class SysSwitchLogVO implements BaseVO {

    /** 日志ID */
    @Schema(description = "日志ID", example = "1")
    private Long id;

    /** 开关ID */
    @Schema(description = "开关ID", example = "1")
    private Long switchId;

    /** 开关键 */
    @Schema(description = "开关键", example = "user.register.enabled")
    private String switchKey;

    /** 变更前状态 */
    @Schema(description = "变更前状态", example = "0")
    private Integer oldValue;

    /** 变更后状态 */
    @Schema(description = "变更后状态", example = "1")
    private Integer newValue;

    /** 变更前配置 */
    @Schema(description = "变更前配置")
    private String oldConfig;

    /** 变更后配置 */
    @Schema(description = "变更后配置")
    private String newConfig;

    /** 变更类型 */
    @Schema(description = "变更类型（manual:手动, schedule:定时, auto:自动）", example = "manual")
    private String changeType;

    /** 变更原因 */
    @Schema(description = "变更原因")
    private String changeReason;

    /** 操作人ID */
    @Schema(description = "操作人ID", example = "1")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名", example = "admin")
    private String operatorName;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

}
