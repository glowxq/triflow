package com.glowxq.triflow.base.auth.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 登录响应 VO
 * <p>
 * 对应前端期望的登录响应结构，包含 accessToken 和用户基本信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "登录响应")
public class LoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 访问令牌 */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    /** 刷新令牌（可选） */
    @Schema(description = "刷新令牌")
    private String refreshToken;

    /** 用户ID */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /** 用户名 */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 真实姓名 */
    @Schema(description = "真实姓名", example = "管理员")
    private String realName;

    /** 头像 */
    @Schema(description = "头像URL")
    private String avatar;

    /** 角色编码列表 */
    @Schema(description = "角色编码列表", example = "[\"admin\", \"user\"]")
    private List<String> roles;

    /** 首页路径 */
    @Schema(description = "首页路径", example = "/dashboard")
    private String homePath;

    /** 手机号（脱敏显示，用于前端判断是否需要收集手机号） */
    @Schema(description = "手机号（脱敏）", example = "138****8888")
    private String phone;

    /** 用户信息是否完整（有头像且有昵称） */
    @Schema(description = "用户信息是否完整", example = "true")
    private Boolean profileComplete;

}
