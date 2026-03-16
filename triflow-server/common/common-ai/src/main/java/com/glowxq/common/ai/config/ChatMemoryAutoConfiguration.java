package com.glowxq.common.ai.config;

import com.glowxq.common.ai.memory.RedisChatMemoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * 对话记忆自动配置
 * <p>
 * 默认使用 {@link InMemoryChatMemoryRepository}，
 * 当配置 {@code triflow.ai.memory-storage=redis} 时切换到 Redis 存储。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@AutoConfiguration
public class ChatMemoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ChatMemoryRepository.class)
    @ConditionalOnProperty(name = "triflow.ai.memory-storage", havingValue = "memory", matchIfMissing = true)
    public ChatMemoryRepository inMemoryChatMemoryRepository() {
        log.info("初始化 InMemory 对话记忆存储");
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    @ConditionalOnMissingBean(ChatMemoryRepository.class)
    @ConditionalOnProperty(name = "triflow.ai.memory-storage", havingValue = "redis")
    public ChatMemoryRepository redisChatMemoryRepository(StringRedisTemplate redisTemplate,
                                                          ObjectMapper objectMapper,
                                                          AiProperties properties) {
        String keyPrefix = properties.getRedis().getKeyPrefix();
        Duration ttl = Duration.ofSeconds(properties.getRedis().getTimeToLive());
        log.info("初始化 Redis 对话记忆存储, keyPrefix={}, ttl={}s", keyPrefix, properties.getRedis().getTimeToLive());
        return new RedisChatMemoryRepository(redisTemplate, objectMapper, keyPrefix, ttl);
    }

    @Bean
    @ConditionalOnMissingBean(ChatMemory.class)
    public ChatMemory chatMemory(ChatMemoryRepository repository, AiProperties properties) {
        int windowSize = properties.getMemoryWindowSize();
        log.info("初始化对话记忆, 窗口大小={}", windowSize);
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(windowSize)
                .build();
    }
}
