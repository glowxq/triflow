/**
 * AI 对话组件
 * @description 简单的AI对话演示组件，支持流式输出效果
 */

<script lang="ts" setup>
import { aiChat } from '@/api/ai'

interface Message {
  id: number
  role: 'user' | 'assistant'
  content: string
  loading?: boolean
  /** AI 模型信息（仅 assistant） */
  model?: string
  /** token 使用量（仅 assistant） */
  totalTokens?: number
}

interface Props {
  /** 组件高度 */
  height?: string
  /** 占位提示 */
  placeholder?: string
  /** AI头像 */
  aiAvatar?: string
  /** 用户头像 */
  userAvatar?: string
  /** AI 提供商 */
  provider?: string
  /** 系统提示词 */
  systemPrompt?: string
}

const props = withDefaults(defineProps<Props>(), {
  height: '500rpx',
  placeholder: '输入消息，与AI对话...',
  aiAvatar: '',
  userAvatar: '',
  provider: undefined,
  systemPrompt: undefined,
})

const emit = defineEmits<{
  (e: 'send', message: string): void
  (e: 'message', messages: Message[]): void
}>()

// 消息列表
const messages = ref<Message[]>([])
// 输入内容
const inputValue = ref('')
// 发送中状态
const sending = ref(false)
// 消息ID计数器
let messageId = 0

// 滚动到底部
const scrollViewRef = ref<any>(null)
const scrollTop = ref(0)

function scrollToBottom() {
  nextTick(() => {
    scrollTop.value = scrollTop.value + 1000
  })
}

// 流式输出效果（逐字显示）
async function streamOutput(text: string, messageIndex: number) {
  const chars = text.split('')
  for (let i = 0; i < chars.length; i++) {
    await new Promise(resolve => setTimeout(resolve, 20 + Math.random() * 15))
    messages.value[messageIndex].content = chars.slice(0, i + 1).join('')
    scrollToBottom()
  }
  messages.value[messageIndex].loading = false
}

// 发送消息（调用真实后端 AI 接口）
async function handleSend() {
  const content = inputValue.value.trim()
  if (!content || sending.value)
    return

  // 添加用户消息
  messages.value.push({
    id: ++messageId,
    role: 'user',
    content,
  })
  inputValue.value = ''
  scrollToBottom()

  emit('send', content)

  // 添加AI消息（loading状态）
  sending.value = true
  const aiMessageIndex = messages.value.length
  messages.value.push({
    id: ++messageId,
    role: 'assistant',
    content: '',
    loading: true,
  })
  scrollToBottom()

  try {
    // 调用后端 AI 聊天接口
    const result = await aiChat({
      message: content,
      provider: props.provider,
      systemPrompt: props.systemPrompt,
    })

    // 流式输出效果展示 AI 回复
    messages.value[aiMessageIndex].model = result.model
    messages.value[aiMessageIndex].totalTokens = result.totalTokens
    await streamOutput(result.content, aiMessageIndex)
  }
  catch (error: any) {
    // 请求失败时显示错误信息
    const errorMsg = error?.message || error?.data?.message || 'AI 服务暂时不可用，请稍后再试'
    messages.value[aiMessageIndex].content = `⚠️ ${errorMsg}`
    messages.value[aiMessageIndex].loading = false
    scrollToBottom()
  }
  finally {
    sending.value = false
    emit('message', messages.value)
  }
}

// 清空对话
function clearMessages() {
  messages.value = []
  messageId = 0
}

// 快捷问题
const quickQuestions = [
  '你好，你是谁？',
  '你能做什么？',
  '介绍一下 Triflow',
  '帮我写一段代码',
]

function handleQuickQuestion(question: string) {
  inputValue.value = question
  handleSend()
}

// 暴露方法
defineExpose({
  clearMessages,
  messages,
})
</script>

<template>
  <view class="ai-chat">
    <!-- 消息列表 -->
    <scroll-view
      ref="scrollViewRef"
      class="chat-messages"
      :style="{ height: props.height }"
      scroll-y
      :scroll-top="scrollTop"
      scroll-with-animation
    >
      <!-- 欢迎消息 -->
      <view v-if="messages.length === 0" class="chat-welcome">
        <view class="welcome-icon">
          <view class="i-carbon-chat-bot" />
        </view>
        <view class="welcome-title">
          AI 助手
        </view>
        <view class="welcome-desc">
          试试问我任何问题，我会尽力帮助你
        </view>
        <view class="quick-questions">
          <view
            v-for="q in quickQuestions"
            :key="q"
            class="quick-question"
            @click="handleQuickQuestion(q)"
          >
            {{ q }}
          </view>
        </view>
      </view>

      <!-- 消息列表 -->
      <view v-for="msg in messages" :key="msg.id" class="chat-message" :class="msg.role">
        <view class="message-avatar">
          <image
            v-if="msg.role === 'assistant' && aiAvatar"
            :src="aiAvatar"
            class="avatar-img"
            mode="aspectFill"
          />
          <image
            v-else-if="msg.role === 'user' && userAvatar"
            :src="userAvatar"
            class="avatar-img"
            mode="aspectFill"
          />
          <view v-else class="avatar-placeholder" :class="msg.role">
            <view :class="msg.role === 'user' ? 'i-carbon-user' : 'i-carbon-chat-bot'" />
          </view>
        </view>
        <view class="message-content">
          <view class="message-bubble" :class="msg.role">
            <text v-if="msg.content">{{ msg.content }}</text>
            <view v-if="msg.loading" class="typing-indicator">
              <view class="dot" />
              <view class="dot" />
              <view class="dot" />
            </view>
          </view>
        </view>
      </view>

      <!-- 底部占位 -->
      <view class="chat-bottom-space" />
    </scroll-view>

    <!-- 输入区域 -->
    <view class="chat-input">
      <input
        v-model="inputValue"
        class="input"
        type="text"
        :placeholder="placeholder"
        :disabled="sending"
        confirm-type="send"
        @confirm="handleSend"
      >
      <view
        class="send-btn"
        :class="{ disabled: !inputValue.trim() || sending }"
        @click="handleSend"
      >
        <view v-if="sending" class="i-carbon-circle-dash loading-icon" />
        <view v-else class="i-carbon-send" />
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
$primary: #0ea5e9;
$primary-light: #38bdf8;
$text-primary: #1e293b;
$text-secondary: #64748b;
$text-placeholder: #94a3b8;
$border: #e2e8f0;
$bg-page: #f8fafc;

.ai-chat {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
  border: 2rpx solid $border;
}

.chat-messages {
  flex: 1;
  padding: 20rpx;
}

.chat-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx 20rpx;
  text-align: center;
}

.welcome-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;

  view {
    font-size: 48rpx;
    color: #ffffff;
  }
}

.welcome-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8rpx;
}

.welcome-desc {
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 24rpx;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12rpx;
}

.quick-question {
  padding: 12rpx 20rpx;
  background: $bg-page;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: $primary;
  border: 2rpx solid rgba($primary, 0.2);
  transition: all 0.2s ease;

  &:active {
    background: rgba($primary, 0.1);
    transform: scale(0.98);
  }
}

.chat-message {
  display: flex;
  gap: 12rpx;
  margin-bottom: 20rpx;

  &.user {
    flex-direction: row-reverse;
  }
}

.message-avatar {
  flex-shrink: 0;
}

.avatar-img {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  &.assistant {
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);

    view {
      font-size: 32rpx;
      color: #ffffff;
    }
  }

  &.user {
    background: #f1f5f9;

    view {
      font-size: 32rpx;
      color: $text-secondary;
    }
  }
}

.message-content {
  flex: 1;
  max-width: 80%;
}

.message-bubble {
  display: inline-block;
  padding: 16rpx 20rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;

  &.assistant {
    background: $bg-page;
    color: $text-primary;
    border-top-left-radius: 8rpx;
  }

  &.user {
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    color: #ffffff;
    border-top-right-radius: 8rpx;
  }
}

.typing-indicator {
  display: flex;
  gap: 6rpx;
  padding: 4rpx 0;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: $text-placeholder;
  animation: bounce 1.4s infinite ease-in-out both;

  &:nth-child(1) {
    animation-delay: -0.32s;
  }

  &:nth-child(2) {
    animation-delay: -0.16s;
  }
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.chat-bottom-space {
  height: 20rpx;
}

.chat-input {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 20rpx;
  background: #ffffff;
  border-top: 2rpx solid $border;
}

.input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  background: $bg-page;
  border-radius: 36rpx;
  font-size: 28rpx;
  color: $text-primary;
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s ease;
  box-shadow: 0 4rpx 12rpx rgba($primary, 0.3);

  view {
    font-size: 32rpx;
    color: #ffffff;
  }

  &:active {
    transform: scale(0.95);
  }

  &.disabled {
    background: $border;
    box-shadow: none;

    view {
      color: $text-placeholder;
    }
  }
}

.loading-icon {
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
</style>
