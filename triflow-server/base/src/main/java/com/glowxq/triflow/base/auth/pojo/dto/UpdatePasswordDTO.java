package com.glowxq.triflow.base.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改密码 DTO
 *
 * @author glowxq
 * @since 2025-02-03
 */
@Data
@Schema(description = "修改密码请求")
public class UpdatePasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 旧密码（已设置过密码时必填） */
    @Schema(description = "旧密码", example = "oldPassword")
    private String oldPassword;

    /** 新密码 */
    @Schema(description = "新密码", example = "newPassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度必须在6-32字符之间")
    private String newPassword;

    /** 确认密码 */
    @Schema(description = "确认密码", example = "newPassword123")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
