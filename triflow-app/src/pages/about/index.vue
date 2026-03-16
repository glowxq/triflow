/**
 * 关于我们页面
 * @description 使用 CmsText 组件展示关于我们内容，采用流动科技感设计
 */

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { TextKey } from '@/api/types/cms'
import CmsText from '@/components/cms-text/cms-text.vue'

defineOptions({
  name: 'About',
})

definePage({
  style: {
    navigationStyle: 'custom',
  },
})

// 状态栏高度
const statusBarHeight = ref(0)
// 页面加载动画
const pageLoaded = ref(false)

// 应用信息
const appInfo = ref({
  name: 'Triflow',
  version: '1.0.0',
  buildNumber: '2024.01',
})

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync()
  statusBarHeight.value = sysInfo.statusBarHeight || 0

  // 获取应用版本信息
  const accountInfo = uni.getAccountInfoSync?.()
  if (accountInfo?.miniProgram?.version) {
    appInfo.value.version = accountInfo.miniProgram.version
  }

  // 触发入场动画
  setTimeout(() => {
    pageLoaded.value = true
  }, 50)
})

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}
</script>

<template>
  <view class="about-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle--1" />
      <view class="bg-circle bg-circle--2" />
      <view class="bg-circle bg-circle--3" />
    </view>

    <!-- 自定义导航栏 -->
    <view class="nav-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-content">
        <view class="nav-back" @click="goBack">
          <text class="nav-back-icon">‹</text>
        </view>
        <text class="nav-title">关于我们</text>
        <view class="nav-placeholder" />
      </view>
    </view>

    <!-- 页面内容 -->
    <view class="page-content" :style="{ paddingTop: statusBarHeight + 44 + 'px' }">
      <view class="content-wrapper" :class="{ 'content-loaded': pageLoaded }">
        <!-- 品牌展示区域 -->
        <view class="brand-section">
          <view class="brand-logo">
            <image src="/static/logo.svg" class="logo-image" mode="aspectFit" />
          </view>
          <text class="brand-name">{{ appInfo.name }}</text>
          <text class="brand-slogan">三端融合 · 数据流转</text>
          <view class="version-tag">
            <text class="version-text">v{{ appInfo.version }}</text>
          </view>
        </view>

        <!-- CMS 内容卡片 -->
        <view class="content-card">
          <view class="card-header">
            <view class="header-line" />
            <text class="header-text">关于我们</text>
            <view class="header-line" />
          </view>
          <view class="card-body">
            <CmsText
              :text-key="TextKey.ABOUT_US"
              :show-title="false"
              :show-update-time="true"
            />
          </view>
        </view>

        <!-- 底部安全区域 -->
        <view class="safe-bottom" />
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
// 主题色
$primary: #0ea5e9;
$primary-light: #38bdf8;
$primary-lighter: #7dd3fc;

// 阴影
$shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
$shadow-md: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
$shadow-lg: 0 16rpx 48rpx rgba(0, 0, 0, 0.12);
$shadow-primary: 0 8rpx 24rpx rgba($primary, 0.25);

.about-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 30%);
  position: relative;
  overflow: hidden;
}

// 背景装饰
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;

  &--1 {
    width: 300rpx;
    height: 300rpx;
    background: linear-gradient(135deg, rgba($primary, 0.15) 0%, rgba($primary-light, 0.08) 100%);
    top: -80rpx;
    right: -60rpx;
    animation: float1 8s ease-in-out infinite;
  }

  &--2 {
    width: 200rpx;
    height: 200rpx;
    background: linear-gradient(135deg, rgba($primary-light, 0.12) 0%, rgba($primary-lighter, 0.06) 100%);
    top: 35%;
    left: -50rpx;
    animation: float2 10s ease-in-out infinite;
  }

  &--3 {
    width: 240rpx;
    height: 240rpx;
    background: linear-gradient(135deg, rgba($primary-lighter, 0.1) 0%, rgba($primary, 0.05) 100%);
    bottom: 15%;
    right: -40rpx;
    animation: float3 12s ease-in-out infinite;
  }
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(-20rpx, 30rpx) rotate(10deg); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(30rpx, -20rpx) rotate(-10deg); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(-25rpx, 25rpx) rotate(8deg); }
}

// 导航栏
.nav-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1rpx solid rgba($primary, 0.08);
}

.nav-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 24rpx;
}

.nav-back {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba($primary, 0.08);
  transition: all 0.2s ease;

  &:active {
    background: rgba($primary, 0.15);
    transform: scale(0.95);
  }
}

.nav-back-icon {
  font-size: 40rpx;
  color: $primary;
  font-weight: 300;
  margin-top: -4rpx;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1e293b;
}

.nav-placeholder {
  width: 64rpx;
}

// 页面内容
.page-content {
  position: relative;
  z-index: 1;
}

.content-wrapper {
  padding: 32rpx;
  opacity: 0;
  transform: translateY(40rpx);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);

  &.content-loaded {
    opacity: 1;
    transform: translateY(0);
  }
}

// 品牌展示区域
.brand-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0 56rpx;
}

.brand-logo {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 32rpx;
}

.logo-image {
  width: 100%;
  height: 100%;
}

.brand-name {
  font-size: 48rpx;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12rpx;
  letter-spacing: 2rpx;
}

.brand-slogan {
  font-size: 28rpx;
  color: #64748b;
  margin-bottom: 24rpx;
}

.version-tag {
  padding: 8rpx 24rpx;
  background: rgba($primary, 0.08);
  border-radius: 20rpx;
}

.version-text {
  font-size: 24rpx;
  color: $primary;
  font-weight: 500;
}

// CMS 内容卡片
.content-card {
  background: #ffffff;
  border-radius: 28rpx;
  overflow: hidden;
  box-shadow: $shadow-md;
  margin-bottom: 32rpx;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32rpx 36rpx 24rpx;
  gap: 24rpx;
}

.header-line {
  flex: 1;
  height: 2rpx;
  background: linear-gradient(90deg, transparent, rgba($primary, 0.3), transparent);
}

.header-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
}

.card-body {
  padding: 0 36rpx 36rpx;

  :deep(.cms-text) {
    .content-body {
      font-size: 28rpx;
      line-height: 1.8;
      color: #475569;
    }

    .update-time {
      margin-top: 32rpx;
      padding-top: 24rpx;
      border-top: 1rpx solid #f1f5f9;
      font-size: 24rpx;
      color: #94a3b8;
    }
  }
}

// 底部安全区域
.safe-bottom {
  height: calc(env(safe-area-inset-bottom) + 40rpx);
}
</style>
