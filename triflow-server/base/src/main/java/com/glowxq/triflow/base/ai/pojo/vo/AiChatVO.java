package com.glowxq.triflow.base.ai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 聊天响应 VO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 聊天响应")
public class AiChatVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应 ID
     */
    @Schema(description = "响应 ID", example = "chatcmpl-xxx")
    private String id;

    /**
     * 使用的提供商
     */
    @Schema(description = "使用的提供商", example = "glm")
    private String provider;

    /**
     * 使用的模型
     */
    @Schema(description = "使用的模型", example = "glm-4-flash")
    private String model;

    /**
     * AI 响应内容
     */
    @Schema(description = "AI 响应内容", example = "你好！有什么我可以帮助你的吗？")
    private String content;

    /**
     * 结束原因
     */
    @Schema(description = "结束原因", example = "stop")
    private String finishReason;

    /**
     * 提示 token 数
     */
    @Schema(description = "提示 token 数", example = "10")
    private Integer promptTokens;

    /**
     * 完成 token 数
     */
    @Schema(description = "完成 token 数", example = "50")
    private Integer completionTokens;

    /**
     * 总 token 数
     */
    @Schema(description = "总 token 数", example = "60")
    private Integer totalTokens;
}
