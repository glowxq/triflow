/**
 * AI 管理模块类型定义
 */

/** 分页结果 (MyBatis-Flex Page) */
export interface PageResult<T> {
  records: T[];
  total: number;
  totalRow: number;
  totalPage: number;
  pageNumber: number;
  pageSize: number;
}

/**
 * AI 提供商信息
 */
export interface AiProviderVO {
  /** 提供商代码 */
  code: string;
  /** 提供商名称 */
  name: string;
  /** 是否可用 */
  available: boolean;
  /** 是否为默认提供商 */
  isDefault: boolean;
  /** 默认模型 */
  defaultModel?: string;
}

/**
 * AI 配置
 */
export interface AiConfigVO {
  /** 配置ID */
  id: number;
  /** 提供商代码 */
  provider: string;
  /** 提供商名称 */
  providerName: string;
  /** API Key (脱敏) */
  apiKey: string;
  /** API 端点 */
  endpoint?: string;
  /** 默认模型 */
  defaultModel?: string;
  /** 是否启用 */
  enabled: boolean;
  /** 是否为默认提供商 */
  isDefault: boolean;
  /** 超时时间 (秒) */
  timeout?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
}

/**
 * AI 配置保存参数
 */
export interface AiConfigSaveDTO {
  /** 配置ID (更新时必填) */
  id?: number;
  /** 提供商代码 */
  provider: string;
  /** API Key */
  apiKey: string;
  /** API 端点 */
  endpoint?: string;
  /** 默认模型 */
  defaultModel?: string;
  /** 是否启用 */
  enabled: boolean;
  /** 是否为默认提供商 */
  isDefault?: boolean;
  /** 超时时间 (秒) */
  timeout?: number;
}

/**
 * AI 聊天请求
 */
export interface AiChatDTO {
  /** AI 提供商 (deepseek, openai, zhipuai, anthropic, ollama) */
  provider?: string;
  /** 模型名称 */
  model?: string;
  /** 系统提示 */
  systemPrompt?: string;
  /** 用户消息 */
  message: string;
  /** 温度 */
  temperature?: number;
  /** 最大生成 token 数 */
  maxTokens?: number;
}

/**
 * AI 聊天响应
 */
export interface AiChatVO {
  /** 响应 ID */
  id?: string;
  /** 使用的提供商 */
  provider: string;
  /** 使用的模型 */
  model?: string;
  /** AI 响应内容 */
  content: string;
  /** 结束原因 */
  finishReason?: string;
  /** 提示 token 数 */
  promptTokens?: number;
  /** 完成 token 数 */
  completionTokens?: number;
  /** 总 token 数 */
  totalTokens?: number;
}

/**
 * Prompt 模板
 */
export interface PromptTemplateVO {
  /** 模板ID */
  id: number;
  /** 模板名称 */
  name: string;
  /** 模板代码 */
  code: string;
  /** 模板分类 */
  category?: string;
  /** 系统提示 */
  systemPrompt: string;
  /** 用户提示模板 */
  userPromptTemplate?: string;
  /** 模板变量 (JSON) */
  variables?: string;
  /** 描述 */
  description?: string;
  /** 是否启用 */
  enabled: boolean;
  /** 排序 */
  sort: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
}

/**
 * Prompt 模板查询参数
 */
export interface PromptTemplateQuery {
  /** 页码 */
  pageNum?: number;
  /** 每页条数 */
  pageSize?: number;
  /** 模板名称 */
  name?: string;
  /** 模板代码 */
  code?: string;
  /** 模板分类 */
  category?: string;
  /** 是否启用 */
  enabled?: boolean;
}

/**
 * Prompt 模板保存参数
 */
export interface PromptTemplateSaveDTO {
  /** 模板ID (更新时必填) */
  id?: number;
  /** 模板名称 */
  name: string;
  /** 模板代码 */
  code: string;
  /** 模板分类 */
  category?: string;
  /** 系统提示 */
  systemPrompt: string;
  /** 用户提示模板 */
  userPromptTemplate?: string;
  /** 模板变量 (JSON) */
  variables?: string;
  /** 描述 */
  description?: string;
  /** 是否启用 */
  enabled: boolean;
  /** 排序 */
  sort?: number;
}

/**
 * AI 调用记录
 */
export interface AiCallLogVO {
  /** 主键ID */
  id: number;
  /** 用户ID */
  userId?: number;
  /** 用户名 */
  username?: string;
  /** AI 提供商 */
  provider: string;
  /** 提供商名称 */
  providerName?: string;
  /** 使用的模型 */
  model?: string;
  /** 系统提示 */
  systemPrompt?: string;
  /** 用户消息 */
  userMessage: string;
  /** AI 响应内容 */
  aiResponse?: string;
  /** 提示 token 数 */
  promptTokens?: number;
  /** 完成 token 数 */
  completionTokens?: number;
  /** 总 token 数 */
  totalTokens?: number;
  /** 耗时 (毫秒) */
  duration?: number;
  /** 状态: 1-成功, 0-失败 */
  status: number;
  /** 状态描述 */
  statusDesc?: string;
  /** 错误信息 */
  errorMessage?: string;
  /** 请求IP */
  ip?: string;
  /** 创建时间 */
  createTime?: string;
}

/**
 * AI 调用记录查询参数
 */
export interface AiCallLogQuery {
  /** 页码 */
  pageNum?: number;
  /** 每页条数 */
  pageSize?: number;
  /** 用户ID */
  userId?: number;
  /** 用户名 */
  username?: string;
  /** AI 提供商 */
  provider?: string;
  /** 模型 */
  model?: string;
  /** 状态 */
  status?: number;
  /** 开始时间 */
  startTime?: string;
  /** 结束时间 */
  endTime?: string;
}
