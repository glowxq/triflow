/**
 * 文件选择组件
 * @description 支持文件选择并直传 OSS，返回文件 URL 列表
 * @example
 * <FilePicker v-model="form.attachments" multiple :limit="3" biz-type="contract" />
 */

<script lang="ts" setup>
import type { FileInfoVO, UniChooseFileResult } from '@/api/types/file'
import { directUpload } from '@/api/file'

// ==================== Props ====================

interface Props {
  /** 是否多选 */
  multiple?: boolean
  /** 最大选择数量 */
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
  /** 是否自动上传 */
  autoUpload?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  limit: 5,
  maxSize: 10,
  disabled: false,
  bizType: 'file',
  bizId: undefined,
  usePublicApi: false,
  autoUpload: true,
})

// ==================== Emits ====================

const emit = defineEmits<{
  change: [value: string | string[] | null]
  success: [file: FileInfoVO]
  error: [error: Error]
  select: [files: UniChooseFileResult[]]
}>()

/** 单文件模式：URL字符串；多文件模式：URL数组 */
const modelValue = defineModel<string | string[] | null>()

// ==================== 状态 ====================

interface UploadingFile {
  uid: string
  name: string
  size: number
  progress: number
  status: 'uploading' | 'error' | 'pending'
  tempPath: string
}

const uploadingFiles = ref<UploadingFile[]>([])
const pendingFiles = ref<UploadingFile[]>([])

// ==================== 计算属性 ====================

const uploadedUrls = computed<string[]>(() => {
  if (!modelValue.value) {
    return []
  }
  if (Array.isArray(modelValue.value)) {
    return modelValue.value
  }
  return [modelValue.value]
})

const showUploadButton = computed(() => {
  if (props.disabled) {
    return false
  }
  const currentCount = uploadedUrls.value.length + uploadingFiles.value.length + pendingFiles.value.length
  if (!props.multiple) {
    return currentCount === 0
  }
  return currentCount < props.limit
})

const canSelectCount = computed(() => {
  if (!props.multiple) {
    return 1
  }
  const currentCount = uploadedUrls.value.length + uploadingFiles.value.length + pendingFiles.value.length
  return Math.max(props.limit - currentCount, 0)
})

// ==================== 方法 ====================

function handleChooseFile() {
  if (props.disabled || canSelectCount.value <= 0) {
    return
  }

  const handleSuccess = (res: any) => {
    const tempFiles = Array.isArray(res?.tempFiles) ? res.tempFiles : []
    let files: UniChooseFileResult[] = tempFiles.map((file: any) => ({
      path: file.path || file.tempFilePath || file.filePath,
      name: file.name || file.path?.split('/').pop() || file.tempFilePath?.split('/').pop(),
      size: file.size || 0,
      type: file.type,
    }))

    if (!files.length && Array.isArray(res?.tempFilePaths)) {
      files = res.tempFilePaths.map((path: string) => ({
        path,
        name: path.split('/').pop(),
        size: 0,
        type: undefined,
      }))
    }

    if (!files.length) {
      return
    }

    if (!props.autoUpload) {
      const pending = files.map(file => createPendingFile(file))
      pendingFiles.value.push(...pending)
      emit('select', files)
      return
    }

    files.forEach(file => handleUpload(file))
  }

  const handleFail = (err: any) => {
    if (!err?.errMsg?.includes('cancel')) {
      uni.showToast({ title: '选择文件失败', icon: 'none' })
    }
  }

  // #ifdef MP
  if (typeof uni.chooseMessageFile === 'function') {
    uni.chooseMessageFile({
      count: canSelectCount.value,
      type: 'file',
      success: handleSuccess,
      fail: handleFail,
    })
    return
  }
  if (typeof uni.chooseFile === 'function') {
    uni.chooseFile({
      count: canSelectCount.value,
      type: 'all',
      success: handleSuccess,
      fail: handleFail,
    })
    return
  }
  uni.showToast({ title: '当前平台不支持选择文件', icon: 'none' })
  return
  // #endif

  // #ifndef MP
  uni.chooseFile({
    count: canSelectCount.value,
    type: 'all',
    success: handleSuccess,
    fail: handleFail,
  })
  // #endif
}

function createPendingFile(file: UniChooseFileResult): UploadingFile {
  return {
    uid: `${Date.now()}-${Math.random().toString(36).slice(2)}`,
    name: file.name || 'file',
    size: file.size,
    progress: 0,
    status: 'pending',
    tempPath: file.path,
  }
}

async function handleUpload(file: UniChooseFileResult) {
  if (file.size > props.maxSize * 1024 * 1024) {
    uni.showToast({ title: `文件大小不能超过 ${props.maxSize}MB`, icon: 'none' })
    emit('error', new Error(`文件大小不能超过 ${props.maxSize}MB`))
    return
  }

  const uploadingFile: UploadingFile = {
    uid: `${Date.now()}-${Math.random().toString(36).slice(2)}`,
    name: file.name || 'file',
    size: file.size,
    progress: 0,
    status: 'uploading',
    tempPath: file.path,
  }
  uploadingFiles.value.push(uploadingFile)

  try {
    const result = await directUpload(file, {
      bizType: props.bizType,
      bizId: props.bizId,
      usePublicApi: props.usePublicApi,
      onProgress: (progress) => {
        const item = uploadingFiles.value.find(f => f.uid === uploadingFile.uid)
        if (item) {
          item.progress = progress
        }
      },
    })

    const fileUrl = result.accessUrl || result.fileUrl || result.previewUrl
    if (props.multiple) {
      const nextList = Array.isArray(modelValue.value) ? [...modelValue.value] : []
      nextList.push(fileUrl)
      modelValue.value = nextList
    }
    else {
      modelValue.value = fileUrl
    }

    emit('change', modelValue.value)
    emit('success', result)

    removeUploading(uploadingFile.uid)
    uni.showToast({ title: '上传成功', icon: 'success' })
  }
  catch (error: any) {
    const errorMessage = error?.message || '上传失败'
    const item = uploadingFiles.value.find(f => f.uid === uploadingFile.uid)
    if (item) {
      item.status = 'error'
    }
    emit('error', error)
    uni.showToast({ title: errorMessage, icon: 'none' })

    setTimeout(() => {
      removeUploading(uploadingFile.uid)
    }, 1500)
  }
}

function removeUploading(uid: string) {
  const index = uploadingFiles.value.findIndex(f => f.uid === uid)
  if (index > -1) {
    uploadingFiles.value.splice(index, 1)
  }
}

function removeUploaded(url: string) {
  if (props.disabled) {
    return
  }
  if (props.multiple) {
    const list = Array.isArray(modelValue.value) ? [...modelValue.value] : []
    const index = list.indexOf(url)
    if (index > -1) {
      list.splice(index, 1)
      modelValue.value = list.length ? list : null
    }
  }
  else {
    modelValue.value = null
  }
  emit('change', modelValue.value)
}

function removePending(uid: string) {
  const index = pendingFiles.value.findIndex(f => f.uid === uid)
  if (index > -1) {
    pendingFiles.value.splice(index, 1)
  }
}

async function uploadPending() {
  if (!pendingFiles.value.length) {
    return
  }
  const files = pendingFiles.value.map(file => ({
    path: file.tempPath,
    name: file.name,
    size: file.size,
  }))
  pendingFiles.value = []
  files.forEach(file => handleUpload(file))
}

function formatFileName(url: string) {
  const name = url.split('/').pop()
  return name || 'file'
}

function formatFileSize(size: number) {
  if (!size) {
    return ''
  }
  if (size < 1024) {
    return `${size}B`
  }
  if (size < 1024 * 1024) {
    return `${Math.round(size / 1024)}KB`
  }
  return `${(size / 1024 / 1024).toFixed(1)}MB`
}
</script>

<template>
  <view class="file-picker">
    <view class="file-list">
      <view v-for="url in uploadedUrls" :key="url" class="file-item">
        <view class="file-item__info">
          <text class="file-item__name">{{ formatFileName(url) }}</text>
        </view>
        <wd-button v-if="!disabled" size="small" type="text" @click="removeUploaded(url)">
          删除
        </wd-button>
      </view>

      <view v-for="file in pendingFiles" :key="file.uid" class="file-item pending">
        <view class="file-item__info">
          <text class="file-item__name">{{ file.name }}</text>
          <text class="file-item__size">待上传</text>
        </view>
        <wd-button v-if="!disabled" size="small" type="text" @click="removePending(file.uid)">
          移除
        </wd-button>
      </view>

      <view v-for="file in uploadingFiles" :key="file.uid" class="file-item uploading">
        <view class="file-item__info">
          <text class="file-item__name">{{ file.name }}</text>
          <text class="file-item__size">{{ file.progress }}%</text>
        </view>
      </view>
    </view>

    <view v-if="!disabled" class="file-actions">
      <wd-button v-if="showUploadButton" size="small" plain @click="handleChooseFile">
        选择文件
      </wd-button>
      <wd-button v-if="!autoUpload && pendingFiles.length" size="small" type="primary" plain @click="uploadPending">
        上传全部
      </wd-button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.file-picker {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 20rpx;
  background-color: #f8fafc;
  border-radius: 12rpx;

  &.uploading {
    background-color: #eef2ff;
  }

  &.pending {
    background-color: #fff7ed;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 4rpx;
  }

  &__name {
    font-size: 26rpx;
    color: #1f2937;
  }

  &__size {
    font-size: 22rpx;
    color: #94a3b8;
  }
}

.file-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}
</style>
