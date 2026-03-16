package com.glowxq.common.ai.service;

import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * AI 服务高阶门面接口
 * <p>
 * 提供统一的多提供商 AI 调用入口，支持同步聊天、流式聊天、结构化输出。
 *
 * @author glowxq
 * @since 2025-01-28
 */
public interface AiService {

    /**
     * 使用默认提供商进行简单对话
     *
     * @param userMessage 用户消息
     * @return AI 响应文本
     */
    String chat(String userMessage);

    /**
     * 使用默认提供商，带系统提示进行对话
     *
     * @param systemPrompt 系统提示
     * @param userMessage  用户消息
     * @return AI 响应文本
     */
    String chat(String systemPrompt, String userMessage);

    /**
     * 使用指定提供商，带系统提示进行对话
     *
     * @param provider     提供商名称
     * @param systemPrompt 系统提示
     * @param userMessage  用户消息
     * @return AI 响应文本
     */
    String chat(String provider, String systemPrompt, String userMessage);

    /**
     * 使用指定提供商，带会话 ID 和系统提示进行对话 (支持对话记忆)
     *
     * @param conversationId 会话 ID
     * @param provider       提供商名称
     * @param systemPrompt   系统提示
     * @param userMessage    用户消息
     * @return AI 响应文本
     */
    String chat(String conversationId, String provider, String systemPrompt, String userMessage);

    /**
     * 使用默认提供商进行流式对话
     *
     * @param userMessage 用户消息
     * @return 响应文本流
     */
    Flux<String> stream(String userMessage);

    /**
     * 使用指定提供商，带系统提示进行流式对话
     *
     * @param provider     提供商名称
     * @param systemPrompt 系统提示
     * @param userMessage  用户消息
     * @return 响应文本流
     */
    Flux<String> stream(String provider, String systemPrompt, String userMessage);

    /**
     * 使用默认提供商进行结构化输出
     *
     * @param userMessage  用户消息
     * @param responseType 期望的响应类型
     * @param <T>          响应类型
     * @return 结构化响应对象
     */
    <T> T chatForObject(String userMessage, Class<T> responseType);

    /**
     * 使用指定提供商进行结构化输出
     *
     * @param provider     提供商名称
     * @param systemPrompt 系统提示
     * @param userMessage  用户消息
     * @param responseType 期望的响应类型
     * @param <T>          响应类型
     * @return 结构化响应对象
     */
    <T> T chatForObject(String provider, String systemPrompt, String userMessage, Class<T> responseType);

    /**
     * 获取所有可用的提供商名称
     *
     * @return 提供商名称集合
     */
    Set<String> getAvailableProviders();

    /**
     * 获取默认提供商名称
     *
     * @return 默认提供商名称
     */
    String getDefaultProvider();
}
