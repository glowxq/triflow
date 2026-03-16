/**
 * AI 管理 API
 * @description AI 配置、Prompt 模板管理接口
 */
import type {
  AiCallLogQuery,
  AiCallLogVO,
  AiChatDTO,
  AiChatVO,
  AiConfigSaveDTO,
  AiConfigVO,
  AiProviderVO,
  PageResult,
  PromptTemplateQuery,
  PromptTemplateSaveDTO,
  PromptTemplateVO,
} from './types';

import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/ai/config';

// ==================== AI 提供商 ====================

/**
 * 获取可用的 AI 提供商列表
 */
export async function getAiProviders() {
  return requestClient.get<AiProviderVO[]>('/base/client/ai/providers');
}

/**
 * AI 聊天
 */
export async function aiChat(data: AiChatDTO) {
  return requestClient.post<AiChatVO>('/base/client/ai/chat', data);
}

/**
 * 简单聊天
 */
export async function simpleChat(message: string) {
  return requestClient.post<string>('/base/client/ai/simple-chat', null, {
    params: { message },
  });
}

// ==================== AI 配置 ====================

/**
 * 获取 AI 配置列表
 */
export async function getAiConfigList() {
  return requestClient.get<AiConfigVO[]>(`${BASE_URL}/config/list`);
}

/**
 * 获取 AI 配置详情
 */
export async function getAiConfig(id: number) {
  return requestClient.get<AiConfigVO>(`${BASE_URL}/config/${id}`);
}

/**
 * 保存 AI 配置
 */
export async function saveAiConfig(data: AiConfigSaveDTO) {
  return requestClient.post<null>(`${BASE_URL}/config/save`, data);
}

/**
 * 删除 AI 配置
 */
export async function deleteAiConfig(id: number) {
  return requestClient.delete<null>(`${BASE_URL}/config/${id}`);
}

/**
 * 设置默认提供商
 */
export async function setDefaultProvider(id: number) {
  return requestClient.post<null>(`${BASE_URL}/config/${id}/set-default`);
}

/**
 * 测试 AI 配置
 */
export async function testAiConfig(id: number) {
  return requestClient.post<string>(`${BASE_URL}/config/${id}/test`);
}

// ==================== Prompt 模板 ====================

/**
 * 分页查询 Prompt 模板
 */
export async function getPromptTemplatePage(params?: PromptTemplateQuery) {
  return requestClient.get<PageResult<PromptTemplateVO>>(
    `${BASE_URL}/prompt/page`,
    { params },
  );
}

/**
 * 获取 Prompt 模板详情
 */
export async function getPromptTemplate(id: number) {
  return requestClient.get<PromptTemplateVO>(`${BASE_URL}/prompt/${id}`);
}

/**
 * 根据代码获取 Prompt 模板
 */
export async function getPromptTemplateByCode(code: string) {
  return requestClient.get<PromptTemplateVO>(`${BASE_URL}/prompt/code/${code}`);
}

/**
 * 保存 Prompt 模板
 */
export async function savePromptTemplate(data: PromptTemplateSaveDTO) {
  return requestClient.post<null>(`${BASE_URL}/prompt/save`, data);
}

/**
 * 删除 Prompt 模板
 */
export async function deletePromptTemplate(id: number) {
  return requestClient.delete<null>(`${BASE_URL}/prompt/${id}`);
}

/**
 * 获取 Prompt 分类列表
 */
export async function getPromptCategories() {
  return requestClient.get<string[]>(`${BASE_URL}/prompt/categories`);
}

/**
 * 测试 Prompt 模板
 */
export async function testPromptTemplate(
  id: number,
  variables?: Record<string, string>,
) {
  // 直接发送 variables 对象，后端期望 Map<String, String>
  return requestClient.post<AiChatVO>(
    `${BASE_URL}/prompt/${id}/test`,
    variables,
  );
}

// ==================== AI 调用记录 ====================

const CALL_LOG_URL = '/base/admin/ai/call-log';

/**
 * 分页查询 AI 调用记录
 */
export async function getAiCallLogPage(params?: AiCallLogQuery) {
  return requestClient.get<PageResult<AiCallLogVO>>(
    `${CALL_LOG_URL}/page`,
    { params },
  );
}

/**
 * 获取 AI 调用记录详情
 */
export async function getAiCallLog(id: number) {
  return requestClient.get<AiCallLogVO>(`${CALL_LOG_URL}/${id}`);
}

/**
 * 删除 AI 调用记录
 */
export async function deleteAiCallLog(id: number) {
  return requestClient.delete<null>(`${CALL_LOG_URL}/${id}`);
}
