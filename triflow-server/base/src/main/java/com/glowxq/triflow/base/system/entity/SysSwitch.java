package com.glowxq.triflow.base.system.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.enums.SwitchScopeEnum;
import com.glowxq.triflow.base.system.enums.SwitchStrategyEnum;
import com.glowxq.triflow.base.system.enums.SwitchTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统开关实体类
 * <p>
 * 对应数据库表 sys_switch，用于管理系统功能的开关状态。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Table("sys_switch")
@Schema(description = "系统开关")
public class SysSwitch implements BaseEntity {

    /**
     * 开关ID（主键，自增）
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "开关ID")
    private Long id;

    /**
     * 开关名称
     */
    @Schema(description = "开关名称")
    private String switchName;

    /**
     * 开关键（唯一标识，如: feature.newUI.enabled）
     */
    @Schema(description = "开关键")
    private String switchKey;

    /**
     * 开关状态
     *
     * @see StatusEnum
     */
    @Schema(description = "开关状态")
    private Integer switchValue;

    /**
     * 开关类型
     *
     * @see SwitchTypeEnum
     */
    @Schema(description = "开关类型")
    private String switchType;

    /**
     * 开关分类（用于分组，如: user, order, payment）
     */
    @Schema(description = "开关分类")
    private String category;

    /**
     * 作用范围
     *
     * @see SwitchScopeEnum
     */
    @Schema(description = "作用范围")
    private String scope;

    /**
     * 生效策略
     *
     * @see SwitchStrategyEnum
     */
    @Schema(description = "生效策略")
    private String strategy;

    /**
     * 白名单配置（用户ID或租户ID列表，JSON格式）
     */
    @Schema(description = "白名单配置")
    private String whitelist;

    /**
     * 灰度百分比（0-100，strategy=percentage时生效）
     */
    @Schema(description = "灰度百分比")
    private Integer percentage;

    /**
     * 生效开始时间（strategy=schedule时生效）
     */
    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    /**
     * 生效结束时间（strategy=schedule时生效）
     */
    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序")
    private Integer sort;

    /**
     * 开关描述
     */
    @Schema(description = "开关描述")
    private String description;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
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

    /**
     * 逻辑删除标识
     *
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;

}
