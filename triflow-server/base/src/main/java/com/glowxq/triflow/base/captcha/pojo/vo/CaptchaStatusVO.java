package com.glowxq.triflow.base.captcha.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码状态 VO
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码状态")
public class CaptchaStatusVO {

    @Schema(description = "是否启用验证码")
    private Boolean captchaEnabled;
}
