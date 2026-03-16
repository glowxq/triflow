/**
 * 用户信息状态管理
 * @description 管理当前登录用户的信息
 */

import type { UserInfoVO } from '@/api/types/login'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getUserInfo } from '@/api/login'

// 默认头像
const DEFAULT_AVATAR = '/static/images/default-avatar.png'

/** 用户信息扩展类型（包含权限码） */
interface UserInfoExtended extends UserInfoVO {
  /** 权限码列表 */
  permissions?: string[]
}

// 初始状态
const initialUserInfo: UserInfoExtended = {
  userId: 0,
  username: '',
  realName: '',
  nickname: '',
  avatar: DEFAULT_AVATAR,
  roles: [],
  permissions: [],
  passwordSet: true,
}

export const useUserStore = defineStore(
  'user',
  () => {
    // ==================== State ====================

    /** 用户信息 */
    const userInfo = ref<UserInfoExtended>({ ...initialUserInfo })

    // ==================== Getters ====================

    /** 用户 ID */
    const userId = computed(() => userInfo.value.userId)

    /** 用户名 */
    const username = computed(() => userInfo.value.username)

    /** 显示名称（优先使用昵称，其次真实姓名，最后用户名） */
    const displayName = computed(() => {
      return userInfo.value.nickname
        || userInfo.value.realName
        || userInfo.value.username
        || '用户'
    })

    /** 头像（无头像时返回默认头像） */
    const avatar = computed(() => userInfo.value.avatar || DEFAULT_AVATAR)

    /** 角色列表 */
    const roles = computed(() => userInfo.value.roles || [])

    /** 权限码列表 */
    const permissions = computed(() => userInfo.value.permissions || [])

    /** 是否有有效用户信息 */
    const hasUserInfo = computed(() => userInfo.value.userId > 0)

    // ==================== Actions ====================

    /**
     * 设置用户信息
     * @param info 用户信息（支持部分字段更新）
     */
    function setUserInfo(info: Partial<UserInfoExtended>) {
      // 若头像为空则使用默认头像
      if (!info.avatar) {
        info.avatar = DEFAULT_AVATAR
      }

      userInfo.value = {
        ...userInfo.value,
        ...info,
      } as UserInfoExtended

      console.log('设置用户信息:', userInfo.value)
    }

    /**
     * 设置权限码列表
     * @param codes 权限码数组
     */
    function setPermissions(codes: string[]) {
      userInfo.value.permissions = codes
      console.log('设置权限码:', codes)
    }

    /**
     * 设置用户头像
     * @param avatarUrl 头像 URL
     */
    function setUserAvatar(avatarUrl: string) {
      userInfo.value.avatar = avatarUrl || DEFAULT_AVATAR
      console.log('设置用户头像:', avatarUrl)
    }

    /**
     * 清除用户信息
     */
    function clearUserInfo() {
      userInfo.value = { ...initialUserInfo }
      uni.removeStorageSync('user')
      console.log('清除用户信息')
    }

    /**
     * 从服务器获取用户信息
     */
    async function fetchUserInfo() {
      try {
        const res = await getUserInfo()
        setUserInfo(res)
        return res
      }
      catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    }

    return {
      // State
      userInfo,

      // Getters
      userId,
      username,
      displayName,
      avatar,
      roles,
      permissions,
      hasUserInfo,

      // Actions
      setUserInfo,
      setUserAvatar,
      setPermissions,
      clearUserInfo,
      fetchUserInfo,
    }
  },
  {
    // 持久化配置
    persist: true,
  },
)
