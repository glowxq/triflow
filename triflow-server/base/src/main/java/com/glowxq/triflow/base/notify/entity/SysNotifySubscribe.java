package com.glowxq.triflow.base.notify.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息订阅实体
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Table("sys_notify_subscribe")
@Schema(description = "消息订阅")
public class SysNotifySubscribe implements BaseEntity {

    /** 主键ID */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long userId;

    /** 模板ID（服务商模板ID） */
    @Schema(description = "模板ID")
    private String templateId;

    /** 模板标识 */
    @Schema(description = "模板标识")
    private String templateKey;

    /** 通知渠道 */
    @Schema(description = "通知渠道")
    private String channel;

    /** 订阅状态 */
    @Schema(description = "订阅状态")
    private String subscribeStatus;

    /** 订阅时间 */
    @Schema(description = "订阅时间")
    private LocalDateTime subscribeTime;

    // ========== 必需标准字段 ==========

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

    /** 数据权限部门ID */
    @Schema(description = "数据权限部门ID")
    private Long deptId;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    private Long createBy;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新者ID */
    @Schema(description = "更新者ID")
    private Long updateBy;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /** 逻辑删除标识 */
    @Schema(description = "删除标识")
    private Integer deleted;

}
