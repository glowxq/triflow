package com.glowxq.triflow.base.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 绑定第三方账号 DTO
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@Schema(description = "绑定第三方账号请求")
public class BindSocialDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 授权码 */
    @NotBlank(message = "授权码不能为空")
    @Schema(description = "第三方授权码", required = true)
    private String code;

    /**
     * 是否强制绑定
     * <p>
     * 如果第三方账号已被其他用户绑定，设置为 true 会解除原绑定，绑定到当前用户。
     * </p>
     */
    @Schema(description = "是否强制绑定（解除原账号绑定）", example = "false")
    private Boolean force = false;

}
