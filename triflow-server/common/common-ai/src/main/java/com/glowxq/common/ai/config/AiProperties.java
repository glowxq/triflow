package com.glowxq.common.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 脚手架配置属性
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@ConfigurationProperties(prefix = "triflow.ai")
public class AiProperties {

    /** 是否启用 AI 功能 */
    private boolean enabled = true;

    /** 默认提供商名称 (如 deepseek, openai, zhipuai, anthropic, ollama) */
    private String defaultProvider = "deepseek";

    /** 对话记忆窗口大小 */
    private int memoryWindowSize = 20;

    /** 对话记忆存储方式: memory | redis */
    private String memoryStorage = "memory";

    /** Redis 配置 */
    private Redis redis = new Redis();

    /** 日志配置 */
    private Logging logging = new Logging();

    @Data
    public static class Redis {

        /** Redis key 前缀 */
        private String keyPrefix = "triflow:ai:memory:";

        /** 过期时间 (秒)，0 表示不过期 */
        private long timeToLive = 86400;
    }

    @Data
    public static class Logging {

        /** 是否启用请求日志 */
        private boolean enabled = true;

        /** 是否记录 prompt 内容 */
        private boolean includePrompt = false;

        /** 是否记录 completion 内容 */
        private boolean includeCompletion = false;
    }
}
