package com.glowxq.common.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Token 用量追踪 Advisor
 * <p>
 * 累计统计 input/output Token 用量，并以 DEBUG 日志输出每次调用的消耗。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
public class TokenTrackingAdvisor implements CallAdvisor, StreamAdvisor {

    private final AtomicLong totalInputTokens = new AtomicLong(0);
    private final AtomicLong totalOutputTokens = new AtomicLong(0);

    @Override
    public String getName() {
        return "TokenTrackingAdvisor";
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        ChatClientResponse response = chain.nextCall(request);
        trackUsage(response.chatResponse());
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        return chain.nextStream(request)
                .doOnNext(response -> trackUsage(response.chatResponse()));
    }

    private void trackUsage(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getMetadata() == null) {
            return;
        }
        Usage usage = chatResponse.getMetadata().getUsage();
        if (usage == null) {
            return;
        }

        long input = usage.getPromptTokens();
        long output = usage.getCompletionTokens();

        totalInputTokens.addAndGet(input);
        totalOutputTokens.addAndGet(output);

        if (log.isDebugEnabled()) {
            log.debug("Token 消耗 - 本次: input={}, output={}, total={} | 累计: input={}, output={}",
                    input, output, input + output,
                    totalInputTokens.get(), totalOutputTokens.get());
        }
    }

    public long getTotalInputTokens() {
        return totalInputTokens.get();
    }

    public long getTotalOutputTokens() {
        return totalOutputTokens.get();
    }
}
