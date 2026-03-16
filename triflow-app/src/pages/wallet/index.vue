/**
 * 钱包变动记录页面
 * @description 显示用户的积分余额变动历史
 * @design 流动科技感 + 列表优雅呈现
 */

<script lang="ts" setup>
import type { WalletTransactionQuery, WalletTransactionVO } from '@/api/types/wallet'
import { getMyTransactions } from '@/api/wallet'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

definePage({
  style: {
    navigationBarTitleText: '账户明细',
    navigationStyle: 'custom',
  },
  needLogin: true,
})

// ==================== 状态 ====================

const statusBarHeight = ref(sysStatusBarHeight || 20)
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const pageLoaded = ref(false)
const list = ref<WalletTransactionVO[]>([])

const query = reactive<WalletTransactionQuery>({
  pageNum: 1,
  pageSize: 20,
  type: undefined,
})

const tabs = [
  { label: '全部', value: undefined, icon: 'i-carbon-list' },
  { label: '积分', value: 'points' as const, icon: 'i-carbon-star' },
]

const activeTab = ref(0)

// ==================== 方法 ====================

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
    const result = await getMyTransactions(query)
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
    console.error('加载钱包记录失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
    refreshing.value = false
  }
}

function onTabChange(index: number) {
  activeTab.value = index
  query.type = tabs[index].value
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

function formatAmount(item: WalletTransactionVO): string {
  const prefix = item.action === 'income' || item.action === 'unfreeze' ? '+' : '-'
  if (item.type === 'points') {
    return `${prefix}${Math.abs(item.amount)}`
  }
  return `${prefix}${Math.abs(item.amount).toFixed(2)}`
}

function getAmountClass(item: WalletTransactionVO): string {
  return item.action === 'income' || item.action === 'unfreeze' ? 'amount-income' : 'amount-expense'
}

function formatTime(time: string): string {
  const date = new Date(time)
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

function getActionIcon(item: WalletTransactionVO): string {
  if (item.action === 'income' || item.action === 'unfreeze') {
    return 'i-carbon-arrow-down-left'
  }
  return 'i-carbon-arrow-up-right'
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
  <view class="wallet-page">
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
        <text class="nav-title">账户明细</text>
        <view class="nav-placeholder" />
      </view>

      <!-- 标签切换 -->
      <view class="tabs-wrapper">
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
      </view>
    </view>

    <!-- 列表区域 -->
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
        <view v-if="list.length" class="transaction-list">
          <view
            v-for="item in list"
            :key="item.id"
            class="transaction-item"
          >
            <view class="item-icon-wrapper" :class="getAmountClass(item)">
              <view :class="getActionIcon(item)" class="item-icon" />
            </view>
            <view class="item-content">
              <view class="item-title">
                {{ item.title }}
              </view>
              <view class="item-time">
                {{ formatTime(item.createTime) }}
              </view>
            </view>
            <view class="item-right">
              <view class="item-amount" :class="getAmountClass(item)">
                {{ formatAmount(item) }}
                <text class="amount-unit">{{ item.type === 'points' ? '积分' : '元' }}</text>
              </view>
              <view class="item-balance">
                余{{ item.type === 'points' ? item.afterAmount : item.afterAmount.toFixed(2) }}
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-else-if="!loading" class="empty-state">
          <view class="empty-icon-wrapper">
            <view class="i-carbon-document empty-icon" />
          </view>
          <text class="empty-title">暂无记录</text>
          <text class="empty-desc">您的账户变动记录将在这里显示</text>
        </view>

        <!-- 加载中 -->
        <view v-if="loading && !refreshing" class="loading-more">
          <wd-loading size="24px" />
          <text>加载中...</text>
        </view>

        <!-- 没有更多 -->
        <view v-if="finished && list.length" class="no-more">
          <view class="no-more-line" />
          <text>没有更多了</text>
          <view class="no-more-line" />
        </view>

        <!-- 底部安全区域 -->
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

$green: #10b981;
$green-light: #d1fae5;
$red: #ef4444;
$red-light: #fee2e2;

// ==================== 页面布局 ====================
.wallet-page {
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

.tabs-wrapper {
  padding: 16rpx 24rpx 24rpx;
}

.tabs {
  display: flex;
  justify-content: center;
  gap: 16rpx;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 14rpx 28rpx;
  font-size: 26rpx;
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

// ==================== 交易列表 ====================
.transaction-list {
  background: $bg-card;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: $shadow-md;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx;

  & + .transaction-item {
    border-top: 1rpx solid $border-color;
  }
}

.item-icon-wrapper {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.amount-income {
    background: $green-light;

    .item-icon {
      color: $green;
    }
  }

  &.amount-expense {
    background: $red-light;

    .item-icon {
      color: $red;
    }
  }
}

.item-icon {
  font-size: 32rpx;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 500;
  margin-bottom: 6rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-time {
  font-size: 24rpx;
  color: $text-tertiary;
}

.item-right {
  text-align: right;
  flex-shrink: 0;
}

.item-amount {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 4rpx;

  &.amount-income {
    color: $green;
  }

  &.amount-expense {
    color: $red;
  }
}

.amount-unit {
  font-size: 22rpx;
  font-weight: 500;
  margin-left: 4rpx;
}

.item-balance {
  font-size: 22rpx;
  color: $text-tertiary;
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
  background: rgba($primary, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}

.empty-icon {
  font-size: 56rpx;
  color: $primary;
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
  height: calc(env(safe-area-inset-bottom, 20px) + 32rpx);
}
</style>
