/**
 * 个人资料页面
 * @description 支持编辑头像、昵称等个人信息
 * @design 流动科技感 + 表单优雅交互
 */

<script lang="ts" setup>
import type { ProfileUpdateDTO, UpdatePasswordDTO } from '@/api/types/login'
import { storeToRefs } from 'pinia'
import { updatePassword, updateProfile } from '@/api/login'
import ProfileBasic from '@/components/profile-basic/profile-basic.vue'
import { useUserStore } from '@/store'
import { statusBarHeight as sysStatusBarHeight } from '@/utils/systemInfo'

defineOptions({
  name: 'Profile',
})

definePage({
  style: {
    navigationBarTitleText: '个人资料',
    navigationStyle: 'custom',
  },
  needLogin: true,
})

const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore)

const statusBarHeight = ref(sysStatusBarHeight || 20)
const loading = ref(false)
const passwordLoading = ref(false)
const pageLoaded = ref(false)

const form = reactive<ProfileUpdateDTO>({
  avatar: '',
  nickname: '',
  realName: '',
  phone: '',
  email: '',
  gender: 0,
})

const passwordForm = reactive({
  oldPassword: '',
  password: '',
  confirmPassword: '',
})

const showOldPassword = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const setPassword = ref(false)

const genderOptions = [
  { label: '未知', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
]

const genderLabel = computed(() => {
  return genderOptions.find(option => option.value === form.gender)?.label || '未设置'
})

const needsPassword = computed(() => userInfo.value.passwordSet === false)

const passwordEnabled = computed(() => needsPassword.value || setPassword.value)

function initForm() {
  form.avatar = userInfo.value.avatar || ''
  form.nickname = userInfo.value.nickname || ''
  form.realName = userInfo.value.realName || ''
  form.phone = userInfo.value.phone || ''
  form.email = userInfo.value.email || ''
  form.gender = userInfo.value.gender ?? 0
  passwordForm.oldPassword = ''
  passwordForm.password = ''
  passwordForm.confirmPassword = ''
  setPassword.value = false
}

function handleGenderChange(event: { detail: { value: number } }) {
  const index = Number(event.detail.value)
  form.gender = genderOptions[index]?.value ?? 0
}

async function handleSubmit() {
  if (loading.value)
    return

  if (!form.nickname?.trim()) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }

  loading.value = true
  try {
    if (passwordEnabled.value) {
      await handlePasswordUpdate()
    }

    await updateProfile({
      avatar: form.avatar,
      nickname: form.nickname?.trim(),
      realName: form.realName?.trim(),
      phone: form.phone?.trim(),
      email: form.email?.trim(),
      gender: form.gender,
    })
    await userStore.fetchUserInfo()
    uni.showToast({ title: '保存成功', icon: 'success' })
    // 保存成功后延迟跳转到"我的"页面
    setTimeout(() => {
      uni.switchTab({ url: '/pages/me/me' })
    }, 1500)
  }
  catch (error: any) {
    console.error('更新个人信息失败:', error)
    uni.showToast({ title: error.message || '保存失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

async function handlePasswordUpdate() {
  if (passwordLoading.value)
    return

  if (!passwordForm.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    throw new Error('请输入密码')
  }
  if (passwordForm.password.trim().length < 6) {
    uni.showToast({ title: '密码至少6个字符', icon: 'none' })
    throw new Error('密码至少6个字符')
  }
  if (passwordForm.confirmPassword !== passwordForm.password) {
    uni.showToast({ title: '两次密码输入不一致', icon: 'none' })
    throw new Error('两次密码输入不一致')
  }
  if (!needsPassword.value && !passwordForm.oldPassword.trim()) {
    uni.showToast({ title: '请输入旧密码', icon: 'none' })
    throw new Error('请输入旧密码')
  }

  passwordLoading.value = true
  try {
    const dto: UpdatePasswordDTO = {
      oldPassword: needsPassword.value ? '' : passwordForm.oldPassword.trim(),
      newPassword: passwordForm.password.trim(),
      confirmPassword: passwordForm.confirmPassword.trim(),
    }
    await updatePassword(dto)
    passwordForm.oldPassword = ''
    passwordForm.password = ''
    passwordForm.confirmPassword = ''
    setPassword.value = false
  }
  finally {
    passwordLoading.value = false
  }
}

function handleGoBack() {
  // 固定返回到 tabbar 我的页面
  uni.switchTab({ url: '/pages/me/me' })
}

onShow(async () => {
  if (!userStore.hasUserInfo) {
    await userStore.fetchUserInfo()
  }
  initForm()
})

onLoad(() => {
  setTimeout(() => {
    pageLoaded.value = true
  }, 100)
})
</script>

<template>
  <view class="profile-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle--1" />
      <view class="bg-circle bg-circle--2" />
      <view class="bg-circle bg-circle--3" />
    </view>

    <!-- 顶部导航栏 -->
    <view class="nav-header" :style="{ paddingTop: `${statusBarHeight}px` }">
      <view class="nav-bar">
        <view class="back-btn" @click="handleGoBack">
          <view class="i-carbon-arrow-left" />
        </view>
        <text class="nav-title">个人资料</text>
        <view class="nav-placeholder" />
      </view>
    </view>

    <!-- 主内容区域 -->
    <view class="main-scroll" :style="{ paddingTop: `${statusBarHeight + 56}px` }">
      <view class="main-content" :class="{ 'is-loaded': pageLoaded }">
        <!-- 页面标题 -->
        <view class="page-header">
          <view class="header-icon-wrapper">
            <view class="i-carbon-user-avatar header-icon" />
          </view>
          <view class="header-text">
            <text class="header-title">编辑资料</text>
            <text class="header-subtitle">完善你的信息，让服务更懂你</text>
          </view>
        </view>

        <!-- 头像昵称卡片 -->
        <view class="card avatar-card">
          <ProfileBasic
            v-model:avatar="form.avatar"
            v-model:nickname="form.nickname"
            title="头像与昵称"
            tips="展示在个人主页与互动内容中"
          />
        </view>

        <!-- 基本信息卡片 -->
        <view class="card">
          <view class="card-header">
            <view class="card-icon-wrapper">
              <view class="i-carbon-identification card-icon" />
            </view>
            <text class="card-title">基本信息</text>
          </view>

          <view class="form-list">
            <view class="form-item">
              <view class="form-label">
                <view class="i-carbon-user form-icon" />
                <text>真实姓名</text>
              </view>
              <view class="input-wrapper">
                <input v-model="form.realName" class="input" type="text" placeholder="请输入真实姓名">
              </view>
            </view>

            <view class="form-item">
              <view class="form-label">
                <view class="i-carbon-phone form-icon" />
                <text>手机号</text>
              </view>
              <view class="input-wrapper">
                <input v-model="form.phone" class="input" type="text" placeholder="请输入手机号">
              </view>
            </view>

            <view class="form-item">
              <view class="form-label">
                <view class="i-carbon-email form-icon" />
                <text>邮箱</text>
              </view>
              <view class="input-wrapper">
                <input v-model="form.email" class="input" type="text" placeholder="请输入邮箱">
              </view>
            </view>

            <view class="form-item">
              <view class="form-label">
                <view class="i-carbon-gender-male form-icon" />
                <text>性别</text>
              </view>
              <picker
                mode="selector"
                :range="genderOptions.map(option => option.label)"
                :value="genderOptions.findIndex(option => option.value === form.gender)"
                @change="handleGenderChange"
              >
                <view class="input-wrapper picker-wrapper">
                  <text class="picker-value">{{ genderLabel }}</text>
                  <view class="i-carbon-chevron-down picker-arrow" />
                </view>
              </picker>
            </view>
          </view>
        </view>

        <!-- 密码设置卡片 -->
        <view class="card">
          <view class="card-header">
            <view class="card-icon-wrapper warning">
              <view class="i-carbon-password card-icon" />
            </view>
            <view class="card-header-content">
              <text class="card-title">密码设置</text>
              <text v-if="needsPassword" class="card-tip warning">初次登录需设置密码</text>
            </view>
            <view v-if="!needsPassword" class="password-switch">
              <text class="switch-label">修改密码</text>
              <switch :checked="setPassword" color="#0ea5e9" @change="setPassword = !setPassword" />
            </view>
          </view>

          <view v-if="passwordEnabled" class="form-list password-form">
            <view v-if="!needsPassword" class="form-item">
              <view class="form-label">
                <view class="i-carbon-locked form-icon" />
                <text>旧密码</text>
              </view>
              <view class="input-wrapper password-input">
                <input
                  v-model="passwordForm.oldPassword"
                  class="input"
                  type="text"
                  :password="!showOldPassword"
                  placeholder="请输入旧密码"
                >
                <view
                  class="toggle-eye"
                  :class="showOldPassword ? 'i-carbon-view' : 'i-carbon-view-off'"
                  @click="showOldPassword = !showOldPassword"
                />
              </view>
            </view>

            <view class="form-item">
              <view class="form-label">
                <view class="i-carbon-password form-icon" />
                <text>新密码</text>
              </view>
              <view class="input-wrapper password-input">
                <input
                  v-model="passwordForm.password"
                  class="input"
                  type="text"
                  :password="!showPassword"
                  placeholder="请输入新密码（至少6位）"
                >
                <view
                  class="toggle-eye"
                  :class="showPassword ? 'i-carbon-view' : 'i-carbon-view-off'"
                  @click="showPassword = !showPassword"
                />
              </view>
            </view>

            <view class="form-item">
              <view class="form-label">
                <view class="i-carbon-locked form-icon" />
                <text>确认密码</text>
              </view>
              <view
                class="input-wrapper password-input"
                :class="{ 'has-error': passwordForm.confirmPassword && passwordForm.confirmPassword !== passwordForm.password }"
              >
                <input
                  v-model="passwordForm.confirmPassword"
                  class="input"
                  type="text"
                  :password="!showConfirmPassword"
                  placeholder="请再次输入新密码"
                >
                <view
                  class="toggle-eye"
                  :class="showConfirmPassword ? 'i-carbon-view' : 'i-carbon-view-off'"
                  @click="showConfirmPassword = !showConfirmPassword"
                />
              </view>
              <text
                v-if="passwordForm.confirmPassword && passwordForm.confirmPassword !== passwordForm.password"
                class="error-hint"
              >
                两次密码输入不一致
              </text>
            </view>
          </view>
        </view>

        <!-- 提交按钮 -->
        <view class="submit-section">
          <button class="submit-btn" :loading="loading || passwordLoading" @click="handleSubmit">
            <view v-if="!loading && !passwordLoading" class="i-carbon-checkmark btn-icon" />
            <text>保存修改</text>
          </button>
        </view>

        <!-- 底部安全区域 -->
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

$warning: #f59e0b;
$error: #ef4444;

// ==================== 页面布局 ====================
.profile-page {
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

// ==================== 导航栏 ====================
.nav-header {
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
.main-scroll {
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

// ==================== 页面头部 ====================
.page-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 12rpx 0;
}

.header-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  background: $primary-gradient;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $shadow-primary;
}

.header-icon {
  font-size: 40rpx;
  color: #ffffff;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
}

.header-subtitle {
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

.avatar-card {
  padding: 20rpx;
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
  background: rgba($primary, 0.1);
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &.warning {
    background: rgba($warning, 0.1);

    .card-icon {
      color: $warning;
    }
  }
}

.card-icon {
  font-size: 28rpx;
  color: $primary;
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

.card-tip {
  font-size: 22rpx;
  color: $text-tertiary;

  &.warning {
    color: $error;
  }
}

.password-switch {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.switch-label {
  font-size: 24rpx;
  color: $text-secondary;
}

// ==================== 表单 ====================
.form-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.password-form {
  margin-top: 4rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid $border-color;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: $text-primary;
}

.form-icon {
  font-size: 24rpx;
  color: $primary;
}

.input-wrapper {
  display: flex;
  align-items: center;
  height: 80rpx;
  padding: 0 20rpx;
  background: $bg-input;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;

  &.has-error {
    border-color: $error;
    background: rgba($error, 0.05);
  }
}

.input {
  flex: 1;
  height: 100%;
  font-size: 28rpx;
  color: $text-primary;
  background: transparent;
  border: none;
}

.picker-wrapper {
  justify-content: space-between;
}

.picker-value {
  font-size: 28rpx;
  color: $text-primary;
}

.picker-arrow {
  font-size: 28rpx;
  color: $text-tertiary;
}

.password-input {
  padding-right: 16rpx;
}

.toggle-eye {
  font-size: 36rpx;
  color: $text-tertiary;
  padding: 8rpx;
  margin-left: 8rpx;
}

.error-hint {
  font-size: 22rpx;
  color: $error;
  padding-left: 4rpx;
}

// ==================== 提交按钮 ====================
.submit-section {
  padding: 8rpx 0;
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  width: 100%;
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

  &::after {
    border: none;
  }
}

.btn-icon {
  font-size: 36rpx;
}

// ==================== 底部安全区域 ====================
.safe-bottom {
  height: calc(40rpx + env(safe-area-inset-bottom, 20px));
}
</style>
