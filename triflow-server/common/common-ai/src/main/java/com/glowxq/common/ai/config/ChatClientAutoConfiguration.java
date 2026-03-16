package com.glowxq.common.ai.config;

import com.glowxq.common.ai.routing.ChatModelRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.util.Map;

/**
 * ChatModel 自动注册配置
 * <p>
 * 自动检测 Spring 容器中的 ChatModel Bean，
 * 按类名推断提供商名称并注册到 {@link ChatModelRouter}。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@AutoConfiguration
@ConditionalOnBean(ChatModel.class)
public class ChatClientAutoConfiguration {

    /**
     * 自动发现并注册所有 ChatModel Bean 到路由器
     */
    public static void registerModels(ChatModelRouter router, ObjectProvider<Map<String, ChatModel>> modelsProvider) {
        modelsProvider.ifAvailable(models -> {
            for (Map.Entry<String, ChatModel> entry : models.entrySet()) {
                String beanName = entry.getKey();
                ChatModel model = entry.getValue();
                String providerName = inferProviderName(model.getClass().getSimpleName(), beanName);
                router.register(providerName, model);
            }
        });
    }

    /**
     * 从 ChatModel 类名推断提供商名称
     * <p>
     * 映射规则:
     * <ul>
     *   <li>DeepSeekChatModel -> deepseek</li>
     *   <li>OpenAiChatModel -> openai</li>
     *   <li>ZhiPuAiChatModel -> zhipuai</li>
     *   <li>AnthropicChatModel -> anthropic</li>
     *   <li>OllamaChatModel -> ollama</li>
     * </ul>
     */
    static String inferProviderName(String className, String beanName) {
        String lower = className.toLowerCase();
        if (lower.contains("deepseek")) return "deepseek";
        if (lower.contains("openai")) return "openai";
        if (lower.contains("zhipuai") || lower.contains("zhipu")) return "zhipuai";
        if (lower.contains("anthropic")) return "anthropic";
        if (lower.contains("ollama")) return "ollama";
        // 回退到 bean 名称
        return beanName.toLowerCase().replace("chatmodel", "").replace("model", "");
    }
}
