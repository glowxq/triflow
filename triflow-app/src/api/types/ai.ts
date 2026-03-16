/**
 * AI 模块类型定义
 */

/** AI 聊天请求参数 */
export interface AiChatDTO {
  /** AI 提供商 (deepseek, openai, zhipuai, anthropic, ollama)，不传则使用默认 */
  provider?: string
  /** 模型名称，不传则使用提供商默认模型 */
  model?: string
  /** 系统提示 */
  systemPrompt?: string
  /** 用户消息 */
  message: string
  /** 温度 (0-2) */
  temperature?: number
  /** 最大生成 token 数 */
  maxTokens?: number
}

/** AI 聊天响应 */
export interface AiChatVO {
  /** 响应 ID */
  id?: string
  /** 使用的提供商 */
  provider: string
  /** 使用的模型 */
  model?: string
  /** AI 响应内容 */
  content: string
  /** 结束原因 */
  finishReason?: string
  /** 提示 token 数 */
  promptTokens?: number
  /** 完成 token 数 */
  completionTokens?: number
  /** 总 token 数 */
  totalTokens?: number
}

/** AI 提供商信息 */
export interface AiProviderVO {
  /** 提供商代码 */
  code: string
  /** 提供商名称 */
  name: string
  /** 是否可用 */
  available: boolean
  /** 是否默认 */
  isDefault: boolean
  /** 默认模型 */
  defaultModel?: string
}
