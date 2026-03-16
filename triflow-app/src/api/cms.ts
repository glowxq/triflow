/**
 * CMS 文本管理 API
 * @description 提供公开的文本内容获取接口
 */

import type { CmsTextVO } from './types/cms'
import { http } from '@/http/http'

// ==================== 文本内容缓存 ====================

/** 文本缓存 */
const textCache = new Map<string, { data: CmsTextVO, timestamp: number }>()

/** 缓存有效期（5分钟） */
const CACHE_DURATION = 5 * 60 * 1000

/**
 * 根据文本标识获取公开文本内容
 * @description 无需登录，支持本地缓存
 * @param textKey 文本标识（如: user_agreement, privacy_policy）
 * @param useCache 是否使用缓存，默认 true
 */
export async function getPublicText(textKey: string, useCache = true): Promise<CmsTextVO> {
  const now = Date.now()

  // 检查缓存
  if (useCache) {
    const cached = textCache.get(textKey)
    if (cached && now - cached.timestamp < CACHE_DURATION) {
      return cached.data
    }
  }

  // 请求后端
  const data = await http.get<CmsTextVO>(`/base/public/text/${textKey}`)

  // 更新缓存
  textCache.set(textKey, { data, timestamp: now })

  return data
}

/**
 * 清除文本缓存
 * @param textKey 文本标识，不传则清除所有缓存
 */
export function clearTextCache(textKey?: string): void {
  if (textKey) {
    textCache.delete(textKey)
  }
  else {
    textCache.clear()
  }
}

/**
 * 预加载常用文本
 * @description 用于应用启动时预加载用户协议、隐私政策等
 * @param textKeys 文本标识列表
 */
export async function preloadTexts(textKeys: string[]): Promise<void> {
  await Promise.allSettled(
    textKeys.map(key => getPublicText(key)),
  )
}
