/**
 * 充值订单记录页面
 * @description 展示用户的充值订单列表
 * @design 流动科技感 + 订单卡片精致展示
 */

<script lang="ts" setup>
import type { WalletRechargeOrderQuery, WalletRechargeOrderVO } from '@/api/types/wallet'
import { getMyRechargeOrders } from '@/api/wallet'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

definePage({
  style: {
    navigationBarTitleText: '充值订单',
    navigationStyle: 'custom',
  },
  needLogin: true,
})

const statusBarHeight = ref(sysStatusBarHeight || 20)
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const pageLoaded = ref(false)
const list = ref<WalletRechargeOrderVO[]>([])

const query = reactive<WalletRechargeOrderQuery>({
  pageNum: 1,
  pageSize: 20,
  status: undefined,
})

const tabs = [
  { label: '全部', value: undefined, icon: 'i-carbon-list' },
  { label: '待支付', value: 'pending' as const, icon: 'i-carbon-time' },
  { label: '已支付', value: 'paid' as const, icon: 'i-carbon-checkmark' },
  { label: '已退款', value: 'refunded' as const, icon: 'i-carbon-undo' },
]

const activeTab = ref(0)

const statusMeta: Record<string, { label: string, color: string, bg: string, icon: string }> = {
  pending: { label: '待支付', color: '#f59e0b', bg: 'rgba(245, 158, 11, 0.1)', icon: 'i-carbon-time' },
  paid: { label: '已支付', color: '#10b981', bg: 'rgba(16, 185, 129, 0.1)', icon: 'i-carbon-checkmark-filled' },
  refunded: { label: '已退款', color: '#8b5cf6', bg: 'rgba(139, 92, 246, 0.1)', icon: 'i-carbon-undo' },
  closed: { label: '已关闭', color: '#94a3b8', bg: 'rgba(148, 163, 184, 0.1)', icon: 'i-carbon-close' },
  failed: { label: '支付失败', color: '#ef4444', bg: 'rgba(239, 68, 68, 0.1)', icon: 'i-carbon-warning' },
}

async function loadData(isRefresh = false) {
  if (loading.value)
    return

  if (isRefresh) {
    query.pageNum = 1
    finished.value = false
    refreshing.value = true
  }

  loading.value = true
  try {
    const result = await getMyRechargeOrders(query)
    const records = Array.isArray(result?.records)
      ? result.records
      : Array.isArray((result as any)?.rows)
        ? (result as any).rows
        : []
    const total = Number((result as any)?.total ?? (result as any)?.totalRow ?? records.length)

    if (isRefresh) {
      list.value = records
    }
    else {
      list.value.push(...records)
    }

    if (records.length === 0 || list.value.length >= total) {
      finished.value = true
    }
    else {
      query.pageNum!++
    }
  }
  catch (error) {
    console.error('加载充值订单失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
    refreshing.value = false
  }
}

function onTabChange(index: number) {
  activeTab.value = index
  query.status = tabs[index].value
  loadData(true)
}

function onRefresh() {
  loadData(true)
}

function onLoadMore() {
  if (!finished.value && !loading.value) {
    loadData()
  }
}

function handleGoBack() {
  uni.navigateBack({
    fail: () => {
      uni.switchTab({ url: '/pages/me/me' })
    },
  })
}

function formatAmount(item: WalletRechargeOrderVO): string {
  if (item.type === 'points') {
    return `${item.rewardAmount}`
  }
  return Number(item.rewardAmount).toFixed(2)
}

function formatPayAmount(value?: number): string {
  if (value === undefined || value === null)
    return '-'
  return Number(value).toFixed(2)
}

function formatTime(time?: string): string {
  if (!time)
    return '--'
  const date = new Date(time)
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

function getStatusMeta(status?: string) {
  return statusMeta[status || ''] || { label: '-', color: '#94a3b8', bg: 'rgba(148, 163, 184, 0.1)', icon: 'i-carbon-help' }
}

onMounted(() => {
  loadData(true)
})

onLoad(() => {
  setTimeout(() => {
    pageLoaded.value = true
  }, 100)
})
</script>

<template>
  <view class="order-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle--1" />
      <view class="bg-circle bg-circle--2" />
      <view class="bg-circle bg-circle--3" />
    </view>

    <!-- 固定头部 -->
    <view class="page-header" :style="{ paddingTop: `${statusBarHeight}px` }">
      <view class="nav-bar">
        <view class="back-btn" @click="handleGoBack">
          <view class="i-carbon-arrow-left" />
        </view>
        <text class="nav-title">充值订单</text>
        <view class="nav-placeholder" />
      </view>

      <scroll-view class="tabs-scroll" scroll-x enhanced :show-scrollbar="false">
        <view class="tabs">
          <view
            v-for="(tab, index) in tabs"
            :key="index"
            class="tab-item"
            :class="{ 'is-active': activeTab === index }"
            @click="onTabChange(index)"
          >
            <view :class="tab.icon" class="tab-icon" />
            <text>{{ tab.label }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <scroll-view
      class="list-container"
      :style="{ paddingTop: `${statusBarHeight + 130}px` }"
      scroll-y
      enhanced
      :show-scrollbar="false"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="main-content" :class="{ 'is-loaded': pageLoaded }">
        <view v-if="list.length" class="order-list">
          <view v-for="item in list" :key="item.id" class="order-card">
            <!-- 订单头部 -->
            <view class="order-header">
              <view class="order-type">
                <view class="type-icon-wrapper">
                  <view :class="item.type === 'points' ? 'i-carbon-star' : 'i-carbon-wallet'" class="type-icon" />
                </view>
                <text class="type-label">{{ item.configName || (item.type === 'points' ? '积分充值' : '余额充值') }}</text>
              </view>
              <view
                class="status-tag"
                :style="{ color: getStatusMeta(item.status).color, background: getStatusMeta(item.status).bg }"
              >
                <view :class="getStatusMeta(item.status).icon" class="status-icon" />
                <text>{{ getStatusMeta(item.status).label }}</text>
              </view>
            </view>

            <!-- 订单信息 -->
            <view class="order-body">
              <view class="info-row">
                <text class="info-label">订单号</text>
                <text class="info-value order-no">{{ item.orderNo }}</text>
              </view>
              <view class="info-row highlight">
                <text class="info-label">支付金额</text>
                <text class="info-value pay-amount">¥{{ formatPayAmount(item.payAmount) }}</text>
              </view>
              <view class="info-row">
                <text class="info-label">到账金额</text>
                <text class="info-value">
                  {{ formatAmount(item) }}
                  <text class="unit">{{ item.type === 'points' ? '积分' : '元' }}</text>
                </text>
              </view>
            </view>

            <!-- 订单底部 -->
            <view class="order-footer">
              <view class="i-carbon-time footer-icon" />
              <text>{{ formatTime(item.createTime) }}</text>
            </view>
          </view>
        </view>

        <view v-else-if="!loading" class="empty-state">
          <view class="empty-icon-wrapper">
            <view class="i-carbon-receipt empty-icon" />
          </view>
          <text class="empty-title">暂无充值订单</text>
          <text class="empty-desc">您的充值记录将在这里显示</text>
        </view>

        <view v-if="loading && !refreshing" class="loading-more">
          <wd-loading size="24px" />
          <text>加载中...</text>
        </view>

        <view v-if="finished && list.length" class="no-more">
          <view class="no-more-line" />
          <text>没有更多了</text>
          <view class="no-more-line" />
        </view>

        <view class="safe-bottom" />
      </view>
    </scroll-view>
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

$amber: #f59e0b;

// ==================== 页面布局 ====================
.order-page {
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
    background: linear-gradient(135deg, rgba($amber, 0.3) 0%, rgba($primary, 0.15) 100%);
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
    background: linear-gradient(135deg, rgba($primary-lighter, 0.3) 0%, rgba($amber, 0.1) 100%);
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
  background: $primary-gradient;
  border-radius: 0 0 32rpx 32rpx;
  box-shadow: $shadow-primary;
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

.tabs-scroll {
  width: 100%;
  white-space: nowrap;
}

.tabs {
  display: inline-flex;
  gap: 12rpx;
  padding: 16rpx 24rpx 24rpx;
}

.tab-item {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.75);
  border-radius: 32rpx;
  background: rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
  flex-shrink: 0;

  &.is-active {
    background: #ffffff;
    color: $primary;
    font-weight: 600;
    box-shadow: $shadow-md;
  }
}

.tab-icon {
  font-size: 26rpx;
}

// ==================== 列表容器 ====================
.list-container {
  height: 100vh;
  position: relative;
  z-index: 1;
}

.main-content {
  padding: 24rpx 28rpx;
  opacity: 0;
  transform: translateY(30rpx);
  transition: opacity 0.5s ease, transform 0.5s ease;

  &.is-loaded {
    opacity: 1;
    transform: translateY(0);
  }
}

// ==================== 订单列表 ====================
.order-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.order-card {
  background: $bg-card;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: $shadow-md;
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: linear-gradient(135deg, rgba($primary, 0.05) 0%, rgba($primary-light, 0.02) 100%);
  border-bottom: 1rpx solid $border-color;
}

.order-type {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.type-icon-wrapper {
  width: 48rpx;
  height: 48rpx;
  background: $primary-gradient;
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.type-icon {
  font-size: 28rpx;
  color: #ffffff;
}

.type-label {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 500;
}

.status-icon {
  font-size: 24rpx;
}

.order-body {
  padding: 20rpx 24rpx;
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;

  &.highlight {
    padding: 12rpx 16rpx;
    background: rgba($primary, 0.05);
    border-radius: 12rpx;
    margin: 4rpx 0;
  }
}

.info-label {
  font-size: 26rpx;
  color: $text-tertiary;
}

.info-value {
  font-size: 26rpx;
  color: $text-primary;
  font-weight: 500;

  &.order-no {
    font-size: 24rpx;
    color: $text-secondary;
    font-family: monospace;
  }

  &.pay-amount {
    font-size: 32rpx;
    font-weight: 700;
    color: $primary;
  }
}

.unit {
  font-size: 22rpx;
  color: $text-tertiary;
  margin-left: 4rpx;
}

.order-footer {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 24rpx;
  background: $bg-input;
  font-size: 24rpx;
  color: $text-tertiary;
}

.footer-icon {
  font-size: 24rpx;
}

// ==================== 空状态 ====================
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
  background: $bg-card;
  border-radius: 24rpx;
  box-shadow: $shadow-md;
}

.empty-icon-wrapper {
  width: 120rpx;
  height: 120rpx;
  background: rgba($amber, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}

.empty-icon {
  font-size: 56rpx;
  color: $amber;
}

.empty-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: $text-tertiary;
}

// ==================== 加载状态 ====================
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 32rpx 0;
  font-size: 26rpx;
  color: $text-tertiary;
}

.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 32rpx 0;
  font-size: 24rpx;
  color: $text-placeholder;
}

.no-more-line {
  width: 60rpx;
  height: 1rpx;
  background: $border-color;
}

// ==================== 底部安全区域 ====================
.safe-bottom {
  height: calc(env(safe-area-inset-bottom, 20px) + 120rpx);
}
</style>
