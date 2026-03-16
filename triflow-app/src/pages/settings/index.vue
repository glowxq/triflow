/**
 * 设置页面
 * @description 账户相关设置与信息展示（仅管理员可访问）
 * @design 流动科技感 + 管理面板风格
 */

<script lang="ts" setup>
import type { SysConfigVO, SysSwitchVO } from '@/api/types/system'
import { getConfigList, getSwitchList, toggleSwitch, updateConfigValue } from '@/api/system'
import { useAccess } from '@/hooks/useAccess'
import { useTokenStore, useUserStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

definePage({
  style: {
    navigationBarTitleText: '设置',
    navigationStyle: 'custom',
  },
  needLogin: true,
})

const statusBarHeight = ref(sysStatusBarHeight || 20)
const userStore = useUserStore()
const tokenStore = useTokenStore()
const { isAdmin } = useAccess()
const isAdminUser = computed(() => isAdmin())
const pageLoaded = ref(false)

const tokenExpireText = computed(() => {
  if (!tokenStore.tokenExpireTime || !tokenStore.accessToken)
    return '未登录'
  const date = new Date(tokenStore.tokenExpireTime)
  if (Number.isNaN(date.getTime()))
    return '未知'
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hour = `${date.getHours()}`.padStart(2, '0')
  const minute = `${date.getMinutes()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
})

const tokenRemainText = computed(() => {
  if (!tokenStore.tokenExpireTime || !tokenStore.accessToken)
    return '--'
  const diff = tokenStore.tokenExpireTime - Date.now()
  if (diff <= 0)
    return '已过期'
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  if (days > 0)
    return `${days}天`
  if (hours > 0)
    return `${hours}小时`
  return `${minutes}分钟`
})

const switchList = ref<SysSwitchVO[]>([])
const configList = ref<SysConfigVO[]>([])
const switchLoading = ref(false)
const configLoading = ref(false)
const switchLoadingMap = reactive<Record<number, boolean>>({})
const configSavingMap = reactive<Record<number, boolean>>({})

async function loadSwitches() {
  if (!isAdminUser.value)
    return
  switchLoading.value = true
  try {
    switchList.value = await getSwitchList()
  }
  catch (error) {
    console.error('获取开关列表失败:', error)
    uni.showToast({ title: '开关加载失败', icon: 'none' })
  }
  finally {
    switchLoading.value = false
  }
}

async function loadConfigs() {
  if (!isAdminUser.value)
    return
  configLoading.value = true
  try {
    configList.value = await getConfigList()
  }
  catch (error) {
    console.error('获取配置列表失败:', error)
    uni.showToast({ title: '配置加载失败', icon: 'none' })
  }
  finally {
    configLoading.value = false
  }
}

async function refreshAdminData() {
  if (!isAdminUser.value)
    return
  await Promise.all([loadSwitches(), loadConfigs()])
}

async function handleSwitchChange(item: SysSwitchVO, event: any) {
  const nextValue = event?.detail?.value ? 1 : 0
  if (item.switchValue === nextValue)
    return
  switchLoadingMap[item.id] = true
  const previous = item.switchValue
  item.switchValue = nextValue
  try {
    await toggleSwitch(item.id, nextValue, '小程序设置')
    uni.showToast({ title: '开关已更新', icon: 'success' })
  }
  catch (error) {
    item.switchValue = previous
    console.error('更新开关失败:', error)
    uni.showToast({ title: '更新失败', icon: 'none' })
  }
  finally {
    switchLoadingMap[item.id] = false
  }
}

async function handleSaveConfig(item: SysConfigVO) {
  if (!item?.id)
    return
  configSavingMap[item.id] = true
  try {
    await updateConfigValue(item.id, item.configValue || '')
    uni.showToast({ title: '配置已保存', icon: 'success' })
  }
  catch (error) {
    console.error('更新配置失败:', error)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
  finally {
    configSavingMap[item.id] = false
  }
}

function handleGoBack() {
  uni.navigateBack({
    fail: () => {
      uni.switchTab({ url: '/pages/me/me' })
    },
  })
}

onShow(() => {
  if (!isAdminUser.value) {
    uni.showToast({ title: '暂无权限访问', icon: 'none' })
    setTimeout(() => {
      uni.navigateBack()
    }, 300)
    return
  }
  refreshAdminData()
})

onLoad(() => {
  setTimeout(() => {
    pageLoaded.value = true
  }, 100)
})
</script>

<template>
  <view class="settings-page">
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
        <text class="nav-title">系统设置</text>
        <view class="nav-placeholder" />
      </view>
    </view>

    <!-- 主内容 -->
    <view class="content-scroll" :style="{ paddingTop: `${statusBarHeight + 56}px` }">
      <view class="main-content" :class="{ 'is-loaded': pageLoaded }">
        <!-- 页面标题 -->
        <view class="page-title">
          <view class="title-icon-wrapper">
            <view class="i-carbon-settings title-icon" />
          </view>
          <view class="title-text">
            <text class="title-main">管理控制台</text>
            <text class="title-sub">管理系统开关与配置</text>
          </view>
        </view>

        <!-- 账户信息卡片 -->
        <view class="card">
          <view class="card-header">
            <view class="card-icon-wrapper blue">
              <view class="i-carbon-user-avatar card-icon" />
            </view>
            <text class="card-title">账户信息</text>
          </view>

          <view class="info-list">
            <view class="info-item">
              <text class="info-label">用户名</text>
              <text class="info-value">{{ userStore.userInfo.username || '未登录' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">昵称</text>
              <text class="info-value">{{ userStore.userInfo.nickname || '未设置' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">Token 有效期</text>
              <text class="info-value">{{ tokenExpireText }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">剩余时间</text>
              <view class="remain-tag" :class="{ warning: tokenRemainText === '已过期' }">
                {{ tokenRemainText }}
              </view>
            </view>
          </view>
        </view>

        <!-- 系统开关卡片 -->
        <view v-if="isAdminUser" class="card">
          <view class="card-header">
            <view class="card-icon-wrapper indigo">
              <view class="i-carbon-toggle-off card-icon" />
            </view>
            <view class="card-header-content">
              <text class="card-title">系统开关</text>
              <text class="card-desc">管理功能启用状态</text>
            </view>
            <view class="refresh-btn" @click="loadSwitches">
              <view class="i-carbon-renew" :class="{ spinning: switchLoading }" />
            </view>
          </view>

          <view v-if="switchList.length" class="switch-list">
            <view v-for="item in switchList" :key="item.id" class="switch-item">
              <view class="switch-content">
                <text class="switch-name">{{ item.switchName }}</text>
                <text class="switch-key">{{ item.switchKey }}</text>
                <text v-if="item.description" class="switch-desc">{{ item.description }}</text>
              </view>
              <switch
                :checked="item.switchValue === 1"
                color="#0ea5e9"
                :disabled="switchLoadingMap[item.id]"
                @change="handleSwitchChange(item, $event)"
              />
            </view>
          </view>
          <view v-else-if="!switchLoading" class="empty-tip">
            <view class="i-carbon-data-base empty-icon" />
            <text>暂无开关数据</text>
          </view>
        </view>

        <!-- 系统配置卡片 -->
        <view v-if="isAdminUser" class="card">
          <view class="card-header">
            <view class="card-icon-wrapper purple">
              <view class="i-carbon-settings-adjust card-icon" />
            </view>
            <view class="card-header-content">
              <text class="card-title">系统配置</text>
              <text class="card-desc">可直接修改配置值并保存</text>
            </view>
            <view class="refresh-btn" @click="loadConfigs">
              <view class="i-carbon-renew" :class="{ spinning: configLoading }" />
            </view>
          </view>

          <view v-if="configList.length" class="config-list">
            <view v-for="item in configList" :key="item.id" class="config-item">
              <view class="config-header">
                <text class="config-name">{{ item.configName }}</text>
                <text class="config-key">{{ item.configKey }}</text>
              </view>
              <view class="config-body">
                <input
                  v-model="item.configValue"
                  class="config-input"
                  type="text"
                  placeholder="请输入配置值"
                >
                <button
                  class="save-btn"
                  :loading="configSavingMap[item.id]"
                  @click="handleSaveConfig(item)"
                >
                  <view v-if="!configSavingMap[item.id]" class="i-carbon-save save-icon" />
                  <text>保存</text>
                </button>
              </view>
            </view>
          </view>
          <view v-else-if="!configLoading" class="empty-tip">
            <view class="i-carbon-data-base empty-icon" />
            <text>暂无配置数据</text>
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

$indigo: #6366f1;
$purple: #8b5cf6;
$warning: #f59e0b;
$error: #ef4444;

// ==================== 页面布局 ====================
.settings-page {
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
    background: linear-gradient(135deg, rgba($indigo, 0.3) 0%, rgba($primary, 0.15) 100%);
    top: -80rpx;
    right: -80rpx;
    animation: float1 8s ease-in-out infinite;
  }

  &--2 {
    width: 280rpx;
    height: 280rpx;
    background: linear-gradient(135deg, rgba($primary, 0.25) 0%, rgba($purple, 0.08) 100%);
    top: 180rpx;
    left: -100rpx;
    animation: float2 10s ease-in-out infinite;
  }

  &--3 {
    width: 200rpx;
    height: 200rpx;
    background: linear-gradient(135deg, rgba($primary-lighter, 0.3) 0%, rgba($indigo, 0.1) 100%);
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

// ==================== 页面标题 ====================
.page-title {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 12rpx 0;
}

.title-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  background: linear-gradient(135deg, $indigo 0%, $purple 100%);
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 14px rgba($indigo, 0.25);
}

.title-icon {
  font-size: 40rpx;
  color: #ffffff;
}

.title-text {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.title-main {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
}

.title-sub {
  font-size: 24rpx;
  color: $text-secondary;
}

// ==================== 卡片 ====================
.card {
  background: $bg-card;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: $shadow-md;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.card-icon-wrapper {
  width: 48rpx;
  height: 48rpx;
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &.blue {
    background: rgba($primary, 0.1);

    .card-icon {
      color: $primary;
    }
  }

  &.indigo {
    background: rgba($indigo, 0.1);

    .card-icon {
      color: $indigo;
    }
  }

  &.purple {
    background: rgba($purple, 0.1);

    .card-icon {
      color: $purple;
    }
  }
}

.card-icon {
  font-size: 28rpx;
}

.card-header-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.card-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.card-desc {
  font-size: 22rpx;
  color: $text-tertiary;
}

.refresh-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: $bg-input;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.95);
  }

  view {
    font-size: 32rpx;
    color: $text-secondary;

    &.spinning {
      animation: spin 1s linear infinite;
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// ==================== 账户信息 ====================
.info-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 16rpx;
  background: $bg-input;
  border-radius: 12rpx;
}

.info-label {
  font-size: 26rpx;
  color: $text-tertiary;
}

.info-value {
  font-size: 26rpx;
  color: $text-primary;
  font-weight: 500;
}

.remain-tag {
  padding: 6rpx 16rpx;
  background: rgba($primary, 0.1);
  border-radius: 16rpx;
  font-size: 24rpx;
  color: $primary;
  font-weight: 500;

  &.warning {
    background: rgba($error, 0.1);
    color: $error;
  }
}

// ==================== 开关列表 ====================
.switch-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.switch-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 20rpx;
  background: $bg-input;
  border-radius: 16rpx;
}

.switch-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.switch-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.switch-key {
  font-size: 22rpx;
  color: $text-secondary;
  word-break: break-all;
}

.switch-desc {
  font-size: 22rpx;
  color: $text-tertiary;
}

// ==================== 配置列表 ====================
.config-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.config-item {
  padding: 20rpx;
  background: $bg-input;
  border-radius: 16rpx;
}

.config-header {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  margin-bottom: 12rpx;
}

.config-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.config-key {
  font-size: 22rpx;
  color: $text-secondary;
}

.config-body {
  display: flex;
  gap: 12rpx;
}

.config-input {
  flex: 1;
  height: 72rpx;
  padding: 0 20rpx;
  background: $bg-card;
  border: 2rpx solid $border-color;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: $text-primary;
}

.save-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  height: 72rpx;
  padding: 0 24rpx;
  background: $primary-gradient;
  border-radius: 12rpx;
  border: none;
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 500;

  &::after {
    border: none;
  }
}

.save-icon {
  font-size: 28rpx;
}

// ==================== 空状态 ====================
.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 40rpx 0;
  color: $text-tertiary;
}

.empty-icon {
  font-size: 48rpx;
}

// ==================== 底部安全区域 ====================
.safe-bottom {
  height: calc(env(safe-area-inset-bottom, 20px) + 120rpx);
}
</style>
