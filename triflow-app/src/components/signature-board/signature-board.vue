/**
 * 签名板组件 (基于 wot-design-uni 的 wd-signature)
 * @description 封装 wot-ui 签名组件，支持签名完成确认后自动上传图片到 S3，可双向绑定获取图片URL
 * @features
 * - 基于 wd-signature 组件
 * - 支持压感模式（笔锋效果）
 * - 支持撤销/恢复功能
 * - 支持横屏模式
 * - 签名完成后自动上传到 S3
 * - 支持 v-model 双向绑定
 * @example
 * <SignatureBoard v-model="signatureUrl" />
 * <SignatureBoard v-model="signatureUrl" landscape />
 */

<script lang="ts" setup>
import type { FileInfoVO, UniChooseFileResult } from '@/api/types/file'
import { directUpload } from '@/api/file'

/** 简单的延迟函数 */
function pause(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// ==================== Props ====================

interface Props {
  /** 是否禁用 */
  disabled?: boolean
  /** 业务类型 */
  bizType?: string
  /** 业务ID */
  bizId?: number
  /** 是否使用公开上传接口（无需登录） */
  usePublicApi?: boolean
  /** 最大文件大小（MB） */
  maxSize?: number
  /** 画布宽度（仅普通模式有效） */
  width?: number
  /** 画布高度（仅普通模式有效） */
  height?: number
  /** 画笔颜色 */
  penColor?: string
  /** 画笔宽度 */
  lineWidth?: number
  /** 导出图片缩放比例 */
  exportScale?: number
  /** 是否启用压力模式（笔锋效果） */
  pressure?: boolean
  /** 是否启用横屏签名模式 */
  landscape?: boolean
  /** 背景色 */
  backgroundColor?: string
  /** 笔锋模式最小宽度 */
  minWidth?: number
  /** 笔锋模式最大宽度 */
  maxWidth?: number
  /** 笔锋模式速度阈值 */
  minSpeed?: number
  /** 是否显示预览 */
  showPreview?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  bizType: 'signature',
  bizId: undefined,
  usePublicApi: false,
  maxSize: 5,
  width: 300,
  height: 200,
  penColor: '#111827',
  lineWidth: 3,
  exportScale: 2,
  pressure: true,
  landscape: false,
  backgroundColor: '#ffffff',
  minWidth: 1,
  maxWidth: 6,
  minSpeed: 1.5,
  showPreview: true,
})

// ==================== Emits ====================

const emit = defineEmits<{
  change: [value: string | null]
  confirm: [tempFilePath: string]
  uploadSuccess: [file: FileInfoVO]
  uploadError: [error: Error]
  clear: []
}>()

/** 签名图片 URL（v-model 双向绑定） */
const modelValue = defineModel<string | null>()

// ==================== 常量 ====================

const DEFAULT_FILE_NAME = 'signature.png'
const DEFAULT_FILE_TYPE = 'image/png'

// ==================== Refs ====================

const signatureRef = ref<any>(null)
const landscapeSignatureRef = ref<any>(null)
const canvasWrapperRef = ref<any>(null)

// 缓存组件实例，避免在回调中调用 getCurrentInstance() 返回 null
const componentInstance = getCurrentInstance()

// ==================== 状态 ====================

const popupVisible = ref(false)
const landscapeVisible = ref(false)
const tempFilePath = ref<string | null>(null)
const uploading = ref(false)
const uploadProgress = ref(0)
const landscapeInited = ref(false)
const isLandscapeResult = ref(false)
const signatureInited = ref(false)

// 普通模式画布尺寸（动态计算）
const canvasWidth = ref(0)
const canvasHeight = ref(0)

// 横屏画布尺寸
const landscapeCanvasWidth = ref(0)
const landscapeCanvasHeight = ref(0)

// 横屏模式撤销/恢复状态
const landscapeCanUndo = ref(false)
const landscapeCanRedo = ref(false)

// ==================== 计算属性 ====================

const previewUrl = computed(() => modelValue.value || tempFilePath.value)

// ==================== 工具方法 ====================

function rpxToPx(rpx: number) {
  const systemInfo = uni.getWindowInfo()
  return Math.round(rpx * systemInfo.windowWidth / 750)
}

/**
 * 同步横屏模式画布尺寸
 * 使用「横向画布方案」：不旋转画布，而是创建一个横向的画布（宽 > 高）
 * 工具栏在左侧（按钮旋转90度显示），画布填满剩余空间
 * 这样触摸坐标不需要转换，用户从上往下签名，实际效果是横向签名
 */
function syncLandscapeCanvasSize() {
  const { windowWidth, windowHeight, safeAreaInsets } = uni.getWindowInfo()
  const toolbarWidth = 48 // 左侧工具栏宽度 (px)
  const padding = 16 // padding (px)
  const safeTop = safeAreaInsets?.top || 0
  const safeBottom = safeAreaInsets?.bottom || 0

  // 画布宽度 = 屏幕宽度 - 左侧工具栏 - padding
  landscapeCanvasWidth.value = Math.max(windowWidth - toolbarWidth - padding * 2, 200)

  // 画布高度 = 屏幕高度 - 安全区 - padding
  landscapeCanvasHeight.value = Math.max(windowHeight - safeTop - safeBottom - padding * 2, 150)
}

/**
 * 同步普通模式画布尺寸（根据容器大小动态计算）
 */
function syncCanvasSize(): Promise<void> {
  return new Promise((resolve) => {
    nextTick(() => {
      // 使用缓存的组件实例
      const query = uni.createSelectorQuery().in(componentInstance?.proxy)
      query.select('.signature-board__canvas-wrapper')
        .boundingClientRect((rect: any) => {
          if (rect) {
            // 容器内边距 border 2rpx = 1px
            canvasWidth.value = Math.floor(rect.width - 2)
            canvasHeight.value = props.height || 200
          }
          else {
            // 回退到默认值
            canvasWidth.value = props.width || 300
            canvasHeight.value = props.height || 200
          }
          resolve()
        })
        .exec()
    })
  })
}

// ==================== 弹窗操作 ====================

async function openPopup() {
  if (props.disabled) return

  if (props.landscape) {
    syncLandscapeCanvasSize()
    landscapeVisible.value = true
    landscapeInited.value = false

    // 延迟初始化画布
    await pause(100)
    landscapeInited.value = true

    nextTick(() => {
      landscapeSignatureRef.value?.init(true)
    })
  }
  else {
    popupVisible.value = true
  }
}

async function closePopup() {
  popupVisible.value = false
  landscapeVisible.value = false
  landscapeInited.value = false
  signatureInited.value = false
}

/**
 * 弹窗打开后初始化签名画布
 */
async function handlePopupAfterEnter() {
  // 先计算容器尺寸
  await syncCanvasSize()
  signatureInited.value = true

  // 延迟初始化画布，确保尺寸已应用
  await pause(50)
  nextTick(() => {
    signatureRef.value?.init(true)
  })
}

// ==================== 签名确认与上传 ====================

/**
 * 将竖向签名图片逆时针旋转90度变为横向
 * 用于横屏模式：画布是竖向的，签名完成后需要旋转成横向图片
 */
function rotateImageToLandscape(filePath: string): Promise<string> {
  return new Promise((resolve) => {
    uni.getImageInfo({
      src: filePath,
      success: (imageInfo) => {
        const origW = imageInfo.width
        const origH = imageInfo.height

        const query = uni.createSelectorQuery().in(componentInstance?.proxy)
        query.select('#rotateCanvas')
          .fields({ node: true, size: true })
          .exec((res: any) => {
            if (!res?.[0]?.node) {
              resolve(filePath)
              return
            }

            const canvas = res[0].node
            const ctx = canvas.getContext('2d')

            // 旋转后：宽高互换
            canvas.width = origH
            canvas.height = origW

            // 加载图片
            const img = canvas.createImage()
            img.onload = () => {
              // 逆时针旋转90度：原来的上→左，下→右
              ctx.translate(0, canvas.height)
              ctx.rotate(-Math.PI / 2)
              ctx.drawImage(img, 0, 0, origW, origH)

              // 导出旋转后的图片
              uni.canvasToTempFilePath({
                canvas,
                success: (exportRes: any) => {
                  resolve(exportRes.tempFilePath)
                },
                fail: () => resolve(filePath),
              })
            }
            img.onerror = () => resolve(filePath)
            img.src = filePath
          })
      },
      fail: () => resolve(filePath),
    })
  })
}

/**
 * 签名完成回调
 */
async function handleConfirm(result: { tempFilePath: string, success?: boolean }) {
  let filePath = result.tempFilePath

  if (!filePath) {
    uni.showToast({ title: '签名生成失败，请重试', icon: 'none' })
    return
  }

  const isFromLandscape = props.landscape && landscapeVisible.value

  // 横屏模式：将竖向签名图片逆时针旋转90度变为横向
  if (isFromLandscape) {
    filePath = await rotateImageToLandscape(filePath)
  }

  tempFilePath.value = filePath
  isLandscapeResult.value = isFromLandscape

  emit('confirm', filePath)

  // 关闭弹窗
  await closePopup()

  // 自动上传
  await uploadSignature(filePath)
}

/**
 * 上传签名图片到 S3
 */
async function uploadSignature(filePath: string) {
  if (!filePath || uploading.value) return

  try {
    uploading.value = true
    uploadProgress.value = 0

    const file = await buildUploadFile(filePath)

    // 检查文件大小
    if (file.size > props.maxSize * 1024 * 1024) {
      throw new Error(`签名图片大小不能超过 ${props.maxSize}MB`)
    }

    const result = await directUpload(file, {
      bizType: props.bizType,
      bizId: props.bizId,
      usePublicApi: props.usePublicApi,
      onProgress: (progress) => {
        uploadProgress.value = progress
      },
    })

    // 获取上传后的 URL
    const fileUrl = result.accessUrl || result.fileUrl || result.previewUrl

    // 更新 v-model
    modelValue.value = fileUrl
    emit('change', modelValue.value)
    emit('uploadSuccess', result)

    uni.showToast({ title: '签名上传成功', icon: 'success' })
  }
  catch (error: any) {
    const errorMessage = error?.message || '签名上传失败'
    uni.showToast({ title: errorMessage, icon: 'none' })
    emit('uploadError', error)
  }
  finally {
    uploading.value = false
  }
}

/**
 * 构建上传文件对象
 */
async function buildUploadFile(filePath: string): Promise<UniChooseFileResult> {
  const fileName = filePath.split('/').pop() || DEFAULT_FILE_NAME
  const size = await getFileSize(filePath)
  return {
    path: filePath,
    name: fileName,
    size,
    type: DEFAULT_FILE_TYPE,
  }
}

/**
 * 获取文件大小
 */
function getFileSize(filePath: string): Promise<number> {
  return new Promise((resolve) => {
    // #ifdef MP-WEIXIN
    const fs = uni.getFileSystemManager()
    fs.getFileInfo({
      filePath,
      success: res => resolve(res.size),
      fail: () => resolve(0),
    })
    // #endif
    // #ifndef MP-WEIXIN
    uni.getFileInfo({
      filePath,
      success: res => resolve(res.size),
      fail: () => resolve(0),
    })
    // #endif
  })
}

// ==================== 横屏模式操作 ====================

/**
 * 更新横屏模式的撤销/恢复状态（从 slot 中同步）
 */
function updateLandscapeState(canUndo: boolean, canRedo: boolean) {
  landscapeCanUndo.value = canUndo
  landscapeCanRedo.value = canRedo
  return ''
}

function handleLandscapeRevoke() {
  landscapeSignatureRef.value?.revoke?.()
}

function handleLandscapeRestore() {
  landscapeSignatureRef.value?.restore?.()
}

function handleLandscapeClear() {
  landscapeSignatureRef.value?.clear?.()
}

function handleLandscapeConfirm() {
  landscapeSignatureRef.value?.confirm?.()
}

// ==================== 清除签名 ====================

function clearSignature() {
  modelValue.value = null
  tempFilePath.value = null
  isLandscapeResult.value = false
  emit('change', null)
  emit('clear')
}

// ==================== 暴露方法 ====================

defineExpose({
  /** 打开签名弹窗 */
  open: openPopup,
  /** 清除签名 */
  clear: clearSignature,
})
</script>

<template>
  <view class="signature-board">
    <!-- 预览区域 -->
    <view
      class="signature-board__preview"
      :class="{
        'is-disabled': disabled,
        'is-landscape': isLandscapeResult,
      }"
      @click="openPopup"
    >
      <image
        v-if="showPreview && previewUrl"
        class="signature-board__image"
        :src="previewUrl"
        mode="aspectFit"
      />
      <view v-else class="signature-board__placeholder">
        <view class="i-carbon-pen text-2xl text-gray-400" />
        <text class="signature-board__placeholder-text">点击签名</text>
      </view>

      <!-- 上传进度遮罩 -->
      <view v-if="uploading" class="signature-board__mask">
        <wd-loading />
        <text class="signature-board__progress">上传中 {{ uploadProgress }}%</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view v-if="!disabled" class="signature-board__actions">
      <wd-button size="small" plain @click="openPopup">
        {{ previewUrl ? '重新签名' : '开始签名' }}
      </wd-button>
      <wd-button v-if="previewUrl" size="small" type="text" @click="clearSignature">
        清除
      </wd-button>
    </view>

    <!-- 普通模式弹窗 -->
    <wd-popup
      v-if="!landscape"
      v-model="popupVisible"
      position="bottom"
      :close-on-click-modal="false"
      :safe-area-inset-bottom="true"
      :lock-scroll="true"
      root-portal
      @after-enter="handlePopupAfterEnter"
      @close="closePopup"
    >
      <view class="signature-board__popup" @touchmove.stop.prevent>
        <view class="signature-board__header">
          <view class="signature-board__close" @click="closePopup">
            <view class="i-carbon-close" />
          </view>
          <text class="signature-board__title">手写签名</text>
          <view class="signature-board__header-placeholder" />
        </view>

        <view ref="canvasWrapperRef" class="signature-board__canvas-wrapper" @touchmove.stop.prevent>
          <wd-signature
            v-if="signatureInited"
            ref="signatureRef"
            :height="canvasHeight"
            :width="canvasWidth"
            :pen-color="penColor"
            :line-width="lineWidth"
            :export-scale="exportScale"
            :pressure="pressure"
            :min-width="minWidth"
            :max-width="maxWidth"
            :min-speed="minSpeed"
            :disabled="disabled"
            :background-color="backgroundColor"
            enable-history
            @confirm="handleConfirm"
          >
            <template #footer="{ clear, confirm, revoke, restore, canUndo, canRedo }">
              <view class="signature-board__footer">
                <wd-button size="small" plain :disabled="!canUndo" @click="revoke">
                  撤销
                </wd-button>
                <wd-button size="small" plain :disabled="!canRedo" @click="restore">
                  恢复
                </wd-button>
                <wd-button size="small" plain @click="clear">
                  清空
                </wd-button>
                <wd-button size="small" type="primary" @click="confirm">
                  确认签名
                </wd-button>
              </view>
            </template>
          </wd-signature>

          <view class="signature-board__tip">
            请在此区域签名
          </view>
        </view>
      </view>
    </wd-popup>

    <!-- 用于横屏模式图片旋转的隐藏 canvas -->
    <canvas v-if="landscape" type="2d" id="rotateCanvas" class="signature-board__hidden-canvas" />

    <!-- 横屏模式全屏覆盖层（横向画布方案：工具栏在左侧旋转显示） -->
    <view
      v-if="landscape && landscapeVisible"
      class="signature-board__landscape"
      @touchmove.stop.prevent
    >
      <!-- 左侧工具栏（按钮旋转90度显示） -->
      <view class="signature-board__landscape-toolbar">
        <view class="signature-board__landscape-toolbar-inner">
          <!-- 旋转后：完成按钮在最下方 -->
          <wd-button size="small" type="primary" @click="handleLandscapeConfirm">
            完成
          </wd-button>
          <wd-button size="small" plain @click="handleLandscapeClear">
            清空
          </wd-button>
          <wd-button size="small" plain :disabled="!landscapeCanRedo" @click="handleLandscapeRestore">
            恢复
          </wd-button>
          <wd-button size="small" plain :disabled="!landscapeCanUndo" @click="handleLandscapeRevoke">
            撤销
          </wd-button>
          <!-- 旋转后：关闭按钮在最上方 -->
          <view class="signature-board__landscape-close" @click="closePopup">
            <view class="i-carbon-close" />
          </view>
        </view>
      </view>

      <!-- 签名区域 -->
      <view class="signature-board__landscape-canvas">
        <wd-signature
          v-if="landscapeInited"
          ref="landscapeSignatureRef"
          :height="landscapeCanvasHeight"
          :width="landscapeCanvasWidth"
          :pen-color="penColor"
          :line-width="lineWidth"
          :export-scale="exportScale"
          :pressure="pressure"
          :min-width="minWidth"
          :max-width="maxWidth"
          :min-speed="minSpeed"
          :background-color="backgroundColor"
          enable-history
          @confirm="handleConfirm"
        >
          <!-- 隐藏默认 footer，使用自定义工具栏 -->
          <template #footer="{ canUndo, canRedo }">
            <view style="display: none;">
              {{ updateLandscapeState(canUndo, canRedo) }}
            </view>
          </template>
        </wd-signature>

        <view class="signature-board__landscape-tip">
          请在此区域签名
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.signature-board {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

// ==================== 预览区域 ====================

.signature-board__preview {
  position: relative;
  width: 100%;
  min-height: 200rpx;
  border: 2rpx dashed #cbd5e1;
  border-radius: 16rpx;
  background-color: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 16rpx;
  box-sizing: border-box;

  &.is-disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &.is-landscape {
    min-height: 120rpx;
    height: 0;
    padding-bottom: 33.33%;
  }
}

.signature-board__image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.signature-board__placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.signature-board__placeholder-text {
  font-size: 26rpx;
  color: #64748b;
}

.signature-board__mask {
  position: absolute;
  inset: 0;
  background-color: rgba(15, 23, 42, 0.35);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.signature-board__progress {
  color: #fff;
  font-size: 24rpx;
}

// ==================== 操作按钮 ====================

.signature-board__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

// ==================== 普通模式弹窗 ====================

.signature-board__popup {
  padding: 24rpx 24rpx 32rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom) + 120rpx);
  background: #fff;
}

.signature-board__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.signature-board__close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #64748b;
  border-radius: 50%;
  background: #f1f5f9;

  &:active {
    opacity: 0.7;
  }
}

.signature-board__header-placeholder {
  width: 56rpx;
}

.signature-board__title {
  font-size: 32rpx;
  font-weight: 600;
  color: #111827;
}

.signature-board__canvas-wrapper {
  position: relative;
  border: 2rpx solid #e2e8f0;
  border-radius: 16rpx;
  overflow: hidden;
  background-color: #ffffff;
  touch-action: none;

  // 让 wd-signature 填满容器
  :deep(.wd-signature) {
    width: 100%;
  }

  :deep(.wd-signature__content) {
    width: 100%;
  }
}

.signature-board__tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 28rpx;
  color: #94a3b8;
  pointer-events: none;
  z-index: 0;
}

.signature-board__footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 16rpx 0 0;
}

// ==================== 隐藏 canvas（用于横屏图片旋转） ====================

.signature-board__hidden-canvas {
  position: fixed;
  left: -9999px;
  top: -9999px;
  width: 1px;
  height: 1px;
  pointer-events: none;
  opacity: 0;
}

// ==================== 横屏模式（横向画布方案：工具栏旋转显示） ====================

// 全屏覆盖层
.signature-board__landscape {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: #fff;
  display: flex;
  flex-direction: row;
  padding: 16px;
  padding-top: calc(16px + env(safe-area-inset-top));
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  padding-left: 56px; // 左侧工具栏占位
  box-sizing: border-box;
}

// 左侧工具栏容器（固定在左侧）
.signature-board__landscape-toolbar {
  position: fixed;
  left: 0;
  top: 50%;
  width: 56px;
  transform: translateY(-50%);
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

// 工具栏内容（旋转90度显示）
.signature-board__landscape-toolbar-inner {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 16rpx;
  white-space: nowrap;
  width: max-content;
  transform: rotate(90deg);
  transform-origin: center center;
}

.signature-board__landscape-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #64748b;
  border-radius: 50%;
  background: #f1f5f9;
  flex-shrink: 0;

  &:active {
    opacity: 0.7;
  }
}

// 签名画布区域（占满剩余空间）
.signature-board__landscape-canvas {
  flex: 1;
  position: relative;
  border: 2rpx solid #e2e8f0;
  border-radius: 16rpx;
  overflow: hidden;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.signature-board__landscape-canvas :deep(.wd-signature) {
  width: 100%;
  height: 100%;
}

.signature-board__landscape-canvas :deep(.wd-signature__content) {
  width: 100%;
  height: 100%;
}

// 隐藏 wd-signature 默认的 footer
.signature-board__landscape-canvas :deep(.wd-signature__footer) {
  display: none !important;
}

.signature-board__landscape-tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 32rpx;
  color: #94a3b8;
  pointer-events: none;
  z-index: 0;
}
</style>
