/**
 * 消息通知页面
 * @description 用于订阅微信小程序消息通知
 * @design 流动科技感 + 通知卡片优雅展示
 */

<script lang="ts" setup>
import type { NotifySubscribeStatus, NotifySubscribeVO, NotifyTemplateVO } from '@/api/types/notify'
import { getNotifySubscribes, getNotifyTemplates } from '@/api/notify'
import { subscribeWechatMessages } from '@/service/notify'
import { useTokenStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

definePage({
  style: {
    navigationBarTitleText: '消息通知',
    navigationStyle: 'custom',
  },
  needLogin: true,
})

// ==================== 常量 ====================

const CHANNEL = 'wechat_miniapp'

const STATUS_META: Record<string, { text: string, color: string, bg: string, icon: string }> = {
  accept: { text: '已订阅', color: '#10b981', bg: 'rgba(16, 185, 129, 0.1)', icon: 'i-carbon-checkmark-filled' },
  reject: { text: '已拒绝', color: '#f59e0b', bg: 'rgba(245, 158, 11, 0.1)', icon: 'i-carbon-close' },
  ban: { text: '已禁用', color: '#ef4444', bg: 'rgba(239, 68, 68, 0.1)', icon: 'i-carbon-subtract' },
  default: { text: '未订阅', color: '#94a3b8', bg: 'rgba(148, 163, 184, 0.1)', icon: 'i-carbon-radio-button' },
}

// ==================== 状态 ====================

const statusBarHeight = ref(sysStatusBarHeight || 20)
const templates = ref<NotifyTemplateVO[]>([])
const subscribes = ref<NotifySubscribeVO[]>([])
const loading = ref(false)
const pageLoaded = ref(false)
const tokenStore = useTokenStore()

const subscribeMap = computed(() => {
  const map = new Map<string, NotifySubscribeStatus>()
  subscribes.value.forEach((item) => {
    map.set(item.templateId, item.subscribeStatus)
  })
  return map
})

const acceptedCount = computed(() => {
  return subscribes.value.filter(s => s.subscribeStatus === 'accept').length
})

// ==================== 方法 ====================

async function loadData() {
  loading.value = true
  try {
    const [templateList, subscribeList] = await Promise.all([
      getNotifyTemplates(CHANNEL),
      getNotifySubscribes(CHANNEL),
    ])
    templates.value = templateList
    subscribes.value = subscribeList
  }
  finally {
    loading.value = false
  }
}

async function handleSubscribe() {
  if (!tokenStore.isLoggedIn) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }

  try {
    loading.value = true
    await subscribeWechatMessages()
    await loadData()
    uni.showToast({ title: '订阅结果已保存', icon: 'success' })
  }
  catch (error: any) {
    const message = error?.message || '订阅失败'
    uni.showToast({ title: message, icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

function handleGoBack() {
  uni.navigateBack({
    fail: () => {
      uni.switchTab({ url: '/pages/me/me' })
    },
  })
}

function getStatusMeta(status?: NotifySubscribeStatus) {
  return STATUS_META[status || 'default'] || STATUS_META.default
}

onMounted(loadData)

onLoad(() => {
  setTimeout(() => {
    pageLoaded.value = true
  }, 100)
})
</script>

<template>
  <view class="notification-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle--1" />
      <view class="bg-circle bg-circle--2" />
      <view class="bg-circle bg-circle--3" />
    </view>

    <!-- 头部导航 -->
    <view class="page-header" :style="{ paddingTop: `${statusBarHeight}px` }">
      <view class="nav-bar">
        <view class="back-btn" @click="handleGoBack">
          <view class="i-carbon-arrow-left" />
        </view>
        <text class="nav-title">消息通知</text>
        <view class="nav-placeholder" />
      </view>
    </view>

    <!-- 主内容 -->
    <view class="content-scroll" :style="{ paddingTop: `${statusBarHeight + 56}px` }">
      <view class="main-content" :class="{ 'is-loaded': pageLoaded }">
        <!-- 订阅卡片 -->
        <view class="subscribe-card">
          <view class="subscribe-icon-wrapper">
            <view class="i-carbon-notification subscribe-icon" />
            <view class="icon-ring" />
          </view>
          <view class="subscribe-content">
            <text class="subscribe-title">订阅消息通知</text>
            <text class="subscribe-desc">订阅后可及时接收业务提醒，订阅状态可在微信"服务通知"中管理。</text>
          </view>
          <button class="subscribe-btn" :loading="loading" @click="handleSubscribe">
            <view v-if="!loading" class="i-carbon-add btn-icon" />
            <text>订阅</text>
          </button>
        </view>

        <!-- 统计卡片 -->
        <view class="stats-card">
          <view class="stat-item">
            <view class="stat-value">{{ templates.length }}</view>
            <view class="stat-label">可订阅</view>
          </view>
          <view class="stat-divider" />
          <view class="stat-item">
            <view class="stat-value highlight">{{ acceptedCount }}</view>
            <view class="stat-label">已订阅</view>
          </view>
          <view class="stat-divider" />
          <view class="stat-item">
            <view class="stat-value">{{ templates.length - acceptedCount }}</view>
            <view class="stat-label">未订阅</view>
          </view>
        </view>

        <!-- 模板列表 -->
        <view class="section">
          <view class="section-header">
            <view class="section-icon-wrapper">
              <view class="i-carbon-document section-icon" />
            </view>
            <text class="section-title">可订阅模板</text>
          </view>

          <view v-if="templates.length" class="template-list">
            <view v-for="item in templates" :key="item.id" class="template-item">
              <view class="template-content">
                <text class="template-name">{{ item.templateName }}</text>
                <text class="template-key">{{ item.templateKey }}</text>
              </view>
              <view
                class="status-tag"
                :style="{
                  color: getStatusMeta(subscribeMap.get(item.templateId)).color,
                  background: getStatusMeta(subscribeMap.get(item.templateId)).bg,
                }"
              >
                <view :class="getStatusMeta(subscribeMap.get(item.templateId)).icon" class="status-icon" />
                <text>{{ getStatusMeta(subscribeMap.get(item.templateId)).text }}</text>
              </view>
            </view>
          </view>

          <view v-else-if="!loading" class="empty-state">
            <view class="empty-icon-wrapper">
              <view class="i-carbon-document empty-icon" />
            </view>
            <text class="empty-title">暂无可订阅模板</text>
            <text class="empty-desc">请稍后再试</text>
          </view>
        </view>

        <!-- 提示说明 -->
        <view class="tips-card">
          <view class="tips-header">
            <view class="i-carbon-information tips-icon" />
            <text class="tips-title">温馨提示</text>
          </view>
          <view class="tips-list">
            <text class="tips-item">• 订阅后将通过微信"服务通知"接收消息</text>
            <text class="tips-item">• 每次订阅仅对一条消息有效</text>
            <text class="tips-item">• 可随时在微信设置中管理订阅状态</text>
          </view>
        </view>

        <view class="safe-bottom" />
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
// ==================== 设计变量 ====================
$primary: #0ea5e9;
$primary-light: #38bdf8;
$primary-lighter: #7dd3fc;
$primary-dark: #0284c7;
$primary-gradient: linear-gradient(135deg, $primary 0%, $primary-light 100%);

$text-primary: #0f172a;
$text-secondary: #475569;
$text-tertiary: #94a3b8;
$text-placeholder: #cbd5e1;

$bg-page: #f8fafc;
$bg-card: #ffffff;
$bg-input: #f1f5f9;

$border-color: #e2e8f0;
$shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
$shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
$shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1);
$shadow-primary: 0 4px 14px rgba($primary, 0.25);

$pink: #ec4899;
$green: #10b981;

// ==================== 页面布局 ====================
.notification-page {
  min-height: 100vh;
  background: $bg-page;
  position: relative;
  overflow: hidden;
}

// ==================== 背景装饰 ====================
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;

  &--1 {
    width: 360rpx;
    height: 360rpx;
    background: linear-gradient(135deg, rgba($pink, 0.3) 0%, rgba($primary, 0.15) 100%);
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
    background: linear-gradient(135deg, rgba($primary-lighter, 0.3) 0%, rgba($pink, 0.1) 100%);
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

// ==================== 头部导航 ====================
.page-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: linear-gradient(180deg, rgba($bg-page, 0.98) 0%, rgba($bg-page, 0.92) 100%);
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 24rpx;
}

.back-btn {
  width: 72rpx;
  height: 72rpx;
  background: $bg-card;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $shadow-md;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.95);
  }

  view {
    font-size: 36rpx;
    color: $text-primary;
  }
}

.nav-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.nav-placeholder {
  width: 72rpx;
}

// ==================== 主内容 ====================
.content-scroll {
  position: relative;
  z-index: 1;
}

.main-content {
  padding: 24rpx 28rpx;
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

// ==================== 订阅卡片 ====================
.subscribe-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 28rpx;
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

.subscribe-icon-wrapper {
  position: relative;
  flex-shrink: 0;
}

.subscribe-icon {
  width: 80rpx;
  height: 80rpx;
  font-size: 44rpx;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.icon-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100rpx;
  height: 100rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: ring-pulse 2s ease-in-out infinite;
}

@keyframes ring-pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.2; }
}

.subscribe-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  position: relative;
  z-index: 1;
}

.subscribe-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #ffffff;
}

.subscribe-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.5;
}

.subscribe-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  height: 72rpx;
  padding: 0 28rpx;
  background: #ffffff;
  border-radius: 36rpx;
  border: none;
  color: $primary;
  font-size: 28rpx;
  font-weight: 600;
  position: relative;
  z-index: 1;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.95);
  }

  &::after {
    border: none;
  }
}

.btn-icon {
  font-size: 28rpx;
}

// ==================== 统计卡片 ====================
.stats-card {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: $bg-card;
  border-radius: 24rpx;
  box-shadow: $shadow-md;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-value {
  font-size: 40rpx;
  font-weight: 700;
  color: $text-primary;

  &.highlight {
    color: $green;
  }
}

.stat-label {
  font-size: 24rpx;
  color: $text-tertiary;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: $border-color;
}

// ==================== 区块 ====================
.section {
  background: $bg-card;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: $shadow-md;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.section-icon-wrapper {
  width: 48rpx;
  height: 48rpx;
  background: rgba($pink, 0.1);
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-icon {
  font-size: 28rpx;
  color: $pink;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

// ==================== 模板列表 ====================
.template-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.template-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 20rpx;
  background: $bg-input;
  border-radius: 16rpx;
}

.template-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.template-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.template-key {
  font-size: 22rpx;
  color: $text-tertiary;
  word-break: break-all;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 500;
  flex-shrink: 0;
}

.status-icon {
  font-size: 24rpx;
}

// ==================== 空状态 ====================
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
}

.empty-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  background: rgba($pink, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}

.empty-icon {
  font-size: 48rpx;
  color: $pink;
}

.empty-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: $text-tertiary;
}

// ==================== 提示说明 ====================
.tips-card {
  background: $bg-card;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: $shadow-sm;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.tips-icon {
  font-size: 28rpx;
  color: $text-tertiary;
}

.tips-title {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-secondary;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.tips-item {
  font-size: 24rpx;
  color: $text-tertiary;
  line-height: 1.6;
}

// ==================== 底部安全区域 ====================
.safe-bottom {
  height: calc(env(safe-area-inset-bottom, 20px) + 40rpx);
}
</style>
