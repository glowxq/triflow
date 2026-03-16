<script lang="ts" setup>
/**
 * 权限控制组件
 * @description 用于条件渲染，基于角色或权限码控制子元素显示
 * @example
 * ```vue
 * // 基于角色
 * <AccessControl :roles="['admin']">
 *   <view>仅管理员可见</view>
 * </AccessControl>
 *
 * // 基于权限码
 * <AccessControl :codes="['user:create']">
 *   <view>有创建用户权限可见</view>
 * </AccessControl>
 *
 * // 显示无权限时的替代内容
 * <AccessControl :roles="['admin']">
 *   <view>管理员内容</view>
 *   <template #fallback>
 *     <view>您没有访问权限</view>
 *   </template>
 * </AccessControl>
 * ```
 */
import { useAccess } from '@/hooks/useAccess'

// ==================== Props 定义 ====================

interface Props {
  /** 需要的角色列表（满足一个即可） */
  roles?: string[]
  /** 需要的权限码列表（满足一个即可） */
  codes?: string[]
  /** 是否需要全部满足（默认 false，满足一个即可） */
  requireAll?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  roles: () => [],
  codes: () => [],
  requireAll: false,
})

// ==================== 权限判断 ====================

const { hasRoles, hasAllRoles, hasPermissions, hasAllPermissions } = useAccess()

/** 是否有权限访问 */
const hasAccess = computed(() => {
  const { roles, codes, requireAll } = props

  // 没有配置权限要求，默认允许访问
  if (roles.length === 0 && codes.length === 0) {
    return true
  }

  // 检查角色
  let rolePass = true
  if (roles.length > 0) {
    rolePass = requireAll ? hasAllRoles(roles) : hasRoles(roles)
  }

  // 检查权限码
  let codePass = true
  if (codes.length > 0) {
    codePass = requireAll ? hasAllPermissions(codes) : hasPermissions(codes)
  }

  // 如果同时配置了角色和权限码，需要同时满足
  if (roles.length > 0 && codes.length > 0) {
    return rolePass && codePass
  }

  // 只配置了其中一个
  return rolePass && codePass
})
</script>

<template>
  <template v-if="hasAccess">
    <slot />
  </template>
  <template v-else>
    <slot name="fallback" />
  </template>
</template>
