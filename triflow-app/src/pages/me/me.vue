/**
 * 我的页面
 * @description 用户个人中心，支持登录状态显示、个人资料和功能入口
 * @design 流动科技感 + 简洁紧凑（一屏展示）
 */

<script lang="ts" setup>
import type { WalletSignInStatusVO } from '@/api/types/wallet'
import { storeToRefs } from 'pinia'
import { getPublicSwitchStatus } from '@/api/system'
import { getSignInStatus, signIn } from '@/api/wallet'
import { useAccess } from '@/hooks/useAccess'
import { useTokenStore, useUserStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

definePage({
  style: {
    navigationBarTitleText: '我的',
    navigationStyle: 'custom',
    disableScroll: true,
  },
})

// ==================== 状态定义 ====================

/** 状态栏高度 */
const statusBarHeight = ref(sysStatusBarHeight || 20)

/** 页面加载动画 */
const pageLoaded = ref(false)

// ==================== Store ====================

const tokenStore = useTokenStore()
const userStore = useUserStore()
const { isAdmin } = useAccess()
const { isLoggedIn } = storeToRefs(tokenStore)
const { displayName, avatar, userInfo, roles } = storeToRefs(userStore)
const isAdminUser = computed(() => isAdmin())
const roleLabel = computed(() => {
  if (!roles.value?.length)
    return '无角色'
  return roles.value.join(' / ')
})

// ==================== 签到/服务 ====================

const signInEnabled = ref(false)
const signInStatus = ref<WalletSignInStatusVO | null>(null)
const signInLoading = ref(false)

/** 累积奖励提示显示状态 */
const showRewardTips = ref(false)

// ==================== 菜单配置 ====================
/** 功能菜单 - 网格图标形式 */
interface MenuItem {
  icon: string
  label: string
  path: string
  needLogin: boolean
  adminOnly?: boolean
  color?: string
}

const baseMenuItems: MenuItem[] = [
  { icon: 'i-carbon-user-avatar', label: '个人资料', path: '/pages/profile/index', needLogin: true, color: '#0ea5e9' },
  { icon: 'i-carbon-receipt', label: '账户明细', path: '/pages/wallet/index', needLogin: true, color: '#8b5cf6' },
  { icon: 'i-carbon-list', label: '充值订单', path: '/pages/wallet/order/index', needLogin: true, color: '#f59e0b' },
  { icon: 'i-carbon-currency-yen', label: '充值中心', path: '/pages/wallet/recharge/index', needLogin: true, color: '#10b981' },
  { icon: 'i-carbon-settings', label: '设置', path: '/pages/settings/index', needLogin: true, adminOnly: true, color: '#6366f1' },
  { icon: 'i-carbon-notification', label: '消息通知', path: '/pages/notification/index', needLogin: true, color: '#ec4899' },
  { icon: 'i-carbon-help', label: '帮助反馈', path: '/pages/help/index', needLogin: false, color: '#14b8a6' },
  { icon: 'i-carbon-information', label: '关于我们', path: '/pages/about/index', needLogin: false, color: '#64748b' },
]

const menuItems = computed(() => baseMenuItems.filter(item => !item.adminOnly || isAdminUser.value))

// ==================== Methods ====================

/**
 * 跳转登录页
 */
function goToLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

/**
 * 跳转个人资料页
 */
function goToProfile() {
  if (!isLoggedIn.value) {
    goToLogin()
    return
  }
  uni.navigateTo({
    url: '/pages/profile/index',
    fail: () => {
      uni.showToast({ title: '页面开发中', icon: 'none' })
    },
  })
}

/**
 * 点击菜单项
 */
function onMenuClick(item: MenuItem) {
  // 需要登录的页面
  if (item.needLogin && !isLoggedIn.value) {
    uni.showModal({
      title: '提示',
      content: '请先登录',
      confirmText: '去登录',
      success: (res) => {
        if (res.confirm) {
          goToLogin()
        }
      },
    })
    return
  }

  uni.navigateTo({
    url: item.path,
    fail: () => {
      uni.showToast({ title: '页面开发中', icon: 'none' })
    },
  })
}


/**
 * 退出登录
 */
async function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    confirmText: '确定退出',
    cancelText: '取消',
    success: async (res) => {
      if (res.confirm) {
        await tokenStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    },
  })
}

async function fetchSignInEnabled() {
  try {
    signInEnabled.value = await getPublicSwitchStatus('wallet.signin.enabled')
  }
  catch (error) {
    console.error('获取签到开关失败:', error)
    signInEnabled.value = false
  }
}

async function fetchSignInStatus() {
  if (!signInEnabled.value) {
    signInStatus.value = null
    return
  }
  if (!isLoggedIn.value) {
    signInStatus.value = null
    return
  }
  try {
    signInStatus.value = await getSignInStatus()
  }
  catch (error) {
    console.error('获取签到状态失败:', error)
  }
}

async function refreshSignInState() {
  await fetchSignInEnabled()
  await fetchSignInStatus()
}

async function handleSignIn() {
  if (!signInEnabled.value) {
    uni.showToast({ title: '签到功能已关闭', icon: 'none' })
    return
  }
  if (!isLoggedIn.value) {
    uni.showModal({
      title: '提示',
      content: '请先登录后再签到',
      confirmText: '去登录',
      success: (res) => {
        if (res.confirm) {
          uni.navigateTo({ url: '/pages/login/index' })
        }
      },
    })
    return
  }
  if (signInStatus.value?.signedToday) {
    uni.showToast({ title: '今天已签到', icon: 'none' })
    return
  }
  signInLoading.value = true
  try {
    await signIn()
    await userStore.fetchUserInfo()
    await fetchSignInStatus()
    uni.showToast({ title: '签到成功', icon: 'success' })
  }
  catch (error) {
    console.error('签到失败:', error)
  }
  finally {
    signInLoading.value = false
  }
}

function handleContactFallback() {
  uni.showToast({ title: '请在微信小程序内联系客服', icon: 'none' })
}

onShow(() => {
  refreshSignInState()
})

onLoad(() => {
  // 延迟启动入场动画
  setTimeout(() => {
    pageLoaded.value = true
  }, 100)
})

onShareAppMessage(() => {
  return {
    title: 'Triflow - 轻松管理积分与余额',
    path: '/pages/index/index',
  }
})

onShareTimeline(() => {
  return {
    title: 'Triflow - 轻松管理积分与余额',
    query: '',
  }
})
</script>

<template>
  <view class="me-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle--1" />
      <view class="bg-circle bg-circle--2" />
      <view class="bg-circle bg-circle--3" />
    </view>

    <!-- 顶部安全区域 -->
    <view :style="{ height: `${statusBarHeight + 12}px` }" />

    <!-- 主内容区域 -->
    <view class="main-content" :class="{ 'is-loaded': pageLoaded }">
      <!-- 用户卡片 -->
      <view class="user-card" @click="isLoggedIn ? goToProfile() : goToLogin()">
        <!-- 已登录状态 -->
        <template v-if="isLoggedIn">
          <view class="user-avatar-wrapper">
            <image
              :src="avatar || '/static/images/default-avatar.png'"
              class="user-avatar"
              mode="aspectFill"
            />
            <view class="avatar-glow" />
          </view>
          <view class="user-info">
            <view class="user-name">
              {{ displayName }}
            </view>
            <view class="user-meta">
              <text class="user-phone">{{ userInfo?.phone || '完善资料' }}</text>
              <view class="user-role-tag">{{ roleLabel }}</view>
            </view>
          </view>
        </template>

        <!-- 未登录状态 -->
        <template v-else>
          <view class="user-avatar-wrapper">
            <view class="user-avatar-placeholder">
              <view class="i-carbon-user" />
            </view>
            <view class="avatar-glow" />
          </view>
          <view class="user-info">
            <view class="user-name">
              点击登录
            </view>
            <view class="user-meta">
              <text class="user-phone">登录后享受更多服务</text>
            </view>
          </view>
        </template>

        <view class="i-carbon-chevron-right arrow-icon" />
      </view>

      <!-- 资产卡片（登录后显示） -->
      <view v-if="isLoggedIn" class="assets-card">
        <view class="assets-header">
          <text class="assets-title">我的资产</text>
          <!-- 签到入口 -->
          <view
            v-if="signInEnabled"
            class="sign-in-btn"
            :class="{ signed: signInStatus?.signedToday }"
            @click.stop="handleSignIn"
          >
            <view class="i-carbon-calendar sign-icon" />
            <text>{{ signInStatus?.signedToday ? '已签到' : '签到' }}</text>
          </view>
        </view>
        <view class="assets-grid">
          <view class="asset-item">
            <view class="asset-value">{{ userInfo?.points ?? 0 }}</view>
            <view class="asset-label">可用积分</view>
          </view>
          <view class="asset-divider" />
          <view class="asset-item">
            <view class="asset-value frozen">{{ userInfo?.frozenPoints ?? 0 }}</view>
            <view class="asset-label">冻结积分</view>
          </view>
          <view class="asset-divider" />
          <view class="asset-item">
            <view class="asset-value reward">{{ userInfo?.rewardPoints ?? 0 }}</view>
            <view class="asset-label-with-tip">
              <text class="asset-label">累积奖励</text>
              <view class="tip-trigger" @click.stop="showRewardTips = !showRewardTips">
                <view class="i-carbon-help" />
              </view>
              <view v-if="showRewardTips" class="tip-bubble" @click.stop>
                <text>累积奖励是通过签到、活动、赠送等方式获得的积分总额，不包括充值积分。</text>
                <view class="tip-close" @click.stop="showRewardTips = false">
                  <view class="i-carbon-close" />
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 功能网格 -->
      <view class="menu-grid">
        <view
          v-for="item in menuItems"
          :key="item.label"
          class="menu-item"
          @click="onMenuClick(item)"
        >
          <view class="menu-icon-wrapper" :style="{ background: `${item.color}15` }">
            <view class="menu-icon" :class="item.icon" :style="{ color: item.color }" />
          </view>
          <text class="menu-label">{{ item.label }}</text>
        </view>
      </view>

      <!-- 服务入口（联系客服、分享） -->
      <view class="service-row">
        <!-- #ifdef MP-WEIXIN -->
        <button class="service-btn contact-btn" open-type="contact">
          <view class="i-carbon-customer-service service-icon" />
          <text>联系客服</text>
        </button>
        <button class="service-btn share-btn" open-type="share">
          <view class="i-carbon-share service-icon" />
          <text>分享应用</text>
        </button>
        <!-- #endif -->
        <!-- #ifndef MP-WEIXIN -->
        <view class="service-btn" @click="handleContactFallback">
          <view class="i-carbon-customer-service service-icon" />
          <text>联系客服</text>
        </view>
        <view class="service-btn" @click="handleContactFallback">
          <view class="i-carbon-share service-icon" />
          <text>分享应用</text>
        </view>
        <!-- #endif -->
      </view>

      <!-- 退出登录（仅登录后显示） -->
      <view v-if="isLoggedIn" class="logout-btn" @click="handleLogout">
        <view class="i-carbon-logout logout-icon" />
        <text>退出登录</text>
      </view>
    </view>

    <!-- 底部版本信息 -->
    <view class="footer">
      <text>Triflow v1.0.0</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
// ==================== 设计变量 ====================
// 主题色 - 天蓝色系（与登录页面一致）
$primary: #0ea5e9;
$primary-light: #38bdf8;
$primary-lighter: #7dd3fc;
$primary-dark: #0284c7;
$primary-gradient: linear-gradient(135deg, $primary 0%, $primary-light 100%);

// 文字颜色
$text-primary: #0f172a;
$text-secondary: #475569;
$text-tertiary: #94a3b8;
$text-placeholder: #cbd5e1;

// 背景色
$bg-page: #f8fafc;
$bg-card: #ffffff;
$bg-input: #f1f5f9;

// 边框与阴影
$border-color: #e2e8f0;
$shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
$shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
$shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1);
$shadow-primary: 0 4px 14px rgba($primary, 0.25);

// ==================== 页面布局 ====================
.me-page {
  height: 100vh;
  overflow: hidden;
  background: $bg-page;
  position: relative;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

// ==================== 背景装饰 ====================
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;

  &--1 {
    width: 360rpx;
    height: 360rpx;
    background: linear-gradient(135deg, rgba($primary-light, 0.35) 0%, rgba($primary, 0.15) 100%);
    top: -80rpx;
    right: -80rpx;
    animation: float1 8s ease-in-out infinite;
  }

  &--2 {
    width: 280rpx;
    height: 280rpx;
    background: linear-gradient(135deg, rgba($primary, 0.25) 0%, rgba($primary-light, 0.08) 100%);
    top: 180rpx;
    left: -100rpx;
    animation: float2 10s ease-in-out infinite;
  }

  &--3 {
    width: 200rpx;
    height: 200rpx;
    background: linear-gradient(135deg, rgba($primary-lighter, 0.3) 0%, rgba($primary, 0.1) 100%);
    bottom: 200rpx;
    right: -40rpx;
    animation: float3 12s ease-in-out infinite;
  }
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-10px, 15px) scale(1.05); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(15px, -10px) scale(1.1); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-8px, -12px) scale(1.08); }
}

// ==================== 主内容 ====================
.main-content {
  position: relative;
  z-index: 1;
  flex: 1;
  padding: 0 28rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  opacity: 0;
  transform: translateY(30rpx);
  transition: opacity 0.5s ease, transform 0.5s ease;

  &.is-loaded {
    opacity: 1;
    transform: translateY(0);
  }
}

// ==================== 用户卡片 ====================
.user-card {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: $primary-gradient;
  border-radius: 28rpx;
  box-shadow: $shadow-primary;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.25), transparent 60%);
    pointer-events: none;
  }
}

.user-avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 1;
}

.user-avatar-placeholder {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;

  view {
    font-size: 48rpx;
    color: #ffffff;
  }
}

.avatar-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120rpx;
  height: 120rpx;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  50% { transform: translate(-50%, -50%) scale(1.15); opacity: 0.3; }
}

.user-info {
  flex: 1;
  margin-left: 20rpx;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.user-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 6rpx;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.user-meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-wrap: wrap;
}

.user-phone {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
}

.user-role-tag {
  padding: 4rpx 12rpx;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 20rpx;
  font-size: 20rpx;
  color: #ffffff;
  max-width: 200rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.7);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

// ==================== 资产卡片 ====================
.assets-card {
  background: $bg-card;
  border-radius: 24rpx;
  padding: 20rpx 24rpx;
  box-shadow: $shadow-md;
}

.assets-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.assets-title {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-secondary;
}

.sign-in-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  background: $primary-gradient;
  border-radius: 20rpx;
  font-size: 22rpx;
  color: #ffffff;
  box-shadow: $shadow-primary;
  transition: transform 0.2s ease, opacity 0.2s ease;

  &:active {
    transform: scale(0.96);
  }

  &.signed {
    background: $bg-input;
    color: $text-tertiary;
    box-shadow: none;
  }
}

.sign-icon {
  font-size: 24rpx;
}

.assets-grid {
  display: flex;
  align-items: center;
}

.asset-item {
  flex: 1;
  text-align: center;
}

.asset-value {
  font-size: 32rpx;
  font-weight: 700;
  color: $primary;
  margin-bottom: 4rpx;

  &.frozen {
    color: $text-tertiary;
    font-size: 28rpx;
  }

  &.reward {
    color: #f59e0b;
    font-size: 28rpx;
  }
}

.asset-label {
  font-size: 22rpx;
  color: $text-tertiary;
}

.asset-label-with-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  position: relative;
}

.tip-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28rpx;
  height: 28rpx;
  color: $text-tertiary;
  font-size: 22rpx;
  cursor: pointer;

  &:active {
    opacity: 0.7;
  }
}

.tip-bubble {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-top: 12rpx;
  padding: 16rpx 20rpx;
  background: rgba(0, 0, 0, 0.85);
  border-radius: 12rpx;
  font-size: 22rpx;
  color: #ffffff;
  line-height: 1.5;
  white-space: normal;
  width: 320rpx;
  text-align: left;
  z-index: 100;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

  &::before {
    content: '';
    position: absolute;
    top: -12rpx;
    left: 50%;
    transform: translateX(-50%);
    border-width: 0 12rpx 12rpx 12rpx;
    border-style: solid;
    border-color: transparent transparent rgba(0, 0, 0, 0.85) transparent;
  }
}

.tip-close {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 28rpx;
  height: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.6);
  font-size: 20rpx;

  &:active {
    opacity: 0.7;
  }
}

.asset-divider {
  width: 1rpx;
  height: 40rpx;
  background: $border-color;
}

// ==================== 功能网格 ====================
.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  background: $bg-card;
  border-radius: 24rpx;
  padding: 24rpx 16rpx;
  box-shadow: $shadow-md;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  padding: 12rpx 0;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

.menu-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}

.menu-icon {
  font-size: 40rpx;
}

.menu-label {
  font-size: 22rpx;
  color: $text-secondary;
  text-align: center;
}

// ==================== 服务入口 ====================
.service-row {
  display: flex;
  gap: 16rpx;
}

.service-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  height: 80rpx;
  background: $bg-card;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: $text-secondary;
  box-shadow: $shadow-sm;
  border: none;
  padding: 0;
  margin: 0;
  line-height: 1;
  transition: transform 0.2s ease, background-color 0.2s ease;

  &:active {
    transform: scale(0.98);
    background: $bg-input;
  }
}

.service-icon {
  font-size: 32rpx;
  color: $primary;
}

// ==================== 退出登录 ====================
.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  height: 80rpx;
  background: rgba(239, 68, 68, 0.08);
  border-radius: 20rpx;
  font-size: 28rpx;
  color: #ef4444;
  transition: transform 0.2s ease, background-color 0.2s ease;

  &:active {
    transform: scale(0.98);
    background: rgba(239, 68, 68, 0.15);
  }
}

.logout-icon {
  font-size: 32rpx;
}

// ==================== 底部版本 ====================
.footer {
  text-align: center;
  padding: 20rpx 0;
  // 为自定义 tabbar 预留空间：tabbar高度50px + 安全区域 + 额外间距
  padding-bottom: calc(constant(safe-area-inset-bottom) + 50px + 20rpx);
  padding-bottom: calc(env(safe-area-inset-bottom) + 50px + 20rpx);
  font-size: 22rpx;
  color: $text-placeholder;
}
</style>
