/**
 * 全局指令注册
 * @description 在 main.ts 中调用 registerDirectives(app) 注册所有指令
 */

import type { App } from 'vue'
import { registerAccessDirective } from './access'

/**
 * 注册所有全局指令
 * @param app Vue 应用实例
 */
export function registerDirectives(app: App) {
  // 注册权限控制指令 v-access
  registerAccessDirective(app)
}

export * from './access'
