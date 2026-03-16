package com.glowxq.triflow.base.auth.pojo.vo;

import com.glowxq.triflow.base.system.enums.GenderEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 当前用户信息响应 VO
 * <p>
 * 对应前端获取用户信息的响应结构。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "当前用户信息响应")
public class UserInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /** 用户名 */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 真实姓名 */
    @Schema(description = "真实姓名", example = "管理员")
    private String realName;

    /** 昵称 */
    @Schema(description = "昵称", example = "小助手")
    private String nickname;

    /** 头像 */
    @Schema(description = "头像URL")
    private String avatar;

    /** 手机号 */
    @Schema(description = "手机号", example = "13800000000")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /**
     * 性别
     *
     * @see GenderEnum
     */
    @Schema(description = "性别", example = "0")
    private Integer gender;

    /** 描述/签名 */
    @Schema(description = "描述")
    private String desc;

    /** 首页路径 */
    @Schema(description = "首页路径", example = "/dashboard")
    private String homePath;

    /** 角色编码列表 */
    @Schema(description = "角色编码列表", example = "[\"admin\", \"user\"]")
    private List<String> roles;

    /** 是否已设置密码 */
    @Schema(description = "是否已设置密码", example = "true")
    private Boolean passwordSet;

    // ========== 积分 ==========

    /** 可用积分 */
    @Schema(description = "可用积分", example = "1000")
    private Long points;

    /** 冻结积分 */
    @Schema(description = "冻结积分", example = "0")
    private Long frozenPoints;

    /** 奖励积分（非充值获得的积分统计，如签到、赠送、活动奖励等） */
    @Schema(description = "奖励积分", example = "0")
    private Long rewardPoints;

}
