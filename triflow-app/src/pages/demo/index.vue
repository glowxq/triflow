/**
 * Demo 页面
 * @description 展示已封装组件，方便测试
 */

<script lang="ts" setup>
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import * as echarts from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { provideEcharts } from 'uni-echarts/shared'
import { computed } from 'vue'
import { TextKey } from '@/api/types/cms'
import AccessControl from '@/components/access-control/access-control.vue'
import CmsText from '@/components/cms-text/cms-text.vue'
import DeptSelector from '@/components/dept-selector/dept-selector.vue'
import FilePicker from '@/components/file-picker/file-picker.vue'
import ImageUpload from '@/components/image-upload/image-upload.vue'
import ProfileBasic from '@/components/profile-basic/profile-basic.vue'
import AiChat from '@/components/ai-chat/ai-chat.vue'
import SignatureBoard from '@/components/signature-board/signature-board.vue'

defineOptions({
  name: 'Demo',
})

definePage({
  style: {
    navigationBarTitleText: 'Demo',
  },
})

provideEcharts(echarts)
echarts.use([GridComponent, LegendComponent, TooltipComponent, LineChart, BarChart, PieChart, CanvasRenderer])

const avatar = ref('')
const basicAvatar = ref('')
const basicNickname = ref('')
const gallery = ref<string[]>([])
const attachments = ref<string[]>([])
const signatureBoard = ref<string | null>(null)
const signatureBoardLandscape = ref<string | null>(null)
const deptId = ref<number | null>(null)

const galleryCount = computed(() => gallery.value.length)
const attachmentCount = computed(() => attachments.value.length)
const signatureBoardStatus = computed(() => (signatureBoard.value ? '已上传' : '未签名'))
const signatureBoardLandscapeStatus = computed(() => (signatureBoardLandscape.value ? '已上传' : '未签名'))
const deptStatus = computed(() => (deptId.value ? `已选择：${deptId.value}` : '未选择'))
const basicNicknameStatus = computed(() => (basicNickname.value ? basicNickname.value : '未设置'))

// AI聊天状态
const aiChatRef = ref<InstanceType<typeof AiChat> | null>(null)
const aiMessageCount = ref(0)

function onAiMessage(messages: any[]) {
  aiMessageCount.value = messages.length
}

function clearAiChat() {
  aiChatRef.value?.clearMessages()
  aiMessageCount.value = 0
}

const lineOption = ref({
  tooltip: { trigger: 'axis' },
  grid: { left: 24, right: 16, top: 24, bottom: 24, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    axisTick: { show: false },
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'line',
      smooth: true,
      data: [120, 200, 150, 80, 70, 110, 130],
      areaStyle: { color: 'rgba(14, 165, 233, 0.18)' },
      lineStyle: { color: '#0ea5e9', width: 3 },
      itemStyle: { color: '#0ea5e9' },
    },
  ],
})

const barOption = ref({
  tooltip: { trigger: 'axis' },
  grid: { left: 24, right: 16, top: 24, bottom: 24, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['A', 'B', 'C', 'D', 'E'],
    axisTick: { show: false },
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'bar',
      data: [23, 45, 56, 33, 60],
      barWidth: '45%',
      itemStyle: { color: '#0ea5e9', borderRadius: [8, 8, 0, 0] },
    },
  ],
})

const pieOption = ref({
  tooltip: { trigger: 'item' },
  legend: { top: 'bottom' },
  series: [
    {
      type: 'pie',
      radius: ['35%', '60%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      data: [
        { value: 1048, name: '搜索' },
        { value: 735, name: '直达' },
        { value: 580, name: '邮件' },
        { value: 484, name: '联盟' },
        { value: 300, name: '视频' },
      ],
    },
  ],
})
</script>

<template>
  <view class="components-page">
    <view class="hero">
      <view class="hero-title">
        Demo Lab
      </view>
      <view class="hero-desc">
        组件集中展示区，提供实时 demo 与返回值，便于调试与验证。
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          ImageUpload
        </view>
        <view class="section-tag">
          图片上传
        </view>
      </view>
      <view class="section-desc">
        支持单图、图库上传，适用于头像与相册场景。
      </view>
      <view class="demo-block">
        <view class="demo-title">
          单图上传
        </view>
        <ImageUpload
          v-model="avatar"
          biz-type="avatar"
          width="180rpx"
          height="180rpx"
          tip="支持 jpg、png 格式"
          use-public-api
        />
      </view>
      <view class="demo-block">
        <view class="demo-title">
          多图上传
        </view>
        <ImageUpload v-model="gallery" multiple :limit="4" biz-type="album" use-public-api />
      </view>
      <view class="state-card">
        <view class="state-item">
          <text class="state-label">单图地址</text>
          <text class="state-value">{{ avatar || '未选择' }}</text>
        </view>
        <view class="state-item">
          <text class="state-label">图库数量</text>
          <text class="state-value">{{ galleryCount }} 张</text>
        </view>
      </view>
      <view class="code-block">
        <text class="code-line">ImageUpload v-model="avatar" biz-type="avatar"</text>
        <text class="code-line">ImageUpload v-model="gallery" multiple :limit="4"</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          ProfileBasic
        </view>
        <view class="section-tag">
          个人信息
        </view>
      </view>
      <view class="section-desc">
        复用头像与昵称编辑区块，适用于资料完善与资料修改。
      </view>
      <view class="demo-block">
        <ProfileBasic
          v-model:avatar="basicAvatar"
          v-model:nickname="basicNickname"
          :show-title="false"
          nickname-placeholder="请输入昵称"
          use-public-api
        />
      </view>
      <view class="state-card">
        <view class="state-item">
          <text class="state-label">头像地址</text>
          <text class="state-value">{{ basicAvatar || '未选择' }}</text>
        </view>
        <view class="state-item">
          <text class="state-label">昵称</text>
          <text class="state-value">{{ basicNicknameStatus }}</text>
        </view>
      </view>
      <view class="code-block">
        <text class="code-line">ProfileBasic v-model:avatar="avatar"</text>
        <text class="code-line">ProfileBasic v-model:nickname="nickname"</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          FilePicker
        </view>
        <view class="section-tag">
          文件上传
        </view>
      </view>
      <view class="section-desc">
        支持附件多选上传，适用于合同、资料上传场景。
      </view>
      <view class="demo-block">
        <FilePicker v-model="attachments" multiple :limit="3" biz-type="demo" use-public-api />
      </view>
      <view class="state-card">
        <view class="state-item">
          <text class="state-label">附件数量</text>
          <text class="state-value">{{ attachmentCount }} 个</text>
        </view>
      </view>
      <view class="code-block">
        <text class="code-line">FilePicker v-model="attachments" multiple :limit="3"</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          SignatureBoard
        </view>
        <view class="section-tag">
          签名板（自动上传）
        </view>
      </view>
      <view class="section-desc">
        基于 wot-design-uni 签名组件封装，签名完成后自动上传到 S3，返回图片 URL。
      </view>
      <view class="demo-block">
        <view class="demo-title">
          普通模式
        </view>
        <SignatureBoard v-model="signatureBoard" use-public-api />
      </view>
      <view class="demo-block">
        <view class="demo-title">
          横屏模式
        </view>
        <SignatureBoard v-model="signatureBoardLandscape" landscape use-public-api />
      </view>
      <view class="state-card">
        <view class="state-item">
          <text class="state-label">普通模式</text>
          <text class="state-value">{{ signatureBoardStatus }}</text>
        </view>
        <view class="state-item">
          <text class="state-label">普通模式URL</text>
          <text class="state-value">{{ signatureBoard || '-' }}</text>
        </view>
        <view class="state-item">
          <text class="state-label">横屏模式</text>
          <text class="state-value">{{ signatureBoardLandscapeStatus }}</text>
        </view>
        <view class="state-item">
          <text class="state-label">横屏模式URL</text>
          <text class="state-value">{{ signatureBoardLandscape || '-' }}</text>
        </view>
      </view>
      <view class="code-block">
        <text class="code-line">SignatureBoard v-model="signatureUrl"</text>
        <text class="code-line">SignatureBoard v-model="signatureUrl" landscape</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          AiChat
        </view>
        <view class="section-tag">
          AI 对话
        </view>
      </view>
      <view class="section-desc">
        简单的AI对话演示组件，支持流式输出效果和快捷问题。
      </view>
      <view class="demo-block">
        <AiChat
          ref="aiChatRef"
          height="600rpx"
          placeholder="输入消息，与AI对话..."
          @message="onAiMessage"
        />
      </view>
      <view class="state-card">
        <view class="state-item">
          <text class="state-label">消息数量</text>
          <text class="state-value">{{ aiMessageCount }} 条</text>
        </view>
        <view class="state-item">
          <text class="state-label">操作</text>
          <text class="state-value state-action" @click="clearAiChat">清空对话</text>
        </view>
      </view>
      <view class="code-block">
        <text class="code-line">AiChat height="600rpx" @message="onMessage"</text>
        <text class="code-line">AiChat :ai-avatar="avatar" :user-avatar="avatar"</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          Uni ECharts
        </view>
        <view class="section-tag">
          图表组件
        </view>
      </view>
      <view class="section-desc">
        跨端可用的 ECharts 渲染能力，示例覆盖折线、柱状与饼图。
      </view>
      <view class="demo-block">
        <view class="demo-title">
          走势折线图
        </view>
        <uni-echarts custom-class="chart" :option="lineOption" />
      </view>
      <view class="demo-block">
        <view class="demo-title">
          分类柱状图
        </view>
        <uni-echarts custom-class="chart" :option="barOption" />
      </view>
      <view class="demo-block">
        <view class="demo-title">
          占比饼图
        </view>
        <uni-echarts custom-class="chart chart--pie" :option="pieOption" />
      </view>
      <view class="code-block">
        <text class="code-line">uni-echarts :option="lineOption"</text>
        <text class="code-line">uni-echarts :option="barOption"</text>
        <text class="code-line">uni-echarts :option="pieOption"</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          DeptSelector
        </view>
        <view class="section-tag">
          组织选择
        </view>
      </view>
      <view class="section-desc">
        支持部门树选择，返回选中部门 ID。
      </view>
      <view class="demo-block">
        <DeptSelector v-model="deptId" use-public-api />
      </view>
      <view class="state-card">
        <view class="state-item">
          <text class="state-label">当前部门</text>
          <text class="state-value">{{ deptStatus }}</text>
        </view>
      </view>
      <view class="code-block">
        <text class="code-line">DeptSelector v-model="deptId"</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          CmsText
        </view>
        <view class="section-tag">
          动态文本
        </view>
      </view>
      <view class="section-desc">
        用于展示可后台配置的内容，如关于我们、帮助说明等。
      </view>
      <view class="demo-block">
        <CmsText :text-key="TextKey.ABOUT_US" :show-title="true" :show-update-time="false" />
      </view>
      <view class="code-block">
        <text class="code-line">CmsText :text-key="TextKey.ABOUT_US"</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view class="section-title">
          AccessControl
        </view>
        <view class="section-tag">
          权限控制
        </view>
      </view>
      <view class="section-desc">
        根据角色展示内容，并提供 fallback 插槽。
      </view>
      <view class="demo-block">
        <AccessControl :roles="['admin']">
          <view class="access-card success">
            管理员可见内容
          </view>
          <template #fallback>
            <view class="access-card">
              当前账号无权限
            </view>
          </template>
        </AccessControl>
      </view>
      <view class="code-block">
        <text class="code-line">AccessControl :roles="['admin']"</text>
      </view>
    </view>

    <view class="safe-bottom" />
  </view>
</template>

<style lang="scss" scoped>
$primary: #0ea5e9;
$primary-light: #38bdf8;
$bg-page: #f8fafc;
$text-primary: #1e293b;
$text-secondary: #64748b;
$text-placeholder: #94a3b8;
$border: #e2e8f0;

.components-page {
  min-height: 100vh;
  background: $bg-page;
  padding: 28rpx 24rpx 32rpx;
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.hero {
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  border-radius: 28rpx;
  padding: 32rpx;
  color: #ffffff;
  box-shadow: 0 18rpx 40rpx rgba(14, 165, 233, 0.25);
}

.hero-title {
  font-size: 38rpx;
  font-weight: 700;
  margin-bottom: 10rpx;
}

.hero-desc {
  font-size: 26rpx;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.9);
}

.section {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 28rpx;
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, 0.06);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.section-tag {
  font-size: 22rpx;
  color: $primary;
  background: rgba(14, 165, 233, 0.12);
  padding: 6rpx 16rpx;
  border-radius: 999rpx;
}

.section-desc {
  font-size: 24rpx;
  color: $text-secondary;
}

.demo-block {
  background: #f8fafc;
  border-radius: 20rpx;
  border: 2rpx dashed rgba(148, 163, 184, 0.35);
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.demo-title {
  font-size: 24rpx;
  color: $text-secondary;
}

.state-card {
  background: #f1f5f9;
  border-radius: 16rpx;
  padding: 16rpx 20rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.state-item {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
}

.state-label {
  color: $text-secondary;
}

.state-value {
  color: $text-primary;
  max-width: 60%;
  text-align: right;
  word-break: break-all;

  &.state-action {
    color: $primary;
    cursor: pointer;

    &:active {
      opacity: 0.7;
    }
  }
}

.code-block {
  background: #0f172a;
  border-radius: 16rpx;
  padding: 16rpx 20rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.code-line {
  font-family: 'Courier New', monospace;
  font-size: 22rpx;
  color: #e2e8f0;
  line-height: 1.5;
}

.chart {
  width: 100%;
  height: 360rpx;
}

.chart--pie {
  height: 400rpx;
}

.access-card {
  padding: 20rpx;
  border-radius: 16rpx;
  background: #f1f5f9;
  border: 1px solid $border;
  font-size: 26rpx;
  color: $text-secondary;

  &.success {
    background: #ecfeff;
    color: #0e7490;
    border-color: #bae6fd;
  }
}
</style>
