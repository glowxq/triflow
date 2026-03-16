package com.glowxq.common.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * 请求/响应日志 Advisor
 * <p>
 * 根据配置决定是否记录 prompt 和 completion 内容，并记录请求耗时。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
public class RequestLoggingAdvisor implements CallAdvisor, StreamAdvisor {

    private final boolean includePrompt;
    private final boolean includeCompletion;

    public RequestLoggingAdvisor(boolean includePrompt, boolean includeCompletion) {
        this.includePrompt = includePrompt;
        this.includeCompletion = includeCompletion;
    }

    @Override
    public String getName() {
        return "RequestLoggingAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        long start = System.currentTimeMillis();

        if (includePrompt && log.isDebugEnabled()) {
            log.debug("[AI 请求] prompt: {}", request.prompt());
        }

        ChatClientResponse response = chain.nextCall(request);
        long duration = System.currentTimeMillis() - start;

        logResponse(response.chatResponse(), duration);
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        long start = System.currentTimeMillis();

        if (includePrompt && log.isDebugEnabled()) {
            log.debug("[AI 流式请求] prompt: {}", request.prompt());
        }

        return chain.nextStream(request)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - start;
                    log.info("[AI 流式响应] 耗时: {}ms", duration);
                });
    }

    private void logResponse(ChatResponse chatResponse, long duration) {
        if (chatResponse == null) {
            log.info("[AI 响应] 耗时: {}ms, 响应为空", duration);
            return;
        }

        String content = chatResponse.getResult() != null
                ? chatResponse.getResult().getOutput().getText()
                : null;

        if (includeCompletion && log.isDebugEnabled()) {
            log.debug("[AI 响应] 耗时: {}ms, content: {}", duration, content);
        } else {
            int contentLength = content != null ? content.length() : 0;
            log.info("[AI 响应] 耗时: {}ms, 内容长度: {} 字符", duration, contentLength);
        }
    }
}
