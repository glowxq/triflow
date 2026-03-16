/**
 * AI 服务相关 API
 */

import type { AiChatDTO, AiChatVO, AiProviderVO } from './types/ai'
import { http } from '@/http/http'

/**
 * 获取可用的 AI 提供商列表
 */
export function getAiProviders() {
  return http.get<AiProviderVO[]>('/base/client/ai/providers')
}

/**
 * AI 聊天（完整参数）
 * @param data 聊天请求参数
 */
export function aiChat(data: AiChatDTO) {
  return http.post<AiChatVO>('/base/client/ai/chat', data)
}

/**
 * AI 简单聊天（仅文本）
 * @param message 用户消息
 */
export function aiSimpleChat(message: string) {
  return http.post<string>(`/base/client/ai/simple-chat?message=${encodeURIComponent(message)}`)
}
