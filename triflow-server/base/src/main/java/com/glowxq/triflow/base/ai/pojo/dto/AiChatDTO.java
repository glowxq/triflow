package com.glowxq.triflow.base.ai.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 聊天请求 DTO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Schema(description = "AI 聊天请求")
public class AiChatDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * AI 提供商 (glm, deepseek, qianwen, claude)，不传则使用默认
     */
    @Schema(description = "AI 提供商", example = "glm")
    private String provider;

    /**
     * 模型名称，不传则使用提供商默认模型
     */
    @Schema(description = "模型名称", example = "glm-4-flash")
    private String model;

    /**
     * 系统提示
     */
    @Schema(description = "系统提示", example = "你是一个有帮助的助手")
    private String systemPrompt;

    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "用户消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好")
    private String message;

    /**
     * 温度 (0-2)
     */
    @Schema(description = "温度 (0-2)", example = "0.7")
    private Double temperature;

    /**
     * 最大生成 token 数
     */
    @Schema(description = "最大生成 token 数", example = "2048")
    private Integer maxTokens;
}
