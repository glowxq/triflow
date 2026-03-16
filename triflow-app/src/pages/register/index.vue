/**
 * 注册页面
 * @description 手机号+短信验证码注册，流动科技感设计
 * @design 与登录页面保持一致的视觉风格
 */

<script lang="ts" setup>
import type { LoginVO, RegisterDTO } from '@/api/types/login'
import { register, sendSmsCode } from '@/api/login'
import { useTokenStore, useUserStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

defineOptions({
  name: 'Register',
})

definePage({
  style: {
    navigationBarTitleText: '注册',
    navigationStyle: 'custom',
  },
})

// ==================== 状态定义 ====================

const tokenStore = useTokenStore()
const userStore = useUserStore()

/** 状态栏高度 */
const statusBarHeight = ref(sysStatusBarHeight || 20)

/** 注册中状态 */
const loading = ref(false)

/** 短信倒计时 */
const smsCountdown = ref(0)
let smsTimer: ReturnType<typeof setInterval> | null = null

/** 页面加载动画 */
const pageLoaded = ref(false)

// ==================== 表单数据 ====================

const form = reactive({
  phone: '',
  smsCode: '',
  password: '',
  confirmPassword: '',
})

/** 是否显示密码 */
const showPassword = ref(false)
const showConfirmPassword = ref(false)

/** 是否同意协议 */
const agreeProtocol = ref(false)

// ==================== 计算属性 ====================

/** 表单是否可提交 */
const canSubmit = computed(() => {
  return form.phone.trim().length === 11
    && form.smsCode.trim().length >= 4
    && form.password.trim().length >= 6
    && form.confirmPassword.trim() === form.password.trim()
    && agreeProtocol.value
})

/** 是否可发送短信验证码 */
const canSendSms = computed(() => {
  return form.phone.trim().length === 11 && smsCountdown.value === 0
})

/** 密码强度 */
const passwordStrength = computed(() => {
  const pwd = form.password.trim()
  if (!pwd)
    return 0
  let strength = 0
  if (pwd.length >= 6)
    strength++
  if (pwd.length >= 8)
    strength++
  if (/[A-Z]/.test(pwd))
    strength++
  if (/\d/.test(pwd))
    strength++
  if (/[^A-Z0-9]/i.test(pwd))
    strength++
  return Math.min(strength, 4)
})

/** 密码强度文本 */
const passwordStrengthText = computed(() => {
  const texts = ['', '弱', '中', '强', '很强']
  return texts[passwordStrength.value]
})

/** 密码强度颜色 */
const passwordStrengthColor = computed(() => {
  const colors = ['', '#ef4444', '#f59e0b', '#22c55e', '#0ea5e9']
  return colors[passwordStrength.value]
})

// ==================== 方法 ====================

/**
 * 发送短信验证码
 */
async function handleSendSmsCode() {
  if (!canSendSms.value)
    return

  try {
    await sendSmsCode(form.phone, 'register')
    uni.showToast({ title: '验证码已发送', icon: 'success' })

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
 * 提交注册
 */
async function handleRegister() {
  if (!canSubmit.value || loading.value)
    return

  if (form.phone.trim().length !== 11) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  if (form.smsCode.trim().length < 4) {
    uni.showToast({ title: '请输入验证码', icon: 'none' })
    return
  }
  if (form.password.trim().length < 6) {
    uni.showToast({ title: '密码至少6个字符', icon: 'none' })
    return
  }
  if (form.password !== form.confirmPassword) {
    uni.showToast({ title: '两次密码输入不一致', icon: 'none' })
    return
  }
  if (!agreeProtocol.value) {
    uni.showToast({ title: '请同意用户协议和隐私政策', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const dto: RegisterDTO = {
      phone: form.phone.trim(),
      smsCode: form.smsCode.trim(),
      password: form.password,
      confirmPassword: form.confirmPassword,
    }

    const result = await register(dto) as LoginVO

    tokenStore.setTokenInfo(result)
    userStore.setUserInfo({
      userId: result.userId,
      username: result.username,
      realName: result.realName,
      avatar: result.avatar,
      roles: result.roles,
    })

    uni.showToast({ title: '注册成功', icon: 'success' })

    setTimeout(async () => {
      const redirected = await tokenStore.postLoginRedirect()
      if (!redirected) {
        uni.switchTab({ url: '/pages/index/index' })
      }
    }, 1500)
  }
  catch (error: any) {
    console.error('注册失败:', error)
    uni.showToast({ title: error.message || '注册失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

/**
 * 返回登录页
 */
function goBack() {
  uni.navigateBack()
}

/**
 * 跳转到用户协议
 */
function goToUserAgreement() {
  uni.navigateTo({ url: '/pages/agreement/user' })
}

/**
 * 跳转到隐私政策
 */
function goToPrivacyPolicy() {
  uni.navigateTo({ url: '/pages/agreement/privacy' })
}

// ==================== 生命周期 ====================

onLoad(() => {
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
  <view class="register-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle--1" />
      <view class="bg-circle bg-circle--2" />
      <view class="bg-circle bg-circle--3" />
    </view>

    <!-- 固定的返回按钮 -->
    <view class="nav-bar" :style="{ top: `${statusBarHeight}px` }">
      <view class="back-btn" @click="goBack">
        <view class="back-btn-inner">
          <view class="i-carbon-arrow-left" />
        </view>
        <text class="back-btn-text">返回</text>
      </view>
    </view>

    <!-- 主内容区域 -->
    <view
      class="main-content"
      :class="{ 'is-loaded': pageLoaded }"
      :style="{ paddingTop: `${statusBarHeight + 44}px` }"
    >
      <!-- 头部标题 -->
      <view class="header">
        <view class="header-icon">
          <view class="i-carbon-user-avatar-filled" />
        </view>
        <view class="title">
          创建账号
        </view>
        <view class="subtitle">
          加入 Triflow，开启全新体验
        </view>
      </view>

      <!-- 注册卡片 -->
      <view class="register-card">
        <!-- 步骤指示器 -->
        <view class="steps">
          <view class="step active" :class="{ done: form.phone.length === 11 }">
            <view class="step-dot">
              <view v-if="form.phone.length === 11" class="i-carbon-checkmark" />
              <text v-else>1</text>
            </view>
            <text class="step-label">手机验证</text>
          </view>
          <view class="step-line" :class="{ active: form.phone.length === 11 && form.smsCode.length >= 4 }" />
          <view class="step" :class="{ active: form.phone.length === 11, done: form.password.length >= 6 }">
            <view class="step-dot">
              <view v-if="form.password.length >= 6" class="i-carbon-checkmark" />
              <text v-else>2</text>
            </view>
            <text class="step-label">设置密码</text>
          </view>
          <view class="step-line" :class="{ active: form.password.length >= 6 && form.confirmPassword === form.password }" />
          <view class="step" :class="{ active: form.password.length >= 6, done: agreeProtocol }">
            <view class="step-dot">
              <view v-if="agreeProtocol" class="i-carbon-checkmark" />
              <text v-else>3</text>
            </view>
            <text class="step-label">确认协议</text>
          </view>
        </view>

        <!-- 表单区域 -->
        <view class="form">
          <!-- 手机号 -->
          <view class="input-wrapper">
            <view class="input-label">
              <view class="i-carbon-phone input-label-icon" />
              <text>手机号</text>
            </view>
            <view class="input-group">
              <text class="input-prefix">+86</text>
              <view class="input-divider" />
              <input
                v-model="form.phone"
                class="input"
                type="number"
                placeholder="请输入手机号"
                :maxlength="11"
              >
            </view>
          </view>

          <!-- 短信验证码 -->
          <view class="input-wrapper">
            <view class="input-label">
              <view class="i-carbon-chat input-label-icon" />
              <text>验证码</text>
            </view>
            <view class="input-group">
              <input
                v-model="form.smsCode"
                class="input"
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

          <!-- 密码 -->
          <view class="input-wrapper">
            <view class="input-label">
              <view class="i-carbon-password input-label-icon" />
              <text>设置密码</text>
            </view>
            <view class="input-group">
              <input
                v-model="form.password"
                class="input"
                type="text"
                :password="!showPassword"
                placeholder="请输入密码（至少6位）"
                :maxlength="32"
              >
              <view
                class="input-suffix"
                :class="showPassword ? 'i-carbon-view' : 'i-carbon-view-off'"
                @click="showPassword = !showPassword"
              />
            </view>
            <!-- 密码强度 -->
            <view v-if="form.password" class="password-strength">
              <view class="strength-bar">
                <view
                  v-for="i in 4"
                  :key="i"
                  class="strength-segment"
                  :class="{ active: i <= passwordStrength }"
                  :style="{ backgroundColor: i <= passwordStrength ? passwordStrengthColor : '' }"
                />
              </view>
              <text class="strength-text" :style="{ color: passwordStrengthColor }">
                {{ passwordStrengthText }}
              </text>
            </view>
          </view>

          <!-- 确认密码 -->
          <view class="input-wrapper">
            <view class="input-label">
              <view class="i-carbon-locked input-label-icon" />
              <text>确认密码</text>
            </view>
            <view class="input-group" :class="{ error: form.confirmPassword && form.confirmPassword !== form.password }">
              <input
                v-model="form.confirmPassword"
                class="input"
                type="text"
                :password="!showConfirmPassword"
                placeholder="请再次输入密码"
                :maxlength="32"
              >
              <view
                class="input-suffix"
                :class="showConfirmPassword ? 'i-carbon-view' : 'i-carbon-view-off'"
                @click="showConfirmPassword = !showConfirmPassword"
              />
            </view>
            <view v-if="form.confirmPassword && form.confirmPassword !== form.password" class="input-error">
              <view class="i-carbon-warning" />
              <text>两次密码输入不一致</text>
            </view>
            <view v-else-if="form.confirmPassword && form.confirmPassword === form.password" class="input-success">
              <view class="i-carbon-checkmark-filled" />
              <text>密码一致</text>
            </view>
          </view>

          <!-- 协议勾选 -->
          <view class="protocol">
            <view
              class="checkbox"
              :class="{ checked: agreeProtocol }"
              @click="agreeProtocol = !agreeProtocol"
            >
              <view v-if="agreeProtocol" class="i-carbon-checkmark checkbox-icon" />
            </view>
            <view class="protocol-text">
              <text>我已阅读并同意</text>
              <text class="link" @click.stop="goToUserAgreement">《用户协议》</text>
              <text>和</text>
              <text class="link" @click.stop="goToPrivacyPolicy">《隐私政策》</text>
            </view>
          </view>

          <!-- 注册按钮 -->
          <button
            class="btn-primary"
            :class="{ disabled: !canSubmit, loading }"
            :disabled="!canSubmit || loading"
            @click="handleRegister"
          >
            <view v-if="loading" class="btn-loading">
              <view class="i-carbon-circle-dash loading-icon" />
            </view>
            <text class="btn-text">立即注册</text>
            <view class="btn-shine" />
          </button>
        </view>
      </view>

      <!-- 底部登录入口 -->
      <view class="login-entry">
        <text class="hint">已有账号？</text>
        <text class="link" @click="goBack">立即登录</text>
      </view>
    </view>

    <!-- 底部安全区域 -->
    <view class="safe-bottom" />
  </view>
</template>

<style lang="scss" scoped>
// ==================== 设计变量 ====================
// 主题色 - 天蓝色系（与登录页保持一致）
$primary: #0ea5e9;
$primary-light: #38bdf8;
$primary-lighter: #7dd3fc;
$primary-dark: #0284c7;
$primary-gradient: linear-gradient(135deg, $primary 0%, $primary-light 100%);

// 语义色
$success: #22c55e;
$warning: #f59e0b;
$error: #ef4444;

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
.register-page {
  min-height: 100vh;
  background: $bg-page;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
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
    width: 350rpx;
    height: 350rpx;
    background: linear-gradient(135deg, rgba($primary-light, 0.3) 0%, rgba($primary, 0.1) 100%);
    top: -80rpx;
    left: -80rpx;
    animation: float1 8s ease-in-out infinite;
  }

  &--2 {
    width: 280rpx;
    height: 280rpx;
    background: linear-gradient(135deg, rgba($primary, 0.2) 0%, rgba($primary-light, 0.05) 100%);
    top: 400rpx;
    right: -100rpx;
    animation: float2 10s ease-in-out infinite;
  }

  &--3 {
    width: 180rpx;
    height: 180rpx;
    background: linear-gradient(135deg, rgba($primary-lighter, 0.3) 0%, rgba($primary, 0.1) 100%);
    bottom: 200rpx;
    left: -40rpx;
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
    transform: translate(10px, 15px) scale(1.05);
  }
}

@keyframes float2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-15px, 10px) scale(1.1);
  }
}

@keyframes float3 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(8px, -12px) scale(1.08);
  }
}

// ==================== 固定导航栏 ====================
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 36px;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  z-index: 100;
}

.back-btn {
  display: flex;
  align-items: center;
  padding: 4rpx;
}

.back-btn-inner {
  width: 52rpx;
  height: 52rpx;
  background: $bg-card;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $shadow-md;
  margin-right: 8rpx;

  view {
    font-size: 30rpx;
    color: $text-primary;
  }
}

.back-btn-text {
  font-size: 24rpx;
  color: $text-secondary;
}

// ==================== 主内容 ====================
.main-content {
  position: relative;
  z-index: 1;
  padding: 0 28rpx;
  flex: 1;
  display: flex;
  flex-direction: column;
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
  padding: 20rpx 0 28rpx;
}

.header-icon {
  width: 80rpx;
  height: 80rpx;
  background: $primary-gradient;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16rpx;
  box-shadow: $shadow-primary;

  view {
    font-size: 40rpx;
    color: $bg-card;
  }
}

.title {
  font-size: 40rpx;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 8rpx;
}

.subtitle {
  font-size: 24rpx;
  color: $text-tertiary;
}

// ==================== 注册卡片 ====================
.register-card {
  background: $bg-card;
  border-radius: 28rpx;
  padding: 32rpx 28rpx 36rpx;
  box-shadow: $shadow-lg;
  margin-bottom: 24rpx;
}

// ==================== 步骤指示器 ====================
.steps {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
  padding: 0 16rpx;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  opacity: 0.5;
  transition: opacity 0.3s ease;

  &.active {
    opacity: 1;
  }

  &.done .step-dot {
    background: $success;
    border-color: $success;

    view {
      font-size: 20rpx;
      color: $bg-card;
    }
  }
}

.step-dot {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid $border-color;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6rpx;
  font-size: 20rpx;
  font-weight: 600;
  color: $text-tertiary;
  background: $bg-card;
  transition: all 0.3s ease;

  .step.active & {
    border-color: $primary;
    color: $primary;
  }
}

.step-label {
  font-size: 20rpx;
  color: $text-tertiary;
  white-space: nowrap;

  .step.active & {
    color: $text-secondary;
  }
}

.step-line {
  width: 48rpx;
  height: 2rpx;
  background: $border-color;
  margin: 0 8rpx;
  margin-bottom: 20rpx;
  transition: background 0.3s ease;

  &.active {
    background: $primary;
  }
}

// ==================== 表单 ====================
.form {
  margin-top: 8rpx;
}

.input-wrapper {
  margin-bottom: 24rpx;
}

.input-label {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
  font-size: 26rpx;
  font-weight: 500;
  color: $text-primary;
}

.input-label-icon {
  font-size: 26rpx;
  color: $primary;
  margin-right: 8rpx;
}

.input-group {
  display: flex;
  align-items: center;
  background: $bg-input;
  border: 2rpx solid transparent;
  border-radius: 16rpx;
  padding: 0 20rpx;
  height: 88rpx;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;

  // 小程序不支持 :focus-within，使用条件编译
  /* #ifdef H5 */
  &:focus-within {
    border-color: $primary;
    box-shadow: 0 0 0 3rpx rgba($primary, 0.1);
  }
  /* #endif */

  &.error {
    border-color: $error;
    box-shadow: 0 0 0 3rpx rgba($error, 0.1);
  }
}

.input-prefix {
  font-size: 24rpx;
  font-weight: 500;
  color: $text-primary;
  padding-right: 10rpx;
}

.input-divider {
  width: 2rpx;
  height: 24rpx;
  background: $border-color;
  margin-right: 10rpx;
}

.input {
  flex: 1;
  height: 100%;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
}

.input-suffix {
  font-size: 28rpx;
  color: $text-tertiary;
  margin-left: 10rpx;
  flex-shrink: 0;
  transition: color 0.3s ease;

  &:active {
    color: $primary;
  }
}

.input-error {
  display: flex;
  align-items: center;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: $error;

  view {
    font-size: 26rpx;
    margin-right: 6rpx;
  }
}

.input-success {
  display: flex;
  align-items: center;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: $success;

  view {
    font-size: 26rpx;
    margin-right: 6rpx;
  }
}

// ==================== 密码强度 ====================
.password-strength {
  display: flex;
  align-items: center;
  margin-top: 12rpx;
}

.strength-bar {
  display: flex;
  flex: 1;
  margin-right: 8rpx;
}

.strength-segment {
  flex: 1;
  height: 6rpx;
  background: $border-color;
  border-radius: 3rpx;
  margin-right: 6rpx;
  transition: background 0.3s ease;

  &:last-child {
    margin-right: 0;
  }
}

.strength-text {
  font-size: 22rpx;
  font-weight: 500;
  min-width: 56rpx;
  text-align: right;
}

// ==================== 短信按钮 ====================
.sms-btn {
  min-width: 160rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $primary-gradient;
  color: $bg-card;
  font-size: 24rpx;
  font-weight: 500;
  border-radius: 30rpx;
  margin-left: 16rpx;
  flex-shrink: 0;
  padding: 0 20rpx;
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

// ==================== 协议 ====================
.protocol {
  display: flex;
  align-items: flex-start;
  padding: 20rpx 0 28rpx;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid $border-color;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-right: 12rpx;
  margin-top: 2rpx;
  transition: all 0.3s ease;

  &.checked {
    background: $primary-gradient;
    border-color: $primary;
  }
}

.checkbox-icon {
  font-size: 22rpx;
  color: $bg-card;
}

.protocol-text {
  flex: 1;
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.6;
}

.protocol-text .link,
.login-entry .link {
  color: $primary;
  font-weight: 500;
}

// ==================== 主按钮 ====================
.btn-primary {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 88rpx;
  background: $primary-gradient;
  color: $bg-card;
  font-size: 30rpx;
  font-weight: 600;
  border-radius: 44rpx;
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
  font-size: 32rpx;
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

// ==================== 登录入口 ====================
.login-entry {
  text-align: center;
  padding: 16rpx 0;
}

.login-entry .hint {
  font-size: 26rpx;
  color: $text-secondary;
}

// ==================== 安全区域 ====================
.safe-bottom {
  // 小程序兼容：使用 constant() 和 env() 双重适配
  height: calc(12rpx + constant(safe-area-inset-bottom));
  height: calc(12rpx + env(safe-area-inset-bottom));
  flex-shrink: 0;
}
</style>
