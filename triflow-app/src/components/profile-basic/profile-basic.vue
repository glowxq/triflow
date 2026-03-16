/**
 * 头像与昵称编辑组件
 * @description 复用头像上传与昵称输入的表单区块
 * @note 微信小程序使用新版 chooseAvatar 和 nickname 输入
 */

<script lang="ts" setup>
import { computed } from 'vue'
import { uploadImageFromPath } from '@/api/file'
import ImageUpload from '@/components/image-upload/image-upload.vue'

const props = withDefaults(
  defineProps<{
    avatar: string
    nickname: string
    title?: string
    tips?: string
    showTitle?: boolean
    nicknamePlaceholder?: string
    avatarBeforeChoose?: () => boolean | Promise<boolean>
    /** 是否使用公开上传接口（无需登录） */
    usePublicApi?: boolean
  }>(),
  {
    title: '头像与昵称',
    tips: '',
    showTitle: true,
    nicknamePlaceholder: '请输入昵称',
    usePublicApi: false,
  },
)

const emit = defineEmits<{
  (event: 'update:avatar', value: string): void
  (event: 'update:nickname', value: string): void
  (event: 'nicknameFocus'): void
}>()

const avatarValue = computed({
  get: () => props.avatar,
  set: value => emit('update:avatar', value),
})

const nicknameValue = computed({
  get: () => props.nickname,
  set: value => emit('update:nickname', value),
})

// 微信小程序头像上传中状态
const wxAvatarUploading = ref(false)

function handleNicknameFocus() {
  emit('nicknameFocus')
}

/**
 * 处理微信小程序头像选择（新版 API）
 * @param event chooseAvatar 事件
 */
async function handleWxChooseAvatar(event: { detail: { avatarUrl: string } }) {
  const tempFilePath = event.detail.avatarUrl
  if (!tempFilePath) {
    uni.showToast({ title: '获取头像失败', icon: 'none' })
    return
  }

  wxAvatarUploading.value = true
  try {
    // 上传临时头像到服务器
    const result = await uploadImageFromPath(tempFilePath, `wx-avatar-${Date.now()}.jpg`, {
      bizType: 'avatar',
      usePublicApi: props.usePublicApi,
    })
    avatarValue.value = result.accessUrl || result.fileUrl || result.previewUrl
    uni.showToast({ title: '头像已更新', icon: 'success' })
  }
  catch (error: any) {
    console.error('上传微信头像失败:', error)
    uni.showToast({ title: error.message || '上传头像失败', icon: 'none' })
  }
  finally {
    wxAvatarUploading.value = false
  }
}

/**
 * 处理微信小程序昵称输入（新版 API）
 * @param event nickname input 事件
 */
function handleWxNicknameInput(event: { detail: { value: string } }) {
  const nickname = event.detail.value?.trim()
  if (nickname) {
    nicknameValue.value = nickname
  }
}

/**
 * 处理微信小程序昵称确认
 */
function handleWxNicknameBlur(event: { detail: { value: string } }) {
  const nickname = event.detail.value?.trim()
  if (nickname) {
    nicknameValue.value = nickname
  }
}
</script>

<template>
  <view class="basic-block">
    <view v-if="showTitle" class="basic-header">
      <view class="basic-title">
        {{ title }}
      </view>
      <view v-if="tips" class="basic-tips">
        {{ tips }}
      </view>
    </view>

    <!-- #ifdef MP-WEIXIN -->
    <!-- 微信小程序使用新版 chooseAvatar -->
    <view class="basic-avatar">
      <button
        class="wx-avatar-btn"
        open-type="chooseAvatar"
        :loading="wxAvatarUploading"
        @chooseavatar="handleWxChooseAvatar"
      >
        <image
          v-if="avatarValue"
          :src="avatarValue"
          class="avatar-img"
          mode="aspectFill"
        />
        <view v-else class="avatar-placeholder">
          <view class="i-carbon-user-avatar placeholder-icon" />
        </view>
        <view v-if="wxAvatarUploading" class="avatar-loading">
          <view class="i-carbon-circle-dash loading-icon" />
        </view>
      </button>
      <view class="avatar-tip">
        点击选择头像
      </view>
    </view>
    <!-- #endif -->

    <!-- #ifndef MP-WEIXIN -->
    <!-- 非微信平台使用 ImageUpload 组件 -->
    <view class="basic-avatar">
      <ImageUpload
        v-model="avatarValue"
        biz-type="avatar"
        width="180rpx"
        height="180rpx"
        :before-choose="avatarBeforeChoose"
        :use-public-api="usePublicApi"
      />
    </view>
    <!-- #endif -->

    <view v-if="$slots.actions" class="basic-actions">
      <slot name="actions" />
    </view>

    <view class="basic-field">
      <view class="basic-label">
        昵称
      </view>
      <!-- #ifdef MP-WEIXIN -->
      <!-- 微信小程序使用 type="nickname" 获取微信昵称 -->
      <input
        :value="nicknameValue"
        class="basic-input"
        type="nickname"
        :maxlength="20"
        :placeholder="nicknamePlaceholder"
        @focus="handleNicknameFocus"
        @input="handleWxNicknameInput"
        @blur="handleWxNicknameBlur"
      >
      <!-- #endif -->
      <!-- #ifndef MP-WEIXIN -->
      <input
        v-model="nicknameValue"
        class="basic-input"
        type="text"
        :maxlength="20"
        :placeholder="nicknamePlaceholder"
        @focus="handleNicknameFocus"
      >
      <!-- #endif -->
    </view>
  </view>
</template>

<style lang="scss" scoped>
$text-primary: #0f172a;
$text-secondary: #475569;
$text-tertiary: #94a3b8;
$bg-input: #f1f5f9;
$primary: #0ea5e9;

// 小程序不支持 * 通用选择器，使用具体的子元素间距
.basic-block {
  display: flex;
  flex-direction: column;
}

// 使用具体的类选择器替代 > * + *
.basic-header {
  margin-top: 0;
  display: flex;
  flex-direction: column;
}

.basic-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 16rpx;
}

.basic-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 16rpx;
  margin-left: -8rpx;
  margin-right: -8rpx;
}

.basic-field {
  display: flex;
  flex-direction: column;
  margin-top: 16rpx;
}

.basic-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
}

.basic-tips {
  font-size: 22rpx;
  color: $text-secondary;
  margin-top: 6rpx;
}

.basic-label {
  font-size: 24rpx;
  font-weight: 500;
  color: $text-primary;
}

.basic-input {
  border: 2rpx solid transparent;
  border-radius: 14rpx;
  padding: 0 16rpx;
  height: 76rpx;
  font-size: 26rpx;
  background: $bg-input;
  color: $text-primary;
  margin-top: 6rpx;
}

// 微信头像按钮样式
.wx-avatar-btn {
  position: relative;
  width: 180rpx;
  height: 180rpx;
  padding: 0;
  margin: 0;
  border: none;
  border-radius: 50%;
  overflow: hidden;
  background: transparent;

  &::after {
    display: none;
  }
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-input;
  border-radius: 50%;
  border: 2rpx dashed $text-tertiary;
}

.placeholder-icon {
  font-size: 80rpx;
  color: $text-tertiary;
}

.avatar-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
}

.loading-icon {
  font-size: 48rpx;
  color: #fff;
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

.avatar-tip {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: $text-tertiary;
}
</style>
