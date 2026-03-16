package com.glowxq.common.ai.config;

import com.glowxq.common.ai.routing.ChatModelRouter;
import com.glowxq.common.ai.routing.RoutingChatModel;
import com.glowxq.common.ai.service.AiService;
import com.glowxq.common.ai.service.AiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

/**
 * AI 模块主自动配置
 * <p>
 * 通过 {@code triflow.ai.enabled=true} 启用，自动注册:
 * <ul>
 *   <li>{@link ChatModelRouter} - 多模型路由注册表</li>
 *   <li>{@link RoutingChatModel} - 可切换提供商的 ChatModel 代理</li>
 *   <li>{@link AiService} - 高阶 AI 门面服务</li>
 * </ul>
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = "triflow.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AiProperties.class)
@Import({ChatClientAutoConfiguration.class, ChatMemoryAutoConfiguration.class, ObservabilityAutoConfiguration.class})
public class AiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ChatModelRouter chatModelRouter(AiProperties properties,
                                           ObjectProvider<Map<String, ChatModel>> modelsProvider) {
        ChatModelRouter router = new ChatModelRouter(properties.getDefaultProvider());
        ChatClientAutoConfiguration.registerModels(router, modelsProvider);
        log.info("AI 模块初始化完成, 默认提供商: {}, 可用提供商: {}",
                properties.getDefaultProvider(), router.getAvailableProviders());
        return router;
    }

    @Bean
    @ConditionalOnMissingBean
    public RoutingChatModel routingChatModel(@Lazy ChatModelRouter router) {
        return new RoutingChatModel(router);
    }

    @Bean
    @ConditionalOnMissingBean(AiService.class)
    public AiService aiService(ChatModelRouter router, ChatMemory chatMemory, AiProperties properties) {
        return new AiServiceImpl(router, chatMemory, properties);
    }
}
