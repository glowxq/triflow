/**
 * 图片上传组件
 * @description 支持预览、删除、进度显示的图片上传组件，使用 OSS 前端直传
 * @example
 * <ImageUpload v-model="form.avatar" biz-type="avatar" />

 * <ImageUpload v-model="form.images" multiple :limit="5" biz-type="article" />
 */

<script lang="ts" setup>
import type { FileInfoVO, UniChooseFileResult } from '@/api/types/file'
import { directUpload } from '@/api/file'

// ==================== Props 定义 ====================

interface Props {
  /** 是否多图模式 */
  multiple?: boolean
  /** 最大上传数量（多图模式） */
  limit?: number
  /** 最大文件大小（MB） */
  maxSize?: number
  /** 是否禁用 */
  disabled?: boolean
  /** 业务类型 */
  bizType?: string
  /** 业务ID */
  bizId?: number
  /** 是否使用公开上传接口（无需登录） */
  usePublicApi?: boolean
  /** 图片宽度 */
  width?: string
  /** 图片高度 */
  height?: string
  /** 提示文字 */
  tip?: string
  /** 尺寸类型 */
  sizeType?: ('original' | 'compressed')[]
  /** 来源类型 */
  sourceType?: ('album' | 'camera')[]
  /** 选择图片前的拦截器，返回 false 则终止选择 */
  beforeChoose?: () => boolean | Promise<boolean>
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  limit: 9,
  maxSize: 10,
  disabled: false,
  bizType: 'image',
  bizId: undefined,
  usePublicApi: false,
  width: '160rpx',
  height: '160rpx',
  tip: '',
  sizeType: () => ['compressed'],
  sourceType: () => ['album', 'camera'],
})

const emit = defineEmits<{
  change: [value: string | string[] | null]
  success: [file: FileInfoVO]
  error: [error: Error]
}>()

/** 单图模式：URL字符串；多图模式：URL数组 */
const modelValue = defineModel<string | string[] | null>()

// ==================== 状态定义 ====================

/** 上传中的文件列表 */
interface UploadingFile {
  uid: string
  name: string
  progress: number
  status: 'uploading' | 'success' | 'error'
  tempPath: string // 本地临时路径，用于预览
}

const uploadingFiles = ref<UploadingFile[]>([])

/** 预览状态 */
const previewVisible = ref(false)
const previewUrls = ref<string[]>([])
const previewCurrent = ref(0)

// ==================== 计算属性 ====================

/** 已上传的图片列表 */
const imageList = computed<string[]>(() => {
  if (!modelValue.value)
    return []
  if (Array.isArray(modelValue.value)) {
    return modelValue.value
  }
  return modelValue.value ? [modelValue.value] : []
})

/** 是否显示上传按钮 */
const showUploadButton = computed(() => {
  if (props.disabled)
    return false
  if (!props.multiple) {
    return imageList.value.length === 0 && uploadingFiles.value.length === 0
  }
  return imageList.value.length + uploadingFiles.value.length < props.limit
})

/** 可选择的图片数量 */
const canSelectCount = computed(() => {
  if (!props.multiple)
    return 1
  return props.limit - imageList.value.length - uploadingFiles.value.length
})

// ==================== 方法 ====================

/**
 * 选择图片
 */
async function handleChooseImage() {
  if (props.disabled || canSelectCount.value <= 0)
    return

  if (props.beforeChoose) {
    try {
      const shouldContinue = await props.beforeChoose()
      if (!shouldContinue)
        return
    }
    catch (error: any) {
      emit('error', error instanceof Error ? error : new Error('选择图片失败'))
      return
    }
  }

  uni.chooseImage({
    count: canSelectCount.value,
    sizeType: props.sizeType,
    sourceType: props.sourceType,
    success: (res) => {
      const files: UniChooseFileResult[] = res.tempFiles.map((file: any) => ({
        path: file.path,
        name: file.name || file.path.split('/').pop() || 'image',
        size: file.size,
        type: file.type,
      }))

      // 逐个上传
      files.forEach(file => handleUpload(file))
    },
    fail: (err) => {
      if (!err.errMsg?.includes('cancel')) {
        uni.showToast({ title: '选择图片失败', icon: 'none' })
      }
    },
  })
}

/**
 * 处理上传
 */
async function handleUpload(file: UniChooseFileResult) {
  // 检查文件大小
  if (file.size > props.maxSize * 1024 * 1024) {
    uni.showToast({ title: `图片大小不能超过 ${props.maxSize}MB`, icon: 'none' })
    emit('error', new Error(`图片大小不能超过 ${props.maxSize}MB`))
    return
  }

  // 创建上传中的文件记录
  const uid = `${Date.now()}-${Math.random().toString(36).slice(2)}`
  const uploadingFile: UploadingFile = {
    uid,
    name: file.name || 'image',
    progress: 0,
    status: 'uploading',
    tempPath: file.path,
  }
  uploadingFiles.value.push(uploadingFile)

  try {
    // 执行上传
    const result = await directUpload(file, {
      bizType: props.bizType,
      bizId: props.bizId,
      usePublicApi: props.usePublicApi,
      onProgress: (progress) => {
        const item = uploadingFiles.value.find(f => f.uid === uid)
        if (item) {
          item.progress = progress
        }
      },
    })

    // 上传成功
    uploadingFile.status = 'success'

    // 更新 modelValue
    const fileUrl = result.accessUrl || result.fileUrl || result.previewUrl
    if (props.multiple) {
      const currentList = Array.isArray(modelValue.value)
        ? [...modelValue.value]
        : []
      currentList.push(fileUrl)
      modelValue.value = currentList
    }
    else {
      modelValue.value = fileUrl
    }

    // 触发事件
    emit('change', modelValue.value)
    emit('success', result)

    // 移除上传中记录
    const index = uploadingFiles.value.findIndex(f => f.uid === uid)
    if (index > -1) {
      uploadingFiles.value.splice(index, 1)
    }

    uni.showToast({ title: '上传成功', icon: 'success' })
  }
  catch (error: any) {
    uploadingFile.status = 'error'
    const errorMsg = error?.message || '上传失败'
    uni.showToast({ title: errorMsg, icon: 'none' })
    emit('error', error)

    // 延迟移除失败的记录
    setTimeout(() => {
      const index = uploadingFiles.value.findIndex(f => f.uid === uid)
      if (index > -1) {
        uploadingFiles.value.splice(index, 1)
      }
    }, 2000)
  }
}

/**
 * 删除图片
 */
function handleRemove(url: string) {
  if (props.disabled)
    return

  uni.showModal({
    title: '提示',
    content: '确定要删除这张图片吗？',
    success: (res) => {
      if (res.confirm) {
        if (props.multiple) {
          const currentList = Array.isArray(modelValue.value)
            ? [...modelValue.value]
            : []
          const index = currentList.indexOf(url)
          if (index > -1) {
            currentList.splice(index, 1)
            modelValue.value = currentList.length > 0 ? currentList : null
          }
        }
        else {
          modelValue.value = null
        }
        emit('change', modelValue.value)
      }
    },
  })
}

/**
 * 预览图片
 */
function handlePreview(url: string) {
  const urls = imageList.value
  const current = urls.indexOf(url)

  uni.previewImage({
    urls,
    current: current >= 0 ? current : 0,
  })
}

/**
 * 取消上传中的文件
 */
function handleCancelUpload(uid: string) {
  const index = uploadingFiles.value.findIndex(f => f.uid === uid)
  if (index > -1) {
    uploadingFiles.value.splice(index, 1)
  }
}
</script>

<template>
  <view class="image-upload">
    <view class="image-upload-list">
      <!-- 已上传的图片 -->
      <view
        v-for="url in imageList"
        :key="url"
        class="image-upload-item"
        :style="{ width, height }"
      >
        <image
          :src="url"
          mode="aspectFill"
          class="upload-image"
          @click="handlePreview(url)"
        />
        <!-- 删除按钮 -->
        <view
          v-if="!disabled"
          class="delete-btn"
          @click.stop="handleRemove(url)"
        >
          <view class="i-carbon-close text-xs text-white" />
        </view>
      </view>

      <!-- 上传中的图片 -->
      <view
        v-for="file in uploadingFiles"
        :key="file.uid"
        class="image-upload-item uploading"
        :style="{ width, height }"
      >
        <!-- 临时预览图 -->
        <image
          :src="file.tempPath"
          mode="aspectFill"
          class="upload-image"
        />
        <!-- 进度遮罩 -->
        <view class="progress-mask">
          <view v-if="file.status === 'uploading'" class="progress-content">
            <view class="progress-bar">
              <view class="progress-inner" :style="{ width: `${file.progress}%` }" />
            </view>
            <text class="progress-text">{{ file.progress }}%</text>
          </view>
          <view v-else-if="file.status === 'error'" class="error-content">
            <view class="i-carbon-warning text-2xl text-red-500" />
            <text class="error-text">上传失败</text>
          </view>
        </view>
        <!-- 取消按钮 -->
        <view
          class="delete-btn"
          @click.stop="handleCancelUpload(file.uid)"
        >
          <view class="i-carbon-close text-xs text-white" />
        </view>
      </view>

      <!-- 上传按钮 -->
      <view
        v-if="showUploadButton"
        class="image-upload-btn"
        :style="{ width, height }"
        @click="handleChooseImage"
      >
        <view class="i-carbon-add text-3xl text-gray-400" />
        <text class="btn-text">上传图片</text>
      </view>
    </view>

    <!-- 提示文字 -->
    <view v-if="tip" class="upload-tip">
      {{ tip }}
    </view>
  </view>
</template>

<style lang="scss" scoped>
.image-upload {
  width: 100%;
}

.image-upload-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.image-upload-item {
  position: relative;
  border-radius: 12rpx;
  overflow: hidden;
  background-color: #f5f5f5;
}

.upload-image {
  width: 100%;
  height: 100%;
}

.delete-btn {
  position: absolute;
  top: 0;
  right: 0;
  width: 36rpx;
  height: 36rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 0 12rpx 0 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.uploading {
  .upload-image {
    opacity: 0.5;
  }
}

.progress-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.3);
}

.progress-content {
  width: 80%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.progress-bar {
  width: 100%;
  height: 8rpx;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 4rpx;
  overflow: hidden;
}

.progress-inner {
  height: 100%;
  background-color: #3b82f6;
  border-radius: 4rpx;
  transition: width 0.2s ease;
}

.progress-text {
  font-size: 20rpx;
  color: #fff;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.error-text {
  font-size: 20rpx;
  color: #ef4444;
}

.image-upload-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  border: 2rpx dashed #d1d5db;
  border-radius: 12rpx;
  background-color: #fafafa;
  transition: all 0.2s ease;

  &:active {
    border-color: #3b82f6;
    background-color: #eff6ff;
  }
}

.btn-text {
  font-size: 22rpx;
  color: #9ca3af;
}

.upload-tip {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #9ca3af;
}
</style>
