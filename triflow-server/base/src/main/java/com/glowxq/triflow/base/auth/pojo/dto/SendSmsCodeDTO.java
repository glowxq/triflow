package com.glowxq.triflow.base.auth.pojo.dto;

import com.glowxq.triflow.base.auth.enums.SmsCodeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送短信验证码 DTO
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@Schema(description = "发送短信验证码请求")
public class SendSmsCodeDTO implements Serializable {

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
     * 验证码类型
     *
     * @see SmsCodeTypeEnum
     */
    @NotNull(message = "验证码类型不能为空")
    @Schema(description = "验证码类型", required = true, example = "register", allowableValues = {"login", "register", "reset"})
    private SmsCodeTypeEnum type;

}
