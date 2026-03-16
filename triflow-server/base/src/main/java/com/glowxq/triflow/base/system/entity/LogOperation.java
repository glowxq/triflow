package com.glowxq.triflow.base.system.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * <p>
 * 对应数据库表 log_operation，记录用户关键操作行为，便于审计和问题追溯。
 * </p>
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Data
@Table("log_operation")
@Schema(description = "操作日志")
public class LogOperation implements BaseEntity {

    /** 日志ID（主键，自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "日志ID")
    private Long id;

    // ========== 操作信息 ==========

    /** 操作模块（如：system, user, role） */
    @Schema(description = "操作模块")
    private String module;

    /** 操作类型（如：create, update, delete, login） */
    @Schema(description = "操作类型")
    private String operation;

    /** 操作描述 */
    @Schema(description = "操作描述")
    private String description;

    // ========== 请求信息 ==========

    /** 请求方法（GET/POST/PUT/DELETE） */
    @Schema(description = "请求方法")
    private String method;

    /** 请求URL */
    @Schema(description = "请求URL")
    private String requestUrl;

    /** 请求参数（JSON） */
    @Schema(description = "请求参数")
    private String requestParams;

    /** 响应数据（JSON） */
    @Schema(description = "响应数据")
    private String responseData;

    // ========== 客户端信息 ==========

    /** 操作IP地址 */
    @Schema(description = "操作IP地址")
    private String ip;

    /** 用户代理 */
    @Schema(description = "用户代理")
    private String userAgent;

    // ========== 操作人信息 ==========

    /** 操作人ID */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    // ========== 执行结果 ==========

    /** 执行耗时（毫秒） */
    @Schema(description = "执行耗时（毫秒）")
    private Long duration;

    /** 操作状态（0:失败, 1:成功） */
    @Schema(description = "操作状态（0:失败, 1:成功）")
    private Integer status;

    /** 错误信息 */
    @Schema(description = "错误信息")
    private String errorMsg;

    // ========== 租户信息 ==========

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

    /** 部门ID */
    @Schema(description = "部门ID")
    private Long deptId;

}
