package com.glowxq.triflow.base.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册 DTO
 * <p>
 * 支持手机号+短信验证码注册
 * </p>
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码长度为4-6位")
    @Schema(description = "短信验证码", required = true, example = "123456")
    private String smsCode;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度为6-32位")
    @Schema(description = "密码", required = true)
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", required = true)
    private String confirmPassword;

    /**
     * 昵称（可选）
     */
    @Size(max = 30, message = "昵称长度不能超过30位")
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像（可选）
     */
    @Schema(description = "头像URL")
    private String avatar;
}
