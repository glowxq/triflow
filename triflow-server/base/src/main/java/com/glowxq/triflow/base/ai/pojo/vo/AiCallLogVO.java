package com.glowxq.triflow.base.ai.pojo.vo;

import com.glowxq.triflow.base.ai.entity.AiCallLog;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 调用记录 VO
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Data
@Schema(description = "AI 调用记录")
@AutoMapper(target = AiCallLog.class)
public class AiCallLogVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "AI 提供商")
    private String provider;

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "使用的模型")
    private String model;

    @Schema(description = "系统提示")
    private String systemPrompt;

    @Schema(description = "用户消息")
    private String userMessage;

    @Schema(description = "AI 响应内容")
    private String aiResponse;

    @Schema(description = "提示 token 数")
    private Integer promptTokens;

    @Schema(description = "完成 token 数")
    private Integer completionTokens;

    @Schema(description = "总 token 数")
    private Integer totalTokens;

    @Schema(description = "耗时 (毫秒)")
    private Long duration;

    @Schema(description = "状态: 1-成功, 0-失败")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "请求IP")
    private String ip;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
