package com.glowxq.common.ai.routing;

import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.exception.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多模型注册表 + 路由
 * <p>
 * 自动扫描注入的 ChatModel Bean，按提供商类名映射，
 * 支持运行时动态注册和路由切换。
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
public class ChatModelRouter {

    private final Map<String, ChatModel> models = new ConcurrentHashMap<>();
    private final String defaultProvider;

    public ChatModelRouter(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    /**
     * 注册模型
     *
     * @param name  提供商名称 (小写，如 deepseek, openai)
     * @param model ChatModel 实例
     */
    public void register(String name, ChatModel model) {
        String key = name.toLowerCase();
        models.put(key, model);
        log.info("AI 模型已注册: {} -> {}", key, model.getClass().getSimpleName());
    }

    /**
     * 获取指定提供商的模型
     *
     * @param providerName 提供商名称
     * @return ChatModel 实例
     * @throws BusinessException 提供商未找到
     */
    public ChatModel getModel(String providerName) {
        String key = providerName.toLowerCase();
        ChatModel model = models.get(key);
        if (model == null) {
            throw new BusinessException(ErrorCodeEnum.AI_PROVIDER_NOT_FOUND,
                    "AI 提供商未找到: " + providerName);
        }
        return model;
    }

    /**
     * 获取默认模型
     *
     * @return 默认 ChatModel
     * @throws BusinessException 默认提供商不可用
     */
    public ChatModel getDefaultModel() {
        ChatModel model = models.get(defaultProvider.toLowerCase());
        if (model == null) {
            // 回退到第一个可用模型
            if (!models.isEmpty()) {
                Map.Entry<String, ChatModel> first = models.entrySet().iterator().next();
                log.warn("默认提供商 [{}] 不可用，回退到 [{}]", defaultProvider, first.getKey());
                return first.getValue();
            }
            throw new BusinessException(ErrorCodeEnum.AI_PROVIDER_UNAVAILABLE,
                    "AI 提供商不可用: " + defaultProvider);
        }
        return model;
    }

    /**
     * 获取默认提供商名称
     */
    public String getDefaultProvider() {
        return defaultProvider;
    }

    /**
     * 获取所有可用提供商名称
     */
    public Set<String> getAvailableProviders() {
        return Collections.unmodifiableSet(models.keySet());
    }

    /**
     * 检查提供商是否可用
     */
    public boolean isAvailable(String providerName) {
        return models.containsKey(providerName.toLowerCase());
    }
}
