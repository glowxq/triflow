package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.system.entity.SysUser;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情响应 VO
 * <p>
 * 用于用户详情展示，包含角色信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "用户详情响应")
@AutoMapper(target = SysUser.class)
public class SysUserDetailVO implements BaseVO {

    /** 用户ID */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /** 用户名 */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 昵称 */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    /** 真实姓名 */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /** 头像URL */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    /** 性别 */
    @Schema(description = "性别", example = "1")
    private Integer gender;

    /** 手机号 */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /** 所属部门ID */
    @Schema(description = "所属部门ID", example = "1")
    private Long deptId;

    /** 数据权限范围 */
    @Schema(description = "数据权限范围", example = "UserCreate")
    private String dataScope;

    /** 首页路径 */
    @Schema(description = "首页路径")
    private String homePath;

    /** 状态 */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 可用积分 */
    @Schema(description = "可用积分", example = "1000")
    private Long points;

    /** 可用余额 */
    @Schema(description = "可用余额", example = "100.00")
    private BigDecimal balance;

    /** 冻结积分 */
    @Schema(description = "冻结积分", example = "0")
    private Long frozenPoints;

    /** 冻结余额 */
    @Schema(description = "冻结余额", example = "0.00")
    private BigDecimal frozenBalance;

    /** 用户类型 */
    @Schema(description = "用户类型", example = "0")
    private Integer userType;

    /** 注册来源 */
    @Schema(description = "注册来源", example = "username")
    private String registerSource;

    /** 最后登录IP */
    @Schema(description = "最后登录IP")
    private String loginIp;

    /** 最后登录时间 */
    @Schema(description = "最后登录时间")
    private LocalDateTime loginTime;

    /** 登录次数 */
    @Schema(description = "登录次数")
    private Integer loginCount;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ========== 扩展字段 ==========

    /** 角色ID列表 */
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;

    /** 角色名称列表 */
    @Schema(description = "角色名称列表")
    private List<String> roleNames;

}
