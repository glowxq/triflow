package com.glowxq.triflow.base.ai.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 调用记录实体
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Data
@Table("ai_call_log")
public class AiCallLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * AI 提供商
     */
    private String provider;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 系统提示
     */
    private String systemPrompt;

    /**
     * 用户消息
     */
    private String userMessage;

    /**
     * AI 响应内容
     */
    private String aiResponse;

    /**
     * 提示 token 数
     */
    private Integer promptTokens;

    /**
     * 完成 token 数
     */
    private Integer completionTokens;

    /**
     * 总 token 数
     */
    private Integer totalTokens;

    /**
     * 耗时 (毫秒)
     */
    private Long duration;

    /**
     * 调用状态
     *
     * @see com.glowxq.triflow.base.ai.enums.AiCallStatusEnum
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
