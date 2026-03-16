package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.system.entity.SysUser;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表响应 VO
 * <p>
 * 用于用户列表展示，不包含敏感信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "用户列表响应")
@AutoMapper(target = SysUser.class)
public class SysUserVO implements BaseVO {

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
    @Schema(description = "手机号", example = "138****8000")
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

    /** 状态 */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 可用积分 */
    @Schema(description = "可用积分", example = "1000")
    private Long points;

    /** 奖励积分（非充值获得的积分统计，如签到、赠送、活动奖励等） */
    @Schema(description = "奖励积分", example = "0")
    private Long rewardPoints;

    /** 最后登录时间 */
    @Schema(description = "最后登录时间")
    private LocalDateTime loginTime;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
