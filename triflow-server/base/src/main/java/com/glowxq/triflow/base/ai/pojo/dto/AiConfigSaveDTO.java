package com.glowxq.triflow.base.ai.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 配置保存 DTO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Schema(description = "AI 配置保存参数")
public class AiConfigSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID (更新时必填)")
    private Long id;

    @NotBlank(message = "提供商代码不能为空")
    @Schema(description = "提供商代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "glm")
    private String provider;

    @Schema(description = "API Key")
    private String apiKey;

    @Schema(description = "API 端点")
    private String endpoint;

    @Schema(description = "默认模型")
    private String defaultModel;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    @Schema(description = "是否为默认提供商", example = "false")
    private Boolean isDefault = false;

    @Schema(description = "超时时间 (秒)", example = "120")
    private Integer timeout = 120;
}
