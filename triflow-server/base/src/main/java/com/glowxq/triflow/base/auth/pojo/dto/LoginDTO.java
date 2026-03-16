package com.glowxq.triflow.base.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求 DTO
 * <p>
 * 支持多种登录方式：账号密码、手机号、第三方登录等。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "登录请求")
public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 授权类型（password, sms, wechat_miniapp, feishu, google），默认 password */
    @Schema(description = "授权类型", example = "password", defaultValue = "password")
    private String grantType = "password";

    // ========== 验证码 ==========

    /** 验证码Key */
    @Schema(description = "验证码Key", example = "captcha:123456")
    private String captchaKey;

    /** 验证码 */
    @Schema(description = "验证码", example = "ABCD")
    private String captchaCode;

    /** 验证码是否已通过（前端滑块） */
    @Schema(description = "验证码是否通过", example = "true")
    private Boolean captcha;

    // ========== 账号密码登录 ==========

    /** 用户名 */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 密码 */
    @Schema(description = "密码", example = "123456")
    private String password;

    // ========== 手机号登录 ==========

    /** 手机号 */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 短信验证码 */
    @Schema(description = "短信验证码", example = "123456")
    private String smsCode;

    // ========== 第三方登录 ==========

    /** 第三方授权码 */
    @Schema(description = "第三方授权码")
    private String code;

    /** 第三方平台类型 */
    @Schema(description = "第三方平台类型", example = "wechat_miniapp")
    private String socialType;

    // ========== 微信小程序登录 ==========

    /** 微信小程序加密数据 */
    @Schema(description = "微信加密数据")
    private String encryptedData;

    /** 微信小程序IV */
    @Schema(description = "微信IV")
    private String iv;

}
