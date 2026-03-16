/**
 * 帮助与反馈页面
 * @description 使用 CmsText 组件展示帮助反馈内容，采用流动科技感设计
 */

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { TextKey } from '@/api/types/cms'
import CmsText from '@/components/cms-text/cms-text.vue'

defineOptions({
  name: 'HelpFeedback',
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

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync()
  statusBarHeight.value = sysInfo.statusBarHeight || 0

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
  <view class="help-page">
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
        <text class="nav-title">帮助与反馈</text>
        <view class="nav-placeholder" />
      </view>
    </view>

    <!-- 页面内容 -->
    <view class="page-content" :style="{ paddingTop: statusBarHeight + 44 + 'px' }">
      <view class="content-wrapper" :class="{ 'content-loaded': pageLoaded }">
        <!-- 页面头部图标 -->
        <view class="page-header">
          <view class="header-icon">
            <text class="icon-emoji">💬</text>
          </view>
          <text class="header-title">帮助与反馈</text>
          <text class="header-subtitle">遇到问题？我们随时为您服务</text>
        </view>

        <!-- CMS 内容卡片 -->
        <view class="content-card">
          <CmsText
            :text-key="TextKey.HELP_FEEDBACK"
            :show-title="false"
            :show-update-time="true"
          />
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

.help-page {
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
    top: 30%;
    left: -50rpx;
    animation: float2 10s ease-in-out infinite;
  }

  &--3 {
    width: 240rpx;
    height: 240rpx;
    background: linear-gradient(135deg, rgba($primary-lighter, 0.1) 0%, rgba($primary, 0.05) 100%);
    bottom: 20%;
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

// 页面头部
.page-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0 48rpx;
}

.header-icon {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $shadow-primary;
  margin-bottom: 24rpx;
}

.icon-emoji {
  font-size: 56rpx;
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12rpx;
}

.header-subtitle {
  font-size: 28rpx;
  color: #64748b;
}

// CMS 内容卡片
.content-card {
  background: #ffffff;
  border-radius: 28rpx;
  padding: 40rpx;
  box-shadow: $shadow-md;
  margin-bottom: 32rpx;

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
