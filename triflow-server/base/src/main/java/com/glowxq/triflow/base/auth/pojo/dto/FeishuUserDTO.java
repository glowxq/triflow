package com.glowxq.triflow.base.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 飞书用户信息 DTO
 * <p>
 * 用于存储从飞书获取的用户信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@Schema(description = "飞书用户信息")
public class FeishuUserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 飞书用户唯一标识 (open_id) */
    @Schema(description = "飞书用户 Open ID")
    private String openId;

    /** 飞书用户 Union ID（跨应用唯一） */
    @Schema(description = "飞书用户 Union ID")
    private String unionId;

    /** 用户姓名 */
    @Schema(description = "用户姓名")
    private String name;

    /** 用户英文名 */
    @Schema(description = "用户英文名")
    private String enName;

    /** 用户头像 URL */
    @Schema(description = "用户头像 URL")
    private String avatarUrl;

    /** 用户头像缩略图 URL */
    @Schema(description = "用户头像缩略图 URL")
    private String avatarThumb;

    /** 用户头像中等尺寸 URL */
    @Schema(description = "用户头像中等尺寸 URL")
    private String avatarMiddle;

    /** 用户头像大尺寸 URL */
    @Schema(description = "用户头像大尺寸 URL")
    private String avatarBig;

    /** 用户邮箱 */
    @Schema(description = "用户邮箱")
    private String email;

    /** 用户企业邮箱 */
    @Schema(description = "用户企业邮箱")
    private String enterpriseEmail;

    /** 用户手机号 */
    @Schema(description = "用户手机号")
    private String mobile;

    /** 租户 Key */
    @Schema(description = "租户 Key")
    private String tenantKey;

    /** 用户访问令牌 */
    @Schema(description = "用户访问令牌")
    private String accessToken;

    /** 用户访问令牌过期时间（秒） */
    @Schema(description = "用户访问令牌过期时间")
    private Integer expiresIn;

    /** 刷新令牌 */
    @Schema(description = "刷新令牌")
    private String refreshToken;

}
