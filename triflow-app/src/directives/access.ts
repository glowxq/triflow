/**
 * 权限控制指令
 * @description 用于组件级别的细粒度权限控制
 *
 * 使用方式：
 * @example
 * <!-- 基于角色控制（单个角色） -->
 * <view v-access:role="'admin'">仅管理员可见</view>
 *
 * <!-- 基于角色控制（多个角色，满足一个即可） -->
 * <view v-access:role="['admin', 'editor']">管理员或编辑可见</view>
 *
 * <!-- 基于权限码控制（单个权限码） -->
 * <view v-access:code="'user:create'">有创建用户权限可见</view>
 *
 * <!-- 基于权限码控制（多个权限码，满足一个即可） -->
 * <view v-access:code="['user:create', 'user:update']">有创建或更新权限可见</view>
 */

import type { App, Directive, DirectiveBinding } from 'vue'
import { useAccess } from '@/hooks/useAccess'

/**
 * 检查是否有访问权限
 */
function checkAccess(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
  const { hasRoles, hasPermissions } = useAccess()

  const value = binding.value
  const arg = binding.arg // 'role' | 'code'

  // 没有值则默认有权限
  if (!value)
    return

  // 转换为数组
  const values = Array.isArray(value) ? value : [value]

  // 根据指令参数选择检查方法
  let hasAccess = false
  if (arg === 'role') {
    hasAccess = hasRoles(values)
  }
  else if (arg === 'code') {
    hasAccess = hasPermissions(values)
  }
  else {
    // 默认按角色处理
    hasAccess = hasRoles(values)
  }

  // 没有权限则移除元素
  if (!hasAccess) {
    // 使用注释节点替换，便于调试
    const comment = document.createComment(`v-access: no access for ${arg}=${JSON.stringify(value)}`)
    el.parentNode?.replaceChild(comment, el)
  }
}

/**
 * 权限指令定义
 */
const accessDirective: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    checkAccess(el, binding)
  },
  updated(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    // 值变化时重新检查（注意：如果元素已被移除，则无法更新）
    if (binding.value !== binding.oldValue) {
      checkAccess(el, binding)
    }
  },
}

/**
 * 注册权限指令
 * @param app Vue 应用实例
 */
export function registerAccessDirective(app: App) {
  app.directive('access', accessDirective)
}

export { accessDirective }
export default accessDirective
