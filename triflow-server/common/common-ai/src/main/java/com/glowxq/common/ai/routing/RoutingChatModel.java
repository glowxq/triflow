package com.glowxq.common.ai.routing;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

/**
 * 可切换提供商的 ChatModel 代理
 * <p>
 * 通过 ThreadLocal 路由键支持临时切换提供商，
 * 默认代理到 {@link ChatModelRouter} 的默认模型。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@RequiredArgsConstructor
public class RoutingChatModel implements ChatModel {

    private static final ThreadLocal<String> ROUTE_KEY = new ThreadLocal<>();

    private final ChatModelRouter router;

    /**
     * 临时切换提供商 (需在同一线程内使用)
     * <p>
     * 使用示例:
     * <pre>
     * try (var ignored = routingChatModel.withProvider("deepseek")) {
     *     chatClient.prompt().user("你好").call().content();
     * }
     * </pre>
     *
     * @param provider 提供商名称
     * @return AutoCloseable，用于自动清理 ThreadLocal
     */
    public ProviderScope withProvider(String provider) {
        ROUTE_KEY.set(provider);
        return new ProviderScope();
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        return resolve().call(prompt);
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return resolve().stream(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return resolve().getDefaultOptions();
    }

    private ChatModel resolve() {
        String key = ROUTE_KEY.get();
        if (key != null) {
            return router.getModel(key);
        }
        return router.getDefaultModel();
    }

    /**
     * 提供商作用域，用于自动清理 ThreadLocal
     */
    public static class ProviderScope implements AutoCloseable {

        @Override
        public void close() {
            ROUTE_KEY.remove();
        }
    }
}
