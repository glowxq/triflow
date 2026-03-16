/**
 * 微信手机号收集页面
 * @description 登录后根据开关自动引导收集手机号
 */

<script lang="ts" setup>
import { bindWechatPhone } from '@/api/login'
import { useTokenStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

defineOptions({
  name: 'CollectPhone',
})

definePage({
  style: {
    navigationBarTitleText: '绑定手机号',
    navigationStyle: 'custom',
  },
  needLogin: true,
})

const tokenStore = useTokenStore()

const statusBarHeight = ref(sysStatusBarHeight || 20)
const loading = ref(false)

async function goNext() {
  const redirected = await tokenStore.postLoginRedirect({ skipPhoneCheck: true })
  if (!redirected) {
    uni.switchTab({ url: '/pages/index/index' })
  }
}

async function handleSkip() {
  goNext()
}

// #ifdef MP-WEIXIN
async function handleGetPhoneNumber(e: any) {
  if (e.detail.errMsg !== 'getPhoneNumber:ok') {
    uni.showToast({ title: '已取消授权', icon: 'none' })
    return
  }

  const code = e.detail.code
  if (!code) {
    uni.showToast({ title: '获取手机号失败', icon: 'none' })
    return
  }

  loading.value = true
  try {
    await bindWechatPhone(code)
    await tokenStore.refreshUserInfo()
    uni.showToast({ title: '手机号绑定成功', icon: 'success' })
    goNext()
  }
  catch (error: any) {
    console.error('绑定手机号失败:', error)
    uni.showToast({ title: error.message || '绑定失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}
// #endif
</script>

<template>
  <view class="collect-phone">
    <view class="header" :style="{ paddingTop: `${statusBarHeight + 24}px` }">
      <view class="title">
        绑定手机号
      </view>
      <view class="subtitle">
        获取更多服务与安全保障
      </view>
    </view>

    <view class="card">
      <view class="icon i-carbon-mobile-check" />
      <view class="card-title">
        授权获取手机号
      </view>
      <view class="card-desc">
        我们将用于账号安全与通知服务，手机号仅用于必要场景
      </view>

      <!-- #ifdef MP-WEIXIN -->
      <button
        class="primary-btn"
        open-type="getPhoneNumber"
        :loading="loading"
        @getphonenumber="handleGetPhoneNumber"
      >
        微信手机号一键绑定
      </button>
      <!-- #endif -->

      <!-- #ifndef MP-WEIXIN -->
      <view class="platform-hint">
        当前平台不支持微信手机号授权
      </view>
      <!-- #endif -->

      <view class="skip" @click="handleSkip">
        稍后绑定
      </view>
    </view>

    <view class="safe-bottom" />
  </view>
</template>

<style lang="scss" scoped>
$primary: #0ea5e9;
$primary-light: #38bdf8;
$text-primary: #1e293b;
$text-secondary: #64748b;
$bg-page: #f8fafc;
$bg-card: #ffffff;

.collect-phone {
  min-height: 100vh;
  background: $bg-page;
}

.header {
  padding: 0 32rpx 24rpx;
}

.title {
  font-size: 40rpx;
  font-weight: 700;
  color: $text-primary;
}

.subtitle {
  margin-top: 8rpx;
  font-size: 26rpx;
  color: $text-secondary;
}

.card {
  margin: 0 32rpx;
  padding: 40rpx 32rpx;
  background: $bg-card;
  border-radius: 24rpx;
  box-shadow: 0 12rpx 32rpx rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.icon {
  font-size: 80rpx;
  color: $primary;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.card-desc {
  font-size: 24rpx;
  color: $text-secondary;
  text-align: center;
}

.primary-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 16rpx;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: #ffffff;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;

  &::after {
    border: none;
  }
}

.platform-hint {
  font-size: 24rpx;
  color: $text-secondary;
}

.skip {
  font-size: 26rpx;
  color: $text-secondary;
}
</style>
