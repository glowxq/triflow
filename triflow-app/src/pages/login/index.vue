/**
 * 登录页面
 * @description 支持账号密码、短信验证码、微信一键登录三种方式
 * @design 流动科技感 + 简洁优雅
 */

<script lang="ts" setup>
import type { CaptchaVO } from '@/api/types/login'
import { getCaptcha, getCaptchaStatus, sendSmsCode } from '@/api/login'
import { getPublicSwitchStatus } from '@/api/system'
import { useTokenStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

defineOptions({
  name: 'Login',
})

definePage({
  style: {
    navigationBarTitleText: '登录',
    navigationStyle: 'custom',
  },
})

// ==================== 状态定义 ====================

const tokenStore = useTokenStore()

/** 状态栏高度 */
const statusBarHeight = ref(sysStatusBarHeight || 20)

/** 当前登录方式：password | sms */
const loginType = ref<'password' | 'sms'>('password')

/** 登录中状态 */
const loading = ref(false)

/** 验证码相关 */
const captchaEnabled = ref(false)
const captchaInfo = ref<CaptchaVO | null>(null)
const captchaLoading = ref(false)
/** 登录方式开关 */
const phoneLoginEnabled = ref(true)
const socialLoginEnabled = ref(true)

/** 短信倒计时 */
const smsCountdown = ref(0)
let smsTimer: ReturnType<typeof setInterval> | null = null

/** 是否显示密码 */
const showPassword = ref(false)

/** 页面加载动画 */
const pageLoaded = ref(false)

// ==================== 表单数据 ====================

/** 密码登录表单 */
const passwordForm = reactive({
  username: '',
  password: '',
  captchaCode: '',
})

/** 短信登录表单 */
const smsForm = reactive({
  phone: '',
  smsCode: '',
})

// ==================== 计算属性 ====================

/** 密码表单是否可提交 */
const canSubmitPassword = computed(() => {
  const hasBasicInfo = passwordForm.username.trim() && passwordForm.password.trim()
  if (captchaEnabled.value) {
    return hasBasicInfo && passwordForm.captchaCode.trim()
  }
  return hasBasicInfo
})

/** 短信表单是否可提交 */
const canSubmitSms = computed(() => {
  return phoneLoginEnabled.value
    && smsForm.phone.trim().length === 11
    && smsForm.smsCode.trim().length >= 4
})

/** 是否可发送短信验证码 */
const canSendSms = computed(() => {
  return phoneLoginEnabled.value
    && smsForm.phone.trim().length === 11
    && smsCountdown.value === 0
})

// ==================== 方法 ====================

/**
 * 初始化验证码状态
 */
async function initCaptchaStatus() {
  try {
    const res = await getCaptchaStatus()
    captchaEnabled.value = res.captchaEnabled
    if (res.captchaEnabled) {
      await refreshCaptcha()
    }
  }
  catch (error) {
    console.warn('获取验证码状态失败:', error)
  }
}

/**
 * 初始化登录方式开关
 */
async function initLoginSwitches() {
  try {
    const [phoneEnabled, socialEnabled] = await Promise.all([
      getPublicSwitchStatus('user.login.phone.enabled'),
      getPublicSwitchStatus('user.login.social.enabled'),
    ])
    phoneLoginEnabled.value = phoneEnabled
    socialLoginEnabled.value = socialEnabled
    if (!phoneLoginEnabled.value && loginType.value === 'sms') {
      loginType.value = 'password'
    }
  }
  catch (error) {
    console.warn('获取登录开关失败:', error)
  }
}

/**
 * 刷新图片验证码
 */
async function refreshCaptcha() {
  if (!captchaEnabled.value)
    return

  captchaLoading.value = true
  try {
    captchaInfo.value = await getCaptcha()
    passwordForm.captchaCode = ''
  }
  catch (error) {
    console.error('获取验证码失败:', error)
    uni.showToast({ title: '获取验证码失败', icon: 'none' })
  }
  finally {
    captchaLoading.value = false
  }
}

/**
 * 发送短信验证码
 */
async function handleSendSmsCode() {
  if (!canSendSms.value)
    return

  try {
    await sendSmsCode(smsForm.phone, 'login')
    uni.showToast({ title: '验证码已发送', icon: 'success' })

    // 开始倒计时
    smsCountdown.value = 60
    smsTimer = setInterval(() => {
      smsCountdown.value--
      if (smsCountdown.value <= 0 && smsTimer) {
        clearInterval(smsTimer)
        smsTimer = null
      }
    }, 1000)
  }
  catch (error: any) {
    console.error('发送验证码失败:', error)
    uni.showToast({ title: error.message || '发送失败', icon: 'none' })
  }
}

/**
 * 密码登录
 */
async function handlePasswordLogin() {
  if (!canSubmitPassword.value || loading.value)
    return

  loading.value = true
  try {
    await tokenStore.loginByPassword(
      passwordForm.username,
      passwordForm.password,
      captchaInfo.value?.captchaKey,
      passwordForm.captchaCode || undefined,
    )
    await handleLoginSuccess()
  }
  catch (error: any) {
    console.error('密码登录失败:', error)
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
    // 登录失败刷新验证码
    await refreshCaptcha()
  }
  finally {
    loading.value = false
  }
}

/**
 * 短信登录
 */
async function handleSmsLogin() {
  if (!canSubmitSms.value || loading.value)
    return

  loading.value = true
  try {
    await tokenStore.loginBySms(smsForm.phone, smsForm.smsCode)
    await handleLoginSuccess()
  }
  catch (error: any) {
    console.error('短信登录失败:', error)
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

/**
 * 微信一键登录
 */
async function handleWechatLogin() {
  if (loading.value || !socialLoginEnabled.value)
    return

  loading.value = true
  try {
    await tokenStore.loginByWechat()
    await handleLoginSuccess()
  }
  catch (error: any) {
    console.error('微信登录失败:', error)
  }
  finally {
    loading.value = false
  }
}

/**
 * 登录成功处理
 */
async function handleLoginSuccess() {
  if (smsTimer) {
    clearInterval(smsTimer)
    smsTimer = null
  }
  const redirected = await tokenStore.postLoginRedirect()
  if (!redirected) {
    uni.switchTab({ url: '/pages/index/index' })
  }
}

/**
 * 跳转注册页
 */
function goToRegister() {
  uni.navigateTo({ url: '/pages/register/index' })
}

/**
 * 跳转用户协议
 */
function goToUserAgreement() {
  uni.navigateTo({ url: '/pages/agreement/user' })
}

/**
 * 跳转隐私政策
 */
function goToPrivacyPolicy() {
  uni.navigateTo({ url: '/pages/agreement/privacy' })
}

// ==================== 生命周期 ====================

onLoad(() => {
  initCaptchaStatus()
  initLoginSwitches()
  // 延迟启动入场动画
  setTimeout(() => {
    pageLoaded.value = true
  }, 100)
})

onUnload(() => {
  if (smsTimer) {
    clearInterval(smsTimer)
    smsTimer = null
  }
})
</script>

<template>
  <view class="login-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle--1" />
      <view class="bg-circle bg-circle--2" />
      <view class="bg-circle bg-circle--3" />
    </view>

    <!-- 顶部安全区域 -->
    <view :style="{ height: `${statusBarHeight + 24}px` }" />

    <!-- 主内容区域 -->
    <view class="main-content" :class="{ 'is-loaded': pageLoaded }">
      <!-- Logo 和标题 -->
      <view class="header">
        <view class="logo-wrapper">
          <image src="/static/logo.svg" class="logo" mode="aspectFit" />
          <view class="logo-glow" />
        </view>
        <view class="brand">
          <text class="brand-name">Triflow</text>
          <view class="brand-line" />
        </view>
        <view class="title">
          欢迎回来
        </view>
        <view class="subtitle">
          登录以继续您的旅程
        </view>
      </view>

      <!-- 登录卡片 -->
      <view class="login-card">
        <!-- 登录方式切换 -->
        <view class="tabs" :class="{ 'is-single': !phoneLoginEnabled }">
          <view
            class="tab-item"
            :class="{ active: loginType === 'password' }"
            @click="loginType = 'password'"
          >
            <view class="tab-icon i-carbon-user-avatar" />
            <text class="tab-text">账号密码</text>
          </view>
          <view
            v-if="phoneLoginEnabled"
            class="tab-item"
            :class="{ active: loginType === 'sms' }"
            @click="loginType = 'sms'"
          >
            <view class="tab-icon i-carbon-mobile" />
            <text class="tab-text">短信验证码</text>
          </view>
          <view class="tab-indicator" :class="{ 'is-sms': loginType === 'sms' }" />
        </view>

        <!-- 密码登录表单 -->
        <view v-if="loginType === 'password'" class="form">
          <!-- 用户名 -->
          <view class="input-wrapper">
            <view class="input-group">
              <view class="input-icon i-carbon-user" />
              <input
                v-model="passwordForm.username"
                class="input"
                type="text"
                placeholder="请输入用户名/手机号"
                :maxlength="50"
              >
            </view>
          </view>

          <!-- 密码 -->
          <view class="input-wrapper">
            <view class="input-group">
              <view class="input-icon i-carbon-password" />
              <input
                v-model="passwordForm.password"
                class="input"
                type="text"
                :password="!showPassword"
                placeholder="请输入密码"
                :maxlength="50"
              >
              <view
                class="input-suffix"
                :class="showPassword ? 'i-carbon-view' : 'i-carbon-view-off'"
                @click="showPassword = !showPassword"
              />
            </view>
          </view>

          <!-- 图片验证码 -->
          <view v-if="captchaEnabled" class="input-wrapper">
            <view class="input-group">
              <view class="input-icon i-carbon-security" />
              <input
                v-model="passwordForm.captchaCode"
                class="input captcha-input"
                type="text"
                placeholder="请输入验证码"
                :maxlength="6"
              >
              <view class="captcha-box" @click="refreshCaptcha">
                <image
                  v-if="captchaInfo?.captchaImage"
                  :src="captchaInfo.captchaImage"
                  class="captcha-img"
                  mode="aspectFill"
                />
                <view v-else class="captcha-placeholder">
                  <view v-if="captchaLoading" class="i-carbon-circle-dash captcha-loading" />
                  <text v-else>点击获取</text>
                </view>
              </view>
            </view>
          </view>

          <!-- 登录按钮 -->
          <button
            class="btn-primary"
            :class="{ disabled: !canSubmitPassword, loading }"
            :disabled="!canSubmitPassword || loading"
            @click="handlePasswordLogin"
          >
            <view v-if="loading" class="btn-loading">
              <view class="i-carbon-circle-dash loading-icon" />
            </view>
            <text class="btn-text">登 录</text>
            <view class="btn-shine" />
          </button>
        </view>

        <!-- 短信登录表单 -->
        <view v-if="loginType === 'sms' && phoneLoginEnabled" class="form">
          <!-- 手机号 -->
          <view class="input-wrapper">
            <view class="input-group">
              <view class="input-icon i-carbon-phone" />
              <input
                v-model="smsForm.phone"
                class="input"
                type="number"
                placeholder="请输入手机号"
                :maxlength="11"
              >
            </view>
          </view>

          <!-- 短信验证码 -->
          <view class="input-wrapper">
            <view class="input-group">
              <view class="input-icon i-carbon-chat" />
              <input
                v-model="smsForm.smsCode"
                class="input sms-input"
                type="number"
                placeholder="请输入验证码"
                :maxlength="6"
              >
              <view
                class="sms-btn"
                :class="{ disabled: !canSendSms }"
                @click="handleSendSmsCode"
              >
                <text>{{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}</text>
              </view>
            </view>
          </view>

          <!-- 登录按钮 -->
          <button
            class="btn-primary"
            :class="{ disabled: !canSubmitSms, loading }"
            :disabled="!canSubmitSms || loading"
            @click="handleSmsLogin"
          >
            <view v-if="loading" class="btn-loading">
              <view class="i-carbon-circle-dash loading-icon" />
            </view>
            <text class="btn-text">登 录</text>
            <view class="btn-shine" />
          </button>
        </view>
      </view>

      <!-- 分隔线 -->
      <view v-if="socialLoginEnabled" class="divider">
        <view class="divider-line" />
        <view class="divider-text">
          其他登录方式
        </view>
        <view class="divider-line" />
      </view>

      <!-- 第三方登录 -->
      <view v-if="socialLoginEnabled" class="social">
        <!-- #ifdef MP-WEIXIN -->
        <view class="social-btn wechat" @click="handleWechatLogin">
          <view class="social-btn-inner">
            <view class="i-carbon-logo-wechat social-icon" />
          </view>
          <text class="social-label">微信登录</text>
        </view>
        <!-- #endif -->

        <!-- #ifdef H5 -->
        <view class="h5-hint">
          <view class="i-carbon-information h5-hint-icon" />
          <text>微信登录仅在小程序中可用</text>
        </view>
        <!-- #endif -->
      </view>

      <!-- 注册入口 -->
      <view class="register-entry">
        <text class="hint">还没有账号？</text>
        <text class="link" @click="goToRegister">立即注册</text>
      </view>
    </view>

    <!-- 底部协议 -->
    <view class="footer">
      <text>登录即表示同意</text>
      <text class="link" @click="goToUserAgreement">《用户协议》</text>
      <text>和</text>
      <text class="link" @click="goToPrivacyPolicy">《隐私政策》</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
// ==================== 设计变量 ====================
// 主题色 - 天蓝色系
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

// 边框
$border-color: #e2e8f0;
$border-light: #f1f5f9;

// 阴影
$shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
$shadow-md:
  0 4px 6px -1px rgba(0, 0, 0, 0.1),
  0 2px 4px -2px rgba(0, 0, 0, 0.1);
$shadow-lg:
  0 10px 15px -3px rgba(0, 0, 0, 0.1),
  0 4px 6px -4px rgba(0, 0, 0, 0.1);
$shadow-primary: 0 4px 14px rgba($primary, 0.35);

// ==================== 页面布局 ====================
.login-page {
  min-height: 100vh;
  background: $bg-page;
  position: relative;
  overflow: hidden;
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
    width: 400rpx;
    height: 400rpx;
    background: linear-gradient(135deg, rgba($primary-light, 0.3) 0%, rgba($primary, 0.1) 100%);
    top: -100rpx;
    right: -100rpx;
    animation: float1 8s ease-in-out infinite;
  }

  &--2 {
    width: 300rpx;
    height: 300rpx;
    background: linear-gradient(135deg, rgba($primary, 0.2) 0%, rgba($primary-light, 0.05) 100%);
    top: 200rpx;
    left: -100rpx;
    animation: float2 10s ease-in-out infinite;
  }

  &--3 {
    width: 200rpx;
    height: 200rpx;
    background: linear-gradient(135deg, rgba($primary-lighter, 0.3) 0%, rgba($primary, 0.1) 100%);
    bottom: 300rpx;
    right: -50rpx;
    animation: float3 12s ease-in-out infinite;
  }
}

@keyframes float1 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    // 使用 px 替代 rpx，小程序动画兼容性更好
    transform: translate(-10px, 15px) scale(1.05);
  }
}

@keyframes float2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(15px, -10px) scale(1.1);
  }
}

@keyframes float3 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-8px, -12px) scale(1.08);
  }
}

// ==================== 主内容 ====================
.main-content {
  position: relative;
  z-index: 1;
  padding: 0 40rpx;
  opacity: 0;
  transform: translateY(40rpx);
  transition:
    opacity 0.6s ease,
    transform 0.6s ease;

  &.is-loaded {
    opacity: 1;
    transform: translateY(0);
  }
}

// ==================== 头部 ====================
.header {
  text-align: center;
  padding: 40rpx 0 48rpx;
}

.logo-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 24rpx;
}

.logo {
  width: 120rpx;
  height: 120rpx;
  position: relative;
  z-index: 1;
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 160rpx;
  height: 160rpx;
  background: radial-gradient(circle, rgba($primary, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.3;
  }
}

.brand {
  margin-bottom: 20rpx;
}

.brand-name {
  font-size: 52rpx;
  font-weight: 800;
  color: $text-primary;
  letter-spacing: 4rpx;
}

.brand-line {
  width: 80rpx;
  height: 6rpx;
  background: $primary-gradient;
  border-radius: 3rpx;
  margin: 16rpx auto 0;
}

.title {
  font-size: 40rpx;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 8rpx;
}

.subtitle {
  font-size: 28rpx;
  color: $text-tertiary;
}

// ==================== 登录卡片 ====================
.login-card {
  background: $bg-card;
  border-radius: 32rpx;
  padding: 40rpx 32rpx;
  box-shadow: $shadow-lg;
  margin-bottom: 40rpx;
}

// ==================== 标签切换 ====================
.tabs {
  display: flex;
  position: relative;
  background: $bg-input;
  border-radius: 20rpx;
  padding: 8rpx;
  margin-bottom: 40rpx;

  &.is-single {
    .tab-indicator {
      width: calc(100% - 16rpx);
      transform: translateX(0);
    }
  }
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
  position: relative;
  z-index: 1;
  transition: color 0.3s ease;

  &.active {
    .tab-icon,
    .tab-text {
      color: $bg-card;
    }
  }
}

.tab-icon {
  font-size: 32rpx;
  color: $text-tertiary;
  margin-right: 8rpx;
  transition: color 0.3s ease;
}

.tab-text {
  font-size: 28rpx;
  color: $text-secondary;
  font-weight: 500;
  transition: color 0.3s ease;
}

.tab-indicator {
  position: absolute;
  top: 8rpx;
  left: 8rpx;
  width: calc(50% - 8rpx);
  height: calc(100% - 16rpx);
  background: $primary-gradient;
  border-radius: 16rpx;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: $shadow-primary;

  &.is-sms {
    transform: translateX(100%);
  }
}

// ==================== 表单 ====================
.form {
  margin-bottom: 8rpx;
}

.input-wrapper {
  margin-bottom: 24rpx;

  &:last-of-type {
    margin-bottom: 32rpx;
  }
}

.input-group {
  display: flex;
  align-items: center;
  background: $bg-input;
  border: 2rpx solid transparent;
  border-radius: 20rpx;
  padding: 0 28rpx;
  height: 104rpx;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;

  // 小程序不支持 :focus-within，使用 :focus 替代
  /* #ifdef H5 */
  &:focus-within {
    border-color: $primary;
    box-shadow: 0 0 0 4rpx rgba($primary, 0.1);
  }
  /* #endif */
}

.input-icon {
  font-size: 40rpx;
  color: $text-tertiary;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.input {
  flex: 1;
  height: 100%;
  font-size: 30rpx;
  color: $text-primary;
  background: transparent;
}

.input-suffix {
  font-size: 40rpx;
  color: $text-tertiary;
  margin-left: 16rpx;
  flex-shrink: 0;
  transition: color 0.3s ease;

  &:active {
    color: $primary;
  }
}

.captcha-input {
  flex: 1;
}

.captcha-box {
  width: 180rpx;
  height: 68rpx;
  margin-left: 16rpx;
  border-radius: 12rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.captcha-img {
  width: 100%;
  height: 100%;
}

.captcha-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $border-color;
  font-size: 24rpx;
  color: $text-tertiary;
}

.captcha-loading {
  font-size: 32rpx;
  animation: spin 1s linear infinite;
}

.sms-input {
  flex: 1;
}

.sms-btn {
  min-width: 180rpx;
  height: 68rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $primary-gradient;
  color: $bg-card;
  font-size: 26rpx;
  font-weight: 500;
  border-radius: 34rpx;
  margin-left: 16rpx;
  flex-shrink: 0;
  padding: 0 28rpx;
  box-shadow: $shadow-primary;
  transition:
    opacity 0.3s ease,
    transform 0.2s ease;

  &:active {
    transform: scale(0.96);
  }

  &.disabled {
    background: $border-color;
    color: $text-tertiary;
    box-shadow: none;
  }
}

// ==================== 主按钮 ====================
.btn-primary {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 104rpx;
  background: $primary-gradient;
  color: $bg-card;
  font-size: 34rpx;
  font-weight: 600;
  border-radius: 52rpx;
  border: none;
  box-shadow: $shadow-primary;
  overflow: hidden;
  transition:
    opacity 0.3s ease,
    transform 0.2s ease;

  &:active:not(.disabled) {
    transform: scale(0.98);
  }

  &.disabled {
    background: $border-color;
    color: $text-tertiary;
    box-shadow: none;
  }

  &.loading {
    .btn-text {
      opacity: 0;
    }
  }
}

.btn-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.btn-text {
  position: relative;
  z-index: 1;
  transition: opacity 0.3s ease;
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.2) 50%, transparent 100%);
  animation: shine 3s ease-in-out infinite;
}

@keyframes shine {
  0% {
    left: -100%;
  }
  20%,
  100% {
    left: 100%;
  }
}

.loading-icon {
  font-size: 40rpx;
  color: $bg-card;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// ==================== 分隔线 ====================
.divider {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.divider-line {
  flex: 1;
  height: 2rpx;
  background: linear-gradient(90deg, transparent 0%, $border-color 50%, transparent 100%);
}

.divider-text {
  padding: 0 24rpx;
  font-size: 26rpx;
  color: $text-tertiary;
}

// ==================== 第三方登录 ====================
.social {
  display: flex;
  justify-content: center;
  margin-bottom: 40rpx;
}

.social-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.social-btn-inner {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
  transition: transform 0.3s ease;

  &:active {
    transform: scale(0.92);
  }
}

.wechat .social-btn-inner {
  background: linear-gradient(135deg, #07c160 0%, #2aae67 100%);
  box-shadow: 0 4px 14px rgba(7, 193, 96, 0.35);
}

.social-icon {
  font-size: 48rpx;
  color: $bg-card;
}

.social-label {
  font-size: 24rpx;
  color: $text-tertiary;
}

.h5-hint {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  color: $text-tertiary;
  padding: 16rpx 24rpx;
  background: $bg-input;
  border-radius: 12rpx;
}

.h5-hint-icon {
  font-size: 32rpx;
  margin-right: 8rpx;
  color: $primary;
}

// ==================== 注册入口 ====================
.register-entry {
  text-align: center;
  margin-bottom: 32rpx;
}

.register-entry .hint {
  font-size: 28rpx;
  color: $text-secondary;
}

.register-entry .link {
  font-size: 28rpx;
  color: $primary;
  font-weight: 500;
  margin-left: 8rpx;
}

// ==================== 底部协议 ====================
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx 48rpx;
  // 小程序兼容：使用 constant() 和 env() 双重适配
  padding-bottom: calc(24rpx + constant(safe-area-inset-bottom));
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  text-align: center;
  font-size: 24rpx;
  color: $text-tertiary;
  background: linear-gradient(to top, $bg-page 0%, transparent 100%);
}

.footer .link {
  color: $primary;
}
</style>
