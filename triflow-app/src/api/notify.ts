/**
 * 消息通知 API
 */

import type {
  NotifySubscribeSubmitDTO,
  NotifySubscribeVO,
  NotifyTemplateVO,
} from './types/notify'
import { http } from '@/http/http'

/**
 * 获取启用的通知模板
 */
export function getNotifyTemplates(channel: string) {
  return http.get<NotifyTemplateVO[]>('/base/admin/notify/template/enabled', { channel })
}

/**
 * 获取当前用户订阅列表
 */
export function getNotifySubscribes(channel: string) {
  return http.get<NotifySubscribeVO[]>('/base/client/notify/subscribe/list', { channel })
}

/**
 * 提交通知订阅结果
 */
export function submitNotifySubscribes(data: NotifySubscribeSubmitDTO) {
  return http.post<void>('/base/client/notify/subscribe/submit', data)
}
