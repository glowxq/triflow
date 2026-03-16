package com.glowxq.common.ai.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Redis 持久化对话记忆存储
 * <p>
 * 使用 {@link StringRedisTemplate} + JSON 序列化存储 Message 列表，
 * 支持 TTL 过期和 key 前缀隔离。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final String keyPrefix;
    private final Duration timeToLive;

    public RedisChatMemoryRepository(StringRedisTemplate redisTemplate,
                                     ObjectMapper objectMapper,
                                     String keyPrefix,
                                     Duration timeToLive) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.keyPrefix = keyPrefix;
        this.timeToLive = timeToLive;
    }

    @Override
    public List<String> findConversationIds() {
        var keys = redisTemplate.keys(keyPrefix + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return keys.stream()
                .map(k -> k.substring(keyPrefix.length()))
                .toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        String key = buildKey(conversationId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<Map<String, String>> rawList = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            List<Message> messages = new ArrayList<>(rawList.size());
            for (Map<String, String> item : rawList) {
                String type = item.get("type");
                String content = item.get("content");
                Message message = switch (type) {
                    case "USER" -> new UserMessage(content);
                    case "ASSISTANT" -> new AssistantMessage(content);
                    case "SYSTEM" -> new SystemMessage(content);
                    default -> new UserMessage(content);
                };
                messages.add(message);
            }
            return messages;
        } catch (JsonProcessingException e) {
            log.error("反序列化对话记忆失败, conversationId={}", conversationId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        String key = buildKey(conversationId);
        try {
            List<Map<String, String>> rawList = messages.stream()
                    .map(msg -> Map.of(
                            "type", msg.getMessageType().name(),
                            "content", msg.getText() != null ? msg.getText() : ""
                    ))
                    .toList();
            String json = objectMapper.writeValueAsString(rawList);
            if (timeToLive.isZero() || timeToLive.isNegative()) {
                redisTemplate.opsForValue().set(key, json);
            } else {
                redisTemplate.opsForValue().set(key, json, timeToLive);
            }
        } catch (JsonProcessingException e) {
            log.error("序列化对话记忆失败, conversationId={}", conversationId, e);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redisTemplate.delete(buildKey(conversationId));
    }

    private String buildKey(String conversationId) {
        return keyPrefix + conversationId;
    }
}
