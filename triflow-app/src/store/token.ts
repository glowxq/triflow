/**
 * Token 状态管理
 * @description 管理登录 Token 和认证状态
 */

import type { LoginVO } from '@/api/types/login'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import {
  logout as logoutApi,
  passwordLogin,
  smsLogin,
  wxQuickLogin,
} from '@/api/login'
import { getPublicSwitchStatus } from '@/api/system'
import { currRoute } from '@/utils'
import { useUserStore } from './user'

// Token 过期时间（默认 7 天，单位：毫秒）
const DEFAULT_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000

export const useTokenStore = defineStore(
  'token',
  () => {
    // ==================== State ====================

    /** 访问令牌 */
    const accessToken = ref<string>('')

    /** Token 过期时间戳 */
    const tokenExpireTime = ref<number>(0)

    /** 登录信息（包含用户基础信息） */
    const loginInfo = ref<LoginVO | null>(null)

    /** 是否正在退出登录 */
    const loggingOut = ref(false)

    /** 是否已提示登录过期 */
    const tokenExpiredNotified = ref(false)

    // ==================== Getters ====================

    /**
     * 判断 Token 是否过期
     */
    const isTokenExpired = computed(() => {
      if (!accessToken.value || !tokenExpireTime.value) {
        return true
      }
      return Date.now() >= tokenExpireTime.value
    })

    /**
     * 获取有效的 Token
     * @returns 有效的 Token 或空字符串
     */
    const validToken = computed(() => {
      if (isTokenExpired.value) {
        return ''
      }
      return accessToken.value
    })

    /**
     * 检查是否已登录（Token 存在且未过期）
     */
    const isLoggedIn = computed(() => {
      return !!accessToken.value && !isTokenExpired.value
    })

    /**
     * 检查是否有登录信息（不考虑 Token 是否过期）
     */
    const hasLoginInfo = computed(() => {
      return !!accessToken.value
    })

    // ==================== Actions ====================

    /**
     * 设置 Token 信息
     * @param data 登录响应数据
     */
    function setTokenInfo(data: LoginVO) {
      accessToken.value = data.accessToken
      loginInfo.value = data

      // 计算并存储过期时间（后端未返回过期时间时使用默认值）
      const expireTime = Date.now() + DEFAULT_TOKEN_EXPIRE_TIME
      tokenExpireTime.value = expireTime
      uni.setStorageSync('tokenExpireTime', expireTime)
    }

    /**
     * 清除 Token 信息
     */
    function clearTokenInfo() {
      accessToken.value = ''
      tokenExpireTime.value = 0
      loginInfo.value = null
      uni.removeStorageSync('tokenExpireTime')
    }

    /**
     * 标记已提示登录过期
     */
    function markTokenExpiredNotified() {
      tokenExpiredNotified.value = true
    }

    /**
     * 重置登录过期提示标记
     */
    function resetTokenExpiredNotified() {
      tokenExpiredNotified.value = false
    }

    /**
     * 登录成功后的通用处理
     */
    async function handleLoginSuccess(data: LoginVO) {
      setTokenInfo(data)
      tokenExpiredNotified.value = false

      // 获取并设置用户信息
      const userStore = useUserStore()
      try {
        await userStore.fetchUserInfo()
      }
      catch (error) {
        console.warn('获取用户信息失败，使用登录返回的基础信息:', error)
        // 使用登录返回的基础信息
        userStore.setUserInfo({
          userId: data.userId,
          username: data.username,
          realName: data.realName,
          avatar: data.avatar,
          roles: data.roles,
        })
      }

      uni.showToast({
        title: '登录成功',
        icon: 'success',
      })
    }

    /**
     * 登录后的跳转检查（手机号收集/资料完善）
     * @param options 跳转控制选项
     */
    async function postLoginRedirect(options?: {
      skipPhoneCheck?: boolean
      skipProfileCheck?: boolean
    }) {
      const { path } = currRoute()
      const userStore = useUserStore()

      if (!options?.skipPhoneCheck) {
        // #ifdef MP-WEIXIN
        try {
          const phoneCollectEnabled = await getPublicSwitchStatus('wechat.collect.phone.enabled')
          const hasPhone = Boolean(userStore.userInfo.phone || loginInfo.value?.phone)
          if (phoneCollectEnabled && !hasPhone && path !== '/pages/collect-phone/index') {
            uni.navigateTo({ url: '/pages/collect-phone/index' })
            return true
          }
        }
        catch (error) {
          console.warn('获取手机号收集开关失败:', error)
        }
        // #endif
      }

      if (!options?.skipProfileCheck) {
        try {
          const profileCompleteEnabled = await getPublicSwitchStatus('user.profile.complete.enabled')
          const passwordReady = userStore.userInfo.passwordSet !== false
          const basicComplete = Boolean(userStore.userInfo.avatar) && Boolean(userStore.userInfo.nickname)
          const isProfileComplete = (loginInfo.value?.profileComplete ?? basicComplete) && passwordReady
          if (profileCompleteEnabled && !isProfileComplete && path !== '/pages/profile/index') {
            uni.navigateTo({ url: '/pages/profile/index' })
            return true
          }
        }
        catch (error) {
          console.warn('获取用户信息完善开关失败:', error)
        }
      }

      return false
    }

    /**
     * 账号密码登录
     * @param username 用户名
     * @param password 密码
     * @param captchaKey 验证码 Key（可选）
     * @param captchaCode 验证码值（可选）
     */
    async function loginByPassword(
      username: string,
      password: string,
      captchaKey?: string,
      captchaCode?: string,
    ) {
      try {
        const res = await passwordLogin(username, password, captchaKey, captchaCode)
        await handleLoginSuccess(res)
        return res
      }
      catch (error) {
        console.error('账号密码登录失败:', error)
        throw error
      }
    }

    /**
     * 短信验证码登录
     * @param phone 手机号
     * @param smsCode 短信验证码
     */
    async function loginBySms(phone: string, smsCode: string) {
      try {
        const res = await smsLogin(phone, smsCode)
        await handleLoginSuccess(res)
        return res
      }
      catch (error) {
        console.error('短信登录失败:', error)
        throw error
      }
    }

    /**
     * 微信一键登录
     */
    async function loginByWechat() {
      try {
        const res = await wxQuickLogin()
        await handleLoginSuccess(res)
        return res
      }
      catch (error) {
        console.error('微信登录失败:', error)
        uni.showToast({
          title: '微信登录失败，请重试',
          icon: 'none',
        })
        throw error
      }
    }

    /**
     * 退出登录
     */
    async function logout(options?: { skipApi?: boolean }) {
      if (loggingOut.value) {
        return
      }
      loggingOut.value = true
      try {
        // 调用后端登出接口
        if (accessToken.value && !options?.skipApi) {
          await logoutApi()
        }
      }
      catch (error) {
        console.warn('调用登出接口失败:', error)
      }
      finally {
        loggingOut.value = false
        // 清除本地状态
        clearTokenInfo()

        // 清除用户信息
        const userStore = useUserStore()
        userStore.clearUserInfo()

        console.log('退出登录成功')
      }
    }

    /**
     * 刷新用户信息（Token 有效时）
     */
    async function refreshUserInfo() {
      if (!isLoggedIn.value) {
        throw new Error('未登录，无法刷新用户信息')
      }

      const userStore = useUserStore()
      await userStore.fetchUserInfo()
    }

    // ==================== 兼容旧版 API ====================

    /** @deprecated 请使用 loginByPassword */
    const login = loginByPassword

    /** @deprecated 请使用 loginByWechat */
    const wxLogin = loginByWechat

    /** @deprecated 请使用 isLoggedIn */
    const hasLogin = isLoggedIn

    /** @deprecated 请使用 validToken */
    const getValidToken = validToken

    /** @deprecated 不再需要手动调用 */
    const updateNowTime = () => useTokenStore()

    /** @deprecated 不再需要手动调用 */
    const tryGetValidToken = async () => validToken.value

    /** @deprecated 单 Token 模式不支持刷新 */
    const refreshToken = async () => {
      throw new Error('单 Token 模式不支持刷新 Token')
    }

    return {
      // ==================== 核心 API ====================
      // 登录方法
      loginByPassword,
      loginBySms,
      loginByWechat,
      logout,

      // 状态
      accessToken,
      tokenExpireTime,
      loginInfo,
      isLoggedIn,
      isTokenExpired,
      validToken,
      hasLoginInfo,
      tokenExpiredNotified,

      // 辅助方法
      setTokenInfo,
      clearTokenInfo,
      markTokenExpiredNotified,
      resetTokenExpiredNotified,
      refreshUserInfo,
      postLoginRedirect,

      // ==================== 兼容旧版（标记废弃） ====================
      login,
      wxLogin,
      hasLogin,
      getValidToken,
      updateNowTime,
      tryGetValidToken,
      refreshToken,
      tokenInfo: loginInfo, // 兼容旧版
    }
  },
  {
    // 持久化配置
    persist: {
      paths: ['accessToken', 'tokenExpireTime', 'loginInfo'],
    },
  },
)
