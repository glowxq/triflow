/**
 * 微信相关 API
 */

import { http } from '@/http/http'

export interface WechatTabbarItem {
  /** 菜单名称 */
  text: string
  /** 页面路径 */
  pagePath: string
  /** 图标类型 */
  iconType: string
  /** 图标资源 */
  icon: string
  /** 选中图标 */
  iconActive?: string
  /** 徽标 */
  badge?: string
  /** 是否鼓包 */
  isBulge?: number
  /** 排序 */
  sort?: number
}

/**
 * 获取微信小程序底部菜单
 */
export function getWechatTabbarList() {
  return http.get<WechatTabbarItem[]>('/base/public/wechat/tabbar/list')
}
