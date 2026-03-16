/**
 * 系统配置相关 API
 */

import type { SysConfigQuery, SysConfigVO, SysDeptTreeVO, SysSwitchQuery, SysSwitchVO } from './types/system'
import { http } from '@/http/http'

/**
 * 根据配置键获取配置值
 * @param key 配置键
 */
export function getConfigValue(key: string) {
  return http.get<string>(`/base/public/config/${key}`)
}

/**
 * 获取多个配置值
 * @param keys 配置键列表
 */
export function getConfigValues(keys: string[]) {
  return http.post<Record<string, string>>('/base/admin/system/config/values', { keys })
}

/**
 * 获取开关状态（需要登录）
 * @param key 开关键
 */
export function getSwitchStatus(key: string) {
  return http.get<boolean>(`/base/admin/system/switch/status/${key}`)
}

/**
 * 获取公开开关状态（无需登录）
 * @param key 开关键
 */
export function getPublicSwitchStatus(key: string) {
  return http.get<boolean>(`/base/public/switch/${key}`)
}

/**
 * 获取部门树（需要登录）
 */
export function getDeptTree() {
  return http.get<SysDeptTreeVO[]>('/base/admin/system/dept/tree')
}

/**
 * 获取公开部门树（无需登录）
 */
export function getPublicDeptTree() {
  return http.get<SysDeptTreeVO[]>('/base/public/system/dept/tree')
}

/**
 * 获取多个开关状态
 * @param keys 开关键列表
 */
export function getSwitchStatuses(keys: string[]) {
  return http.post<Record<string, boolean>>('/base/admin/system/switch/statuses', { keys })
}

/**
 * 查询系统开关列表（管理员）
 */
export function getSwitchList(query: Partial<SysSwitchQuery> = {}) {
  return http.post<SysSwitchVO[]>('/base/admin/system/switch/list', query as Record<string, any>)
}

/**
 * 切换系统开关（管理员）
 */
export function toggleSwitch(id: number, switchValue: number, changeReason?: string) {
  return http.put<void>(
    '/base/admin/system/switch/toggle',
    { switchValue, changeReason },
    { id },
  )
}

/**
 * 查询系统配置列表（管理员）
 */
export function getConfigList(query: Partial<SysConfigQuery> = {}) {
  return http.post<SysConfigVO[]>('/base/admin/system/config/list', query as Record<string, any>)
}

/**
 * 更新系统配置（管理员）
 */
export function updateConfigValue(id: number, configValue: string) {
  return http.put<void>('/base/admin/system/config/update', { id, configValue })
}
