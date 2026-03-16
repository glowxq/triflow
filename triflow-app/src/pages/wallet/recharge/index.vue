/**
 * 充值页面
 * @description 用户充值积分/余额
 * @design 流动科技感 + 档位卡片精美展示
 */

<script lang="ts" setup>
import type { WalletRechargeConfigVO } from '@/api/types/wallet'
import { createRechargeOrder, getRechargeConfigs } from '@/api/wallet'
import { useUserStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

definePage({
  style: {
    navigationBarTitleText: '充值中心',
    navigationStyle: 'custom',
  },
  needLogin: true,
})

const statusBarHeight = ref(sysStatusBarHeight || 20)
const userStore = useUserStore()
const pageLoaded = ref(false)

const tabs = [
  { label: '积分充值', type: 'points' as const, icon: 'i-carbon-star' },
]
const activeTab = ref(0)

const loading = ref(false)
const paying = ref(false)
const configs = ref<WalletRechargeConfigVO[]>([])
const selectedId = ref<number | null>(null)

const activeType = computed(() => 'points' as const)

const currentBalance = computed(() => userStore.userInfo.points ?? 0)

const balanceUnit = computed(() => '积分')

async function fetchConfigs() {
  loading.value = true
  try {
    const list = await getRechargeConfigs(activeType.value)
    configs.value = list.filter(item => item.status === 1)
    selectedId.value = configs.value[0]?.id ?? null
  }
  catch (error) {
    console.error('获取充值配置失败:', error)
    uni.showToast({ title: '加载配置失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

function handleTabChange(index: number) {
  activeTab.value = index
  fetchConfigs()
}

function handleSelect(item: WalletRechargeConfigVO) {
  selectedId.value = item.id
}

function formatAmount(value: number, type: 'points' | 'balance') {
  return type === 'points' ? Math.round(value).toString() : Number(value).toFixed(2)
}

async function handlePay() {
  if (!selectedId.value) {
    uni.showToast({ title: '请选择充值档位', icon: 'none' })
    return
  }
  if (paying.value)
    return

  paying.value = true
  try {
    const res = await createRechargeOrder({ configId: selectedId.value })

    // #ifdef MP-WEIXIN
    await new Promise<void>((resolve, reject) => {
      uni.requestPayment({
        timeStamp: String(res.paymentData.timeStamp),
        nonceStr: res.paymentData.nonceStr,
        package: res.paymentData.package,
        signType: res.paymentData.signType,
        paySign: res.paymentData.paySign,
        success: () => resolve(),
        fail: err => reject(err),
      })
    })
    // #endif

    // #ifndef MP-WEIXIN
    uni.showToast({ title: '仅支持微信小程序支付', icon: 'none' })
    return
    // #endif

    await userStore.fetchUserInfo()
    uni.showToast({ title: '充值成功', icon: 'success' })
  }
  catch (error) {
    console.error('支付失败:', error)
    uni.showToast({ title: '支付未完成', icon: 'none' })
  }
  finally {
    paying.value = false
  }
}

function handleGoBack() {
  uni.navigateBack({
    fail: () => {
      uni.switchTab({ url: '/pages/me/me' })
    },
  })
}

onMounted(() => {
  fetchConfigs()
})

onLoad(() => {
  setTimeout(() => {
    pageLoaded.value = true
  }, 100)
})
</script>

<template>
  <view class="recharge-page">
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
        <text class="nav-title">充值中心</text>
        <view class="nav-placeholder" />
      </view>

      <!-- 余额展示 -->
      <view class="balance-display">
        <text class="balance-label">当前{{ balanceUnit }}</text>
        <view class="balance-value">
          <text class="balance-number">{{ currentBalance }}</text>
          <text class="balance-unit">{{ balanceUnit }}</text>
        </view>
      </view>

      <!-- 标签切换 -->
      <view class="tabs">
        <view
          v-for="(tab, index) in tabs"
          :key="tab.type"
          class="tab-item"
          :class="{ 'is-active': activeTab === index }"
          @click="handleTabChange(index)"
        >
          <view :class="tab.icon" class="tab-icon" />
          <text>{{ tab.label }}</text>
        </view>
      </view>
    </view>

    <!-- 主内容 -->
    <view class="content-scroll" :style="{ paddingTop: `${statusBarHeight + 200}px` }">
      <view class="main-content" :class="{ 'is-loaded': pageLoaded }">
        <!-- 档位选择 -->
        <view class="section">
          <view class="section-header">
            <view class="section-icon-wrapper">
              <view class="i-carbon-currency-dollar section-icon" />
            </view>
            <text class="section-title">选择充值档位</text>
          </view>

          <view v-if="configs.length" class="config-grid">
            <view
              v-for="item in configs"
              :key="item.id"
              class="config-card"
              :class="{ 'is-active': selectedId === item.id }"
              @click="handleSelect(item)"
            >
              <view v-if="item.bonusAmount" class="bonus-tag">
                <view class="i-carbon-gift bonus-icon" />
                <text>赠{{ formatAmount(item.bonusAmount, item.type) }}</text>
              </view>

              <view class="config-price">
                <text class="price-symbol">¥</text>
                <text class="price-value">{{ Number(item.payAmount).toFixed(2) }}</text>
              </view>

              <view class="config-reward">
                到账 {{ formatAmount(item.rewardAmount, item.type) }} {{ item.type === 'points' ? '积分' : '元' }}
              </view>

              <view class="config-name">
                {{ item.configName }}
              </view>

              <view v-if="selectedId === item.id" class="check-mark">
                <view class="i-carbon-checkmark" />
              </view>
            </view>
          </view>

          <view v-else-if="!loading" class="empty-state">
            <view class="empty-icon-wrapper">
              <view class="i-carbon-wallet empty-icon" />
            </view>
            <text class="empty-text">暂无可用充值档位</text>
          </view>
        </view>

        <!-- 充值说明 -->
        <view class="tips-card">
          <view class="tips-header">
            <view class="i-carbon-information tips-icon" />
            <text class="tips-title">充值说明</text>
          </view>
          <view class="tips-list">
            <text class="tips-item">• 充值后{{ activeType === 'points' ? '积分' : '余额' }}即时到账</text>
            <text class="tips-item">• 充值成功后不支持退款</text>
            <text class="tips-item">• 如有问题请联系客服</text>
          </view>
        </view>

        <view class="safe-bottom" />
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="action-bar">
      <view class="action-info">
        <text class="action-label">支付金额</text>
        <view class="action-price">
          <text class="price-symbol">¥</text>
          <text class="price-value">{{ configs.find(c => c.id === selectedId)?.payAmount?.toFixed(2) || '0.00' }}</text>
        </view>
      </view>
      <button class="pay-btn" :loading="paying" :disabled="!selectedId" @click="handlePay">
        <view v-if="!paying" class="i-carbon-wallet btn-icon" />
        <text>立即充值</text>
      </button>
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

$green: #10b981;
$orange: #f97316;

// ==================== 页面布局 ====================
.recharge-page {
  min-height: 100vh;
  background: $bg-page;
  position: relative;
  overflow: hidden;
  padding-bottom: 140rpx;
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
    background: linear-gradient(135deg, rgba($green, 0.3) 0%, rgba($primary, 0.15) 100%);
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
    background: linear-gradient(135deg, rgba($primary-lighter, 0.3) 0%, rgba($green, 0.1) 100%);
    bottom: 300rpx;
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
  background: $primary-gradient;
  border-radius: 0 0 40rpx 40rpx;
  box-shadow: $shadow-primary;
  padding-bottom: 24rpx;
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
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.95);
  }

  view {
    font-size: 36rpx;
    color: #ffffff;
  }
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #ffffff;
}

.nav-placeholder {
  width: 72rpx;
}

.balance-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0 20rpx;
}

.balance-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8rpx;
}

.balance-value {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
}

.balance-number {
  font-size: 56rpx;
  font-weight: 700;
  color: #ffffff;
}

.balance-unit {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.tabs {
  display: flex;
  justify-content: center;
  gap: 16rpx;
  padding: 0 24rpx;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 14rpx 32rpx;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.75);
  border-radius: 32rpx;
  background: rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;

  &.is-active {
    background: #ffffff;
    color: $primary;
    font-weight: 600;
    box-shadow: $shadow-md;
  }
}

.tab-icon {
  font-size: 28rpx;
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
  background: rgba($green, 0.1);
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-icon {
  font-size: 28rpx;
  color: $green;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

// ==================== 档位卡片 ====================
.config-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}

.config-card {
  position: relative;
  background: $bg-input;
  border-radius: 20rpx;
  padding: 24rpx 20rpx;
  border: 3rpx solid transparent;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;

  &.is-active {
    border-color: $primary;
    background: rgba($primary, 0.05);
    box-shadow: $shadow-primary;
  }
}

.bonus-tag {
  position: absolute;
  top: -4rpx;
  right: -4rpx;
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 6rpx 12rpx;
  background: linear-gradient(135deg, $orange 0%, #fb923c 100%);
  border-radius: 0 20rpx 0 16rpx;
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 500;
}

.bonus-icon {
  font-size: 20rpx;
}

.config-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 28rpx;
  font-weight: 600;
  color: $primary;
}

.price-value {
  font-size: 48rpx;
  font-weight: 700;
  color: $primary;
}

.config-reward {
  font-size: 24rpx;
  color: $green;
  font-weight: 500;
}

.config-name {
  font-size: 22rpx;
  color: $text-tertiary;
}

.check-mark {
  position: absolute;
  bottom: 12rpx;
  right: 12rpx;
  width: 36rpx;
  height: 36rpx;
  background: $primary;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  view {
    font-size: 24rpx;
    color: #ffffff;
  }
}

// ==================== 空状态 ====================
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0;
}

.empty-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  background: rgba($primary, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}

.empty-icon {
  font-size: 48rpx;
  color: $primary;
}

.empty-text {
  font-size: 26rpx;
  color: $text-tertiary;
}

// ==================== 充值说明 ====================
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

// ==================== 底部操作栏 ====================
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 28rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom, 20px));
  background: $bg-card;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.action-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.action-label {
  font-size: 22rpx;
  color: $text-tertiary;
}

.action-price {
  display: flex;
  align-items: baseline;

  .price-symbol {
    font-size: 26rpx;
    color: $primary;
  }

  .price-value {
    font-size: 40rpx;
    color: $primary;
  }
}

.pay-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  height: 96rpx;
  background: $primary-gradient;
  border-radius: 24rpx;
  box-shadow: $shadow-primary;
  border: none;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 600;
  transition: transform 0.2s ease, opacity 0.2s ease;

  &:active {
    transform: scale(0.98);
    opacity: 0.9;
  }

  &[disabled] {
    opacity: 0.5;
  }

  &::after {
    border: none;
  }
}

.btn-icon {
  font-size: 36rpx;
}

// ==================== 底部安全区域 ====================
.safe-bottom {
  height: 160rpx;
}
</style>
