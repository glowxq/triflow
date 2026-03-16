<script lang="ts" setup>
import { storeToRefs } from 'pinia'
import { getPublicSwitchStatus } from '@/api/system'
import { useTokenStore, useUserStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'
import { useScreenshot } from '@/hooks/useScreenshot'

defineOptions({
  name: 'Home',
})

definePage({
  type: 'home',
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '首页',
    disableScroll: false,
  },
})

const statusBarHeight = ref(sysStatusBarHeight || 20)
const tokenStore = useTokenStore()
const userStore = useUserStore()
const { isLoggedIn } = storeToRefs(tokenStore)
const { displayName } = storeToRefs(userStore)
const signInEnabled = ref(false)

// ========== 截图监听相关 ==========
const { isListening, checkForNewScreenshot, startListening } = useScreenshot()
const showScreenshot = ref(false)
const screenshotPath = ref('')
const screenshotTime = ref('')

// 调试日志（常驻显示在页面上）
const debugLogs = ref<string[]>([])
function addLog(msg: string) {
  const time = new Date().toLocaleTimeString()
  debugLogs.value.unshift(`[${time}] ${msg}`)
  // 最多保留 20 条
  if (debugLogs.value.length > 20) {
    debugLogs.value.pop()
  }
}

function handleNavigate(url: string) {
  if (!isLoggedIn.value) {
    uni.showModal({
      title: '提示',
      content: '请先登录后继续',
      confirmText: '去登录',
      success: (res) => {
        if (res.confirm) {
          uni.navigateTo({ url: '/pages/login/index' })
        }
      },
    })
    return
  }
  uni.navigateTo({ url })
}

async function fetchSignInEnabled() {
  try {
    signInEnabled.value = await getPublicSwitchStatus('wallet.signin.enabled')
  }
  catch {
    signInEnabled.value = false
  }
}

// 监听截图事件（后台轮询检测到时触发）
function onScreenshotDetected(screenshot: any) {
  addLog(`事件触发! path=${screenshot?.path}`)
  updateScreenshotDisplay(screenshot)
}

// 更新截图显示
function updateScreenshotDisplay(screenshot: any) {
  if (screenshot && screenshot.path) {
    screenshotPath.value = `file://${screenshot.path}`
    screenshotTime.value = new Date(screenshot.timestamp * 1000).toLocaleString()
    showScreenshot.value = true
    addLog(`显示截图: ${screenshot.path}`)
  }
}

function closeScreenshot() {
  showScreenshot.value = false
}

function previewScreenshot() {
  if (screenshotPath.value) {
    uni.previewImage({
      urls: [screenshotPath.value],
      current: screenshotPath.value,
    })
  }
}

// 首页自己启动监听（确保生效）
async function initScreenshotListener() {
  addLog(`平台: ${uni.getSystemInfoSync().platform}`)
  addLog(`环境: ${typeof plus === 'undefined' ? '非App' : 'App(plus可用)'}`)

  if (typeof plus === 'undefined') {
    addLog('⚠ plus 对象不存在，非 App 环境')
    return
  }

  if (isListening.value) {
    addLog('✓ 监听已在运行中')
    return
  }

  addLog('正在启动监听...')
  try {
    const success = await startListening()
    addLog(success ? '✓ 监听启动成功' : '✗ 监听启动失败')
  }
  catch (e: any) {
    addLog(`✗ 监听异常: ${e?.message || e}`)
  }
}

// 监听来自 hook 的调试日志
function onScreenshotDebug(msg: string) {
  addLog(msg)
}

onMounted(() => {
  uni.$on('screenshot-detected', onScreenshotDetected)
  uni.$on('screenshot-debug', onScreenshotDebug)
  addLog('页面已挂载')

  // 在 App 环境中启动监听
  // plus 可能在 onMounted 时还未就绪，需要等待 plusready
  if (typeof plus !== 'undefined') {
    initScreenshotListener()
  }
  else if (typeof document !== 'undefined') {
    // H5/App 环境中 plus 可能延迟就绪
    document.addEventListener('plusready', () => {
      addLog('plusready 事件触发')
      initScreenshotListener()
    })
  }
})

onUnmounted(() => {
  uni.$off('screenshot-detected', onScreenshotDetected)
  uni.$off('screenshot-debug', onScreenshotDebug)
})

onShow(() => {
  fetchSignInEnabled()
  addLog('onShow 触发')

  // 切回前台时检查新截图
  if (isListening.value) {
    const screenshot = checkForNewScreenshot()
    if (screenshot) {
      addLog(`onShow 检测到新截图: ${screenshot.path}`)
      updateScreenshotDisplay(screenshot)
    }
    else {
      addLog('onShow 无新截图')
    }
  }
})
</script>

<template>
  <view class="home-page">
    <view class="hero" :style="{ paddingTop: `${statusBarHeight + 24}px` }">
      <view class="hero-title">
        {{ isLoggedIn ? `你好，${displayName}` : '欢迎来到 Triflow' }}
      </view>
      <view class="hero-subtitle">
        积分与余额管理更轻松，充值、签到、明细一站完成
      </view>
      <view class="hero-actions">
        <view class="hero-card" @click="handleNavigate('/pages/wallet/recharge/index')">
          <view class="hero-icon i-carbon-wallet" />
          <view class="hero-label">
            充值中心
          </view>
        </view>
        <view class="hero-card" @click="handleNavigate('/pages/wallet/index')">
          <view class="hero-icon i-carbon-receipt" />
          <view class="hero-label">
            账户明细
          </view>
        </view>
        <view v-if="signInEnabled" class="hero-card" @click="handleNavigate('/pages/me/me')">
          <view class="hero-icon i-carbon-calendar" />
          <view class="hero-label">
            每日签到
          </view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">
        常用功能
      </view>
      <view class="feature-grid">
        <view class="feature-item" @click="handleNavigate('/pages/profile/index')">
          <view class="feature-icon i-carbon-user" />
          <view class="feature-title">
            个人资料
          </view>
          <view class="feature-desc">
            完善资料提升体验
          </view>
        </view>
        <view class="feature-item" @click="handleNavigate('/pages/wallet/index')">
          <view class="feature-icon i-carbon-view" />
          <view class="feature-title">
            流水查询
          </view>
          <view class="feature-desc">
            查看积分/余额变化
          </view>
        </view>
        <view class="feature-item" @click="handleNavigate('/pages/help/index')">
          <view class="feature-icon i-carbon-help" />
          <view class="feature-title">
            帮助中心
          </view>
          <view class="feature-desc">
            常见问题与指引
          </view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">
        专属提醒
      </view>
      <view class="notice-card">
        <view class="notice-title">
          积分余额动态提醒
        </view>
        <view class="notice-desc">
          开启通知后可第一时间掌握账户变化与活动信息
        </view>
        <wd-button type="primary" plain block @click="handleNavigate('/pages/notification/index')">
          前往开启
        </wd-button>
      </view>
    </view>

    <!-- ========== 截图预览（常驻） ========== -->
    <view class="screenshot-section">
      <view class="screenshot-section-header">
        <text class="screenshot-section-title">🖼 最新截图</text>
        <text class="screenshot-section-info">{{ screenshotPath ? screenshotTime : '暂无截图' }}</text>
      </view>
      <view v-if="screenshotPath" class="screenshot-preview" @click="previewScreenshot">
        <image class="screenshot-preview-img" :src="screenshotPath" mode="aspectFit" />
      </view>
      <view v-else class="screenshot-empty">
        <text class="screenshot-empty-text">截图后这里会显示最新截图</text>
        <text class="screenshot-empty-hint">在后台截图或在 App 内截图均可</text>
      </view>
      <view v-if="screenshotPath" class="screenshot-path-box">
        <text class="screenshot-path-text">{{ screenshotPath }}</text>
      </view>
    </view>

    <!-- ========== 调试面板 ========== -->
    <view class="debug-panel">
      <view class="debug-header">
        <text class="debug-title">📡 截图监听调试</text>
        <text class="debug-status" :class="isListening ? 'status-on' : 'status-off'">
          {{ isListening ? '● 监听中' : '○ 未监听' }}
        </text>
      </view>
      <scroll-view scroll-y class="debug-logs">
        <view v-for="(log, i) in debugLogs" :key="i" class="debug-log-item">
          <text>{{ log }}</text>
        </view>
        <view v-if="debugLogs.length === 0" class="debug-log-item">
          <text style="color: #666;">等待日志...</text>
        </view>
      </scroll-view>
    </view>

    <view class="safe-bottom" />
  </view>
</template>

<style lang="scss" scoped>
$primary: #0ea5e9;
$primary-light: #38bdf8;
$bg-page: #f8fafc;
$text-primary: #0f172a;
$text-secondary: #64748b;
$text-tertiary: #94a3b8;

.home-page {
  min-height: 100vh;
  background: $bg-page;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.hero {
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: #ffffff;
  padding: 0 24rpx 32rpx;
  border-radius: 0 0 40rpx 40rpx;
}

.hero-title {
  font-size: 40rpx;
  font-weight: 700;
  margin-bottom: 12rpx;
}

.hero-subtitle {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.5;
}

.hero-actions {
  margin-top: 24rpx;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
}

.hero-card {
  background: rgba(255, 255, 255, 0.55);
  border-radius: 20rpx;
  padding: 20rpx 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.hero-icon {
  font-size: 40rpx;
}

.hero-label {
  font-size: 24rpx;
  font-weight: 600;
}

.section {
  padding: 24rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 16rpx;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
}

.feature-item {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 18rpx 12rpx;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  box-shadow: 0 8rpx 20rpx rgba(15, 23, 42, 0.08);
}

.feature-icon {
  font-size: 32rpx;
  color: $primary;
}

.feature-title {
  font-size: 24rpx;
  font-weight: 600;
  color: $text-primary;
}

.feature-desc {
  font-size: 22rpx;
  color: $text-tertiary;
}

.notice-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.notice-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.notice-desc {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.5;
}

.safe-bottom {
  // 为自定义 tabbar 预留空间：tabbar高度50px + 安全区域 + 额外间距
  height: calc(env(safe-area-inset-bottom, 20px) + 50px + 20rpx);
}

// 截图展示区
.screenshot-section {
  margin: 24rpx;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, 0.06);
}

.screenshot-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.screenshot-section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.screenshot-section-info {
  font-size: 22rpx;
  color: $text-tertiary;
}

.screenshot-preview {
  width: 100%;
  background: #f1f5f9;
  border-radius: 16rpx;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.screenshot-preview-img {
  width: 100%;
  max-height: 500rpx;
  border-radius: 16rpx;
}

.screenshot-empty {
  width: 100%;
  padding: 48rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  background: #f1f5f9;
  border-radius: 16rpx;
}

.screenshot-empty-text {
  font-size: 26rpx;
  color: $text-secondary;
}

.screenshot-empty-hint {
  font-size: 22rpx;
  color: $text-tertiary;
}

.screenshot-path-box {
  margin-top: 12rpx;
  padding: 12rpx 16rpx;
  background: #f1f5f9;
  border-radius: 10rpx;
  overflow: hidden;
}

.screenshot-path-text {
  font-size: 20rpx;
  color: $text-tertiary;
  font-family: monospace;
  word-break: break-all;
}

// 调试面板
.debug-panel {
  margin: 24rpx;
  background: #1a1a2e;
  border-radius: 16rpx;
  padding: 20rpx;
  border: 2rpx solid #333;
}

.debug-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.debug-title {
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 600;
}

.debug-status {
  font-size: 24rpx;
  font-weight: 600;
}

.status-on {
  color: #4ade80;
}

.status-off {
  color: #f87171;
}

.debug-logs {
  max-height: 300rpx;
  background: #0d0d1a;
  border-radius: 12rpx;
  padding: 12rpx;
}

.debug-log-item {
  padding: 4rpx 0;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.06);

  text {
    color: #a5f3fc;
    font-size: 22rpx;
    font-family: monospace;
    word-break: break-all;
  }
}
</style>
