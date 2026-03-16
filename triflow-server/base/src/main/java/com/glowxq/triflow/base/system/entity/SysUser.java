package com.glowxq.triflow.base.system.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.system.enums.GenderEnum;
import com.glowxq.triflow.base.system.enums.UserStatusEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>
 * 对应数据库表 sys_user，支持多种登录方式：账号密码、手机号、邮箱、第三方登录。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Table("sys_user")
@Schema(description = "系统用户")
public class SysUser implements BaseEntity {

    /** 用户ID（主键，自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "用户ID")
    private Long id;

    // ========== 基本信息 ==========

    /** 用户名（登录账号） */
    @Schema(description = "用户名")
    private String username;

    /** 密码（加密存储） */
    @Schema(description = "密码")
    private String password;

    /**
     * 是否设置密码
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否设置密码")
    private Integer passwordSet;

    /** 用户昵称 */
    @Schema(description = "昵称")
    private String nickname;

    /** 真实姓名 */
    @Schema(description = "真实姓名")
    private String realName;

    /** 头像URL */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 性别
     *
     * @see GenderEnum
     */
    @Schema(description = "性别")
    private Integer gender;

    // ========== 联系方式 ==========

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    // ========== 组织信息 ==========

    /** 所属部门ID */
    @Schema(description = "所属部门ID")
    private Long deptId;

    /**
     * 数据权限范围
     *
     * @see com.glowxq.common.core.common.enums.DataScopeEnum
     */
    @Schema(description = "数据权限范围")
    private String dataScope;

    /** 登录后首页路径 */
    @Schema(description = "首页路径")
    private String homePath;

    // ========== 状态信息 ==========

    /**
     * 状态
     *
     * @see UserStatusEnum
     */
    @Schema(description = "状态")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 个人简介 */
    @Schema(description = "个人简介")
    private String introduction;

    // ========== 积分 ==========

    /** 可用积分 */
    @Schema(description = "可用积分")
    private Long points;

    /** 冻结积分 */
    @Schema(description = "冻结积分")
    private Long frozenPoints;

    /** 奖励积分（非充值获得的积分统计，如签到、赠送、活动奖励等） */
    @Schema(description = "奖励积分")
    private Long rewardPoints;

    // ========== 标准字段 ==========

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

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
