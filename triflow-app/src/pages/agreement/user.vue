/**
 * 用户协议页面
 * @description 使用 CmsText 组件展示用户服务协议
 */

<script lang="ts" setup>
import { TextKey } from '@/api/types/cms'
import CmsText from '@/components/cms-text/cms-text.vue'

defineOptions({
  name: 'UserAgreement',
})

definePage({
  style: {
    navigationBarTitleText: '用户协议',
  },
})
</script>

<template>
  <view class="agreement-page">
    <CmsText
      :text-key="TextKey.USER_AGREEMENT"
      :show-title="true"
      :show-update-time="true"
    />
  </view>
</template>

<style lang="scss" scoped>
.agreement-page {
  min-height: 100vh;
  background: #ffffff;
}
</style>
