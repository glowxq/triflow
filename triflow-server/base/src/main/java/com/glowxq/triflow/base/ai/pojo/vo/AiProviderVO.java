package com.glowxq.triflow.base.ai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 提供商信息 VO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 提供商信息")
public class AiProviderVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提供商代码
     */
    @Schema(description = "提供商代码 (Spring AI 自动检测)", example = "deepseek")
    private String code;

    /**
     * 提供商名称
     */
    @Schema(description = "提供商名称", example = "deepseek")
    private String name;

    /**
     * 是否可用
     */
    @Schema(description = "是否可用", example = "true")
    private Boolean available;

    /**
     * 是否为默认提供商
     */
    @Schema(description = "是否为默认提供商", example = "true")
    private Boolean isDefault;

    /**
     * 默认模型
     */
    @Schema(description = "默认模型", example = "deepseek-chat")
    private String defaultModel;
}
