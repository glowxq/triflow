/**
 * CMS 文本展示组件
 * @description 用于展示用户协议、隐私政策等 CMS 文本内容
 * @supports 支持 HTML、Markdown、纯文本三种格式
 */

<script setup lang="ts">
import type { CmsTextVO } from '@/api/types/cms'
import { getPublicText } from '@/api/cms'

// ==================== Props ====================

interface Props {
  /** 文本标识（如: user_agreement, privacy_policy） */
  textKey: string
  /** 是否显示标题 */
  showTitle?: boolean
  /** 是否显示类型标签 */
  showTypeTag?: boolean
  /** 是否显示更新时间 */
  showUpdateTime?: boolean
  /** 是否使用缓存 */
  useCache?: boolean
  /** 自定义内容样式类 */
  contentClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  showTitle: true,
  showTypeTag: true,
  showUpdateTime: true,
  useCache: true,
  contentClass: '',
})

// ==================== Emits ====================

const emit = defineEmits<{
  /** 加载完成 */
  (e: 'load', data: CmsTextVO): void
  /** 加载失败 */
  (e: 'error', error: Error): void
}>()

// ==================== 状态 ====================

const loading = ref(true)
const error = ref<string | null>(null)
const textData = ref<CmsTextVO | null>(null)

// ==================== 计算属性 ====================

/**
 * 处理后的 HTML 内容
 */
const htmlContent = computed(() => {
  if (!textData.value?.textContent)
    return ''

  const content = textData.value.textContent
  const contentType = textData.value.contentType || 'html'

  switch (contentType) {
    case 'markdown':
      return markdownToHtml(content)
    case 'text':
      return textToHtml(content)
    case 'html':
    default:
      return content
  }
})

/**
 * 格式化的更新时间
 */
const formattedUpdateTime = computed(() => {
  if (!textData.value?.updateTime)
    return ''
  return formatDate(textData.value.updateTime)
})

/**
 * 文本类型标签
 */
const typeLabel = computed(() => {
  const type = textData.value?.textType
  if (!type)
    return ''

  const map: Record<string, string> = {
    article: '文章',
    notice: '公告',
    help: '帮助',
    agreement: '协议',
    about: '关于',
    other: '其他',
    faq: '帮助',
    banner: '公告',
  }

  return map[type] || type
})

// ==================== 方法 ====================

/**
 * 简单的 Markdown 转 HTML
 * 支持：标题、段落、粗体、斜体、列表、链接、换行
 */
function markdownToHtml(md: string): string {
  let html = md
  // 转义 HTML 特殊字符（保留已有的 HTML 标签）
  // .replace(/&/g, '&amp;')
  // .replace(/</g, '&lt;')
  // .replace(/>/g, '&gt;')

    // 标题 (h1 - h6)
    .replace(/^###### (.+)$/gm, '<h6>$1</h6>')
    .replace(/^##### (.+)$/gm, '<h5>$1</h5>')
    .replace(/^#### (.+)$/gm, '<h4>$1</h4>')
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')

    // 粗体和斜体
    .replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/___(.+?)___/g, '<strong><em>$1</em></strong>')
    .replace(/__(.+?)__/g, '<strong>$1</strong>')
    .replace(/_(.+?)_/g, '<em>$1</em>')

    // 分隔线
    .replace(/^---$/gm, '<hr/>')
    .replace(/^\*\*\*$/gm, '<hr/>')

    // 无序列表
    .replace(/^[\-*] (.+)$/gm, '<li>$1</li>')

    // 有序列表
    .replace(/^\d+\. (.+)$/gm, '<li>$1</li>')

    // 链接
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>')

    // 换行（两个以上连续换行变成段落）
    .replace(/\n{2,}/g, '</p><p>')

    // 单个换行变成 <br>
    .replace(/\n/g, '<br/>')

  // 包装列表项
  html = html.replace(/(<li>[\s\S]*?<\/li>)+/g, '<ul>$&</ul>')

  // 包装段落
  if (!html.startsWith('<'))
    html = `<p>${html}</p>`

  return html
}

/**
 * 纯文本转 HTML
 */
function textToHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n{2,}/g, '</p><p>')
    .replace(/\n/g, '<br/>')
    .replace(/^(.+)$/, '<p>$1</p>')
}

/**
 * 格式化日期
 */
function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
}

/**
 * 加载文本内容
 */
async function loadText() {
  loading.value = true
  error.value = null

  try {
    const data = await getPublicText(props.textKey, props.useCache)
    textData.value = data
    emit('load', data)
  }
  catch (err: any) {
    console.error('加载文本内容失败:', err)
    error.value = err.message || '加载失败'
    emit('error', err)
  }
  finally {
    loading.value = false
  }
}

// ==================== 生命周期 ====================

onMounted(() => {
  loadText()
})

// 监听 textKey 变化
watch(() => props.textKey, () => {
  loadText()
})

// 暴露方法
defineExpose({
  /** 重新加载 */
  reload: loadText,
  /** 文本数据 */
  textData,
})
</script>

<template>
  <view class="cms-text">
    <!-- 加载状态 -->
    <view v-if="loading" class="cms-text__loading">
      <wd-loading />
      <text class="cms-text__loading-text">加载中...</text>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="error" class="cms-text__error">
      <view class="i-carbon-warning cms-text__error-icon" />
      <text class="cms-text__error-text">{{ error }}</text>
      <view class="cms-text__retry" @click="loadText">
        点击重试
      </view>
    </view>

    <!-- 内容区域 -->
    <view v-else-if="textData" class="cms-text__content" :class="contentClass">
      <!-- 类型标签 -->
      <view v-if="showTypeTag && typeLabel" class="cms-text__type">
        {{ typeLabel }}
      </view>
      <!-- 标题 -->
      <view v-if="showTitle && textData.textTitle" class="cms-text__title">
        {{ textData.textTitle }}
      </view>

      <!-- 更新时间 -->
      <view v-if="showUpdateTime && formattedUpdateTime" class="cms-text__time">
        更新时间：{{ formattedUpdateTime }}
      </view>

      <!-- 文本内容 -->
      <view class="cms-text__body">
        <rich-text :nodes="htmlContent" />
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="cms-text__empty">
      <text>暂无内容</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
// 主题色
$primary: #0ea5e9;
$text-primary: #1e293b;
$text-secondary: #64748b;
$text-placeholder: #94a3b8;
$border: #e2e8f0;
$bg-page: #f8fafc;

.cms-text {
  min-height: 200rpx;

  // 加载状态
  &__loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80rpx 0;
  }

  &__loading-text {
    margin-top: 16rpx;
    font-size: 26rpx;
    color: $text-placeholder;
  }

  // 错误状态
  &__error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80rpx 0;
  }

  &__error-icon {
    font-size: 64rpx;
    color: #ef4444;
    margin-bottom: 16rpx;
  }

  &__error-text {
    font-size: 28rpx;
    color: $text-secondary;
    margin-bottom: 24rpx;
  }

  &__retry {
    padding: 12rpx 32rpx;
    background: $primary;
    color: #ffffff;
    font-size: 26rpx;
    border-radius: 32rpx;
  }

  // 内容区域
  &__content {
    padding: 32rpx;
  }

  &__type {
    display: inline-flex;
    align-items: center;
    padding: 6rpx 18rpx;
    border-radius: 999rpx;
    font-size: 22rpx;
    color: $primary;
    background: rgba(14, 165, 233, 0.12);
    margin: 0 auto 20rpx;
  }

  // 标题
  &__title {
    font-size: 40rpx;
    font-weight: 700;
    color: $text-primary;
    text-align: center;
    margin-bottom: 16rpx;
    line-height: 1.4;
  }

  // 更新时间
  &__time {
    font-size: 24rpx;
    color: $text-placeholder;
    text-align: center;
    margin-bottom: 32rpx;
  }

  // 文本内容
  &__body {
    font-size: 28rpx;
    line-height: 1.8;
    color: $text-secondary;
  }

  // 空状态
  &__empty {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 80rpx 0;
    font-size: 28rpx;
    color: $text-placeholder;
  }
}
</style>

<!-- #ifdef H5 -->
<!-- H5 平台的 rich-text 内部样式（小程序不支持标签选择器） -->
<style lang="scss">
$primary: #0ea5e9;
$text-primary: #1e293b;
$text-secondary: #64748b;
$border: #e2e8f0;

.cms-text__body {
  h1 {
    font-size: 36rpx;
    font-weight: 700;
    color: $text-primary;
    margin: 32rpx 0 16rpx;
    line-height: 1.4;
  }

  h2 {
    font-size: 32rpx;
    font-weight: 600;
    color: $text-primary;
    margin: 28rpx 0 14rpx;
    line-height: 1.4;
  }

  h3 {
    font-size: 30rpx;
    font-weight: 600;
    color: $text-primary;
    margin: 24rpx 0 12rpx;
    line-height: 1.4;
  }

  h4,
  h5,
  h6 {
    font-size: 28rpx;
    font-weight: 600;
    color: $text-primary;
    margin: 20rpx 0 10rpx;
    line-height: 1.4;
  }

  p {
    margin: 16rpx 0;
    text-align: justify;
  }

  ul,
  ol {
    padding-left: 32rpx;
    margin: 16rpx 0;
  }

  li {
    margin: 8rpx 0;
  }

  a {
    color: $primary;
    text-decoration: none;
  }

  strong {
    font-weight: 600;
    color: $text-primary;
  }

  em {
    font-style: italic;
  }

  hr {
    border: none;
    border-top: 1px solid $border;
    margin: 32rpx 0;
  }

  blockquote {
    border-left: 4rpx solid $primary;
    padding-left: 16rpx;
    margin: 16rpx 0;
    color: $text-secondary;
  }
}
</style>
<!-- #endif -->
