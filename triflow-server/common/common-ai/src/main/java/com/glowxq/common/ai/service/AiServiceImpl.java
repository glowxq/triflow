package com.glowxq.common.ai.service;

import com.glowxq.common.ai.config.AiProperties;
import com.glowxq.common.ai.routing.ChatModelRouter;
import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.exception.common.AlertsException;
import com.glowxq.common.core.common.exception.common.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * AI 服务实现
 * <p>
 * 通过 {@link ChatModelRouter} 路由到目标提供商，
 * 使用 {@link ChatClient} 构建请求。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatModelRouter router;
    private final ChatMemory chatMemory;
    private final AiProperties properties;

    @Override
    public String chat(String userMessage) {
        return chat(null, null, userMessage);
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        return chat(null, systemPrompt, userMessage);
    }

    @Override
    public String chat(String provider, String systemPrompt, String userMessage) {
        try {
            ChatModel model = resolveModel(provider);
            ChatClient.Builder builder = ChatClient.builder(model);

            var prompt = builder.build().prompt();
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                prompt = prompt.system(systemPrompt);
            }
            return prompt.user(userMessage).call().content();
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 调用失败, provider={}", provider, e);
            throw new AlertsException(ErrorCodeEnum.AI_CALL_ERROR,
                    "AI 调用失败: " + e.getMessage());
        }
    }

    @Override
    public String chat(String conversationId, String provider, String systemPrompt, String userMessage) {
        try {
            ChatModel model = resolveModel(provider);
            ChatClient.Builder builder = ChatClient.builder(model)
                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build());

            var prompt = builder.build().prompt()
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId));

            if (systemPrompt != null && !systemPrompt.isBlank()) {
                prompt = prompt.system(systemPrompt);
            }
            return prompt.user(userMessage).call().content();
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 调用失败, conversationId={}, provider={}", conversationId, provider, e);
            throw new AlertsException(ErrorCodeEnum.AI_CALL_ERROR,
                    "AI 调用失败: " + e.getMessage());
        }
    }

    @Override
    public Flux<String> stream(String userMessage) {
        return stream(null, null, userMessage);
    }

    @Override
    public Flux<String> stream(String provider, String systemPrompt, String userMessage) {
        try {
            ChatModel model = resolveModel(provider);
            ChatClient.Builder builder = ChatClient.builder(model);

            var prompt = builder.build().prompt();
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                prompt = prompt.system(systemPrompt);
            }
            return prompt.user(userMessage).stream().content();
        } catch (BaseException e) {
            return Flux.error(e);
        } catch (Exception e) {
            log.error("AI 流式调用失败, provider={}", provider, e);
            return Flux.error(new AlertsException(ErrorCodeEnum.AI_STREAM_ERROR,
                    "AI 流式调用失败: " + e.getMessage()));
        }
    }

    @Override
    public <T> T chatForObject(String userMessage, Class<T> responseType) {
        return chatForObject(null, null, userMessage, responseType);
    }

    @Override
    public <T> T chatForObject(String provider, String systemPrompt, String userMessage, Class<T> responseType) {
        try {
            ChatModel model = resolveModel(provider);
            ChatClient.Builder builder = ChatClient.builder(model);

            var prompt = builder.build().prompt();
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                prompt = prompt.system(systemPrompt);
            }
            return prompt.user(userMessage).call().entity(responseType);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 结构化输出失败, provider={}", provider, e);
            throw new AlertsException(ErrorCodeEnum.AI_STRUCTURED_OUTPUT_ERROR,
                    "AI 结构化输出失败: " + e.getMessage());
        }
    }

    @Override
    public Set<String> getAvailableProviders() {
        return router.getAvailableProviders();
    }

    @Override
    public String getDefaultProvider() {
        return router.getDefaultProvider();
    }

    private ChatModel resolveModel(String provider) {
        if (provider == null || provider.isBlank()) {
            return router.getDefaultModel();
        }
        return router.getModel(provider);
    }
}
