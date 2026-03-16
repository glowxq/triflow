package com.glowxq.triflow.base.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 绑定微信手机号 DTO
 * <p>
 * 用于接收微信小程序获取手机号后的授权码
 * </p>
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@Schema(description = "绑定微信手机号请求")
public class BindWechatPhoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 微信手机号授权码
     * <p>
     * 从微信小程序 getPhoneNumber 接口获取的 code
     * </p>
     */
    @NotBlank(message = "授权码不能为空")
    @Schema(description = "微信手机号授权码", required = true)
    private String code;
}
