package com.glowxq.triflow.base.captcha.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码 VO
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图片验证码")
public class CaptchaVO {

    @Schema(description = "验证码唯一标识")
    private String captchaKey;

    @Schema(description = "验证码图片（Base64编码）")
    private String captchaImage;
}
