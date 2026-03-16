package com.glowxq.common.ai.config;

import com.glowxq.common.ai.advisor.RequestLoggingAdvisor;
import com.glowxq.common.ai.advisor.TokenTrackingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * AI 可观测性自动配置
 * <p>
 * 配置日志记录和 Token 追踪 Advisor。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@AutoConfiguration
public class ObservabilityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TokenTrackingAdvisor tokenTrackingAdvisor() {
        log.info("初始化 Token 追踪 Advisor");
        return new TokenTrackingAdvisor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "triflow.ai.logging.enabled", havingValue = "true", matchIfMissing = true)
    public RequestLoggingAdvisor requestLoggingAdvisor(AiProperties properties) {
        boolean includePrompt = properties.getLogging().isIncludePrompt();
        boolean includeCompletion = properties.getLogging().isIncludeCompletion();
        log.info("初始化请求日志 Advisor, includePrompt={}, includeCompletion={}", includePrompt, includeCompletion);
        return new RequestLoggingAdvisor(includePrompt, includeCompletion);
    }
}
