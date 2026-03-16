/**
 * 权限控制 Composable
 * @description 提供基于角色和权限码的权限判断方法
 * @example
 * const { hasRole, hasRoles, hasPermission } = useAccess()
 * if (hasRole('admin')) { ... }
 * if (hasRoles(['admin', 'editor'])) { ... }
 */

import { useUserStore } from '@/store'

/**
 * 权限控制 Hook
 */
export function useAccess() {
  const userStore = useUserStore()

  /**
   * 获取当前用户的角色列表
   */
  const userRoles = computed(() => userStore.roles || [])

  /**
   * 获取当前用户的权限码列表
   */
  const userPermissions = computed(() => userStore.permissions || [])

  /**
   * 判断是否有指定角色（单个）
   * @param role 角色编码
   */
  function hasRole(role: string): boolean {
    if (!role)
      return true
    return userRoles.value.includes(role)
  }

  /**
   * 判断是否有指定角色（多个，满足一个即可）
   * @param roles 角色编码数组
   */
  function hasRoles(roles: string[]): boolean {
    if (!roles || roles.length === 0)
      return true
    return roles.some(role => userRoles.value.includes(role))
  }

  /**
   * 判断是否有所有指定角色（多个，必须全部满足）
   * @param roles 角色编码数组
   */
  function hasAllRoles(roles: string[]): boolean {
    if (!roles || roles.length === 0)
      return true
    return roles.every(role => userRoles.value.includes(role))
  }

  /**
   * 判断是否有指定权限码（单个）
   * @param permission 权限码
   */
  function hasPermission(permission: string): boolean {
    if (!permission)
      return true
    // 超级管理员拥有所有权限
    if (userRoles.value.includes('super_admin'))
      return true
    return userPermissions.value.includes(permission)
  }

  /**
   * 判断是否有指定权限码（多个，满足一个即可）
   * @param permissions 权限码数组
   */
  function hasPermissions(permissions: string[]): boolean {
    if (!permissions || permissions.length === 0)
      return true
    // 超级管理员拥有所有权限
    if (userRoles.value.includes('super_admin'))
      return true
    return permissions.some(p => userPermissions.value.includes(p))
  }

  /**
   * 判断是否有所有指定权限码（多个，必须全部满足）
   * @param permissions 权限码数组
   */
  function hasAllPermissions(permissions: string[]): boolean {
    if (!permissions || permissions.length === 0)
      return true
    // 超级管理员拥有所有权限
    if (userRoles.value.includes('super_admin'))
      return true
    return permissions.every(p => userPermissions.value.includes(p))
  }

  /**
   * 判断是否是超级管理员
   */
  function isSuperAdmin(): boolean {
    return userRoles.value.includes('super_admin')
  }

  /**
   * 判断是否是管理员（包括超级管理员）
   */
  function isAdmin(): boolean {
    return userRoles.value.includes('super_admin') || userRoles.value.includes('admin')
  }

  return {
    // 响应式数据
    userRoles,
    userPermissions,
    // 角色相关方法
    hasRole,
    hasRoles,
    hasAllRoles,
    // 权限码相关方法
    hasPermission,
    hasPermissions,
    hasAllPermissions,
    // 便捷方法
    isSuperAdmin,
    isAdmin,
  }
}

export default useAccess
