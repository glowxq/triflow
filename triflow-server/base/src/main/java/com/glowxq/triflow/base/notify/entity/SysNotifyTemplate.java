package com.glowxq.triflow.base.notify.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息通知模板实体
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Table("sys_notify_template")
@Schema(description = "消息通知模板")
public class SysNotifyTemplate implements BaseEntity {

    /** 模板ID */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "模板ID")
    private Long id;

    /** 模板标识 */
    @Schema(description = "模板标识")
    private String templateKey;

    /** 模板名称 */
    @Schema(description = "模板名称")
    private String templateName;

    /** 模板ID（服务商模板ID） */
    @Schema(description = "模板ID")
    private String templateId;

    /** 通知渠道 */
    @Schema(description = "通知渠道")
    private String channel;

    /** 排序 */
    @Schema(description = "排序")
    private Integer sort;

    /** 状态 */
    @Schema(description = "状态")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

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
