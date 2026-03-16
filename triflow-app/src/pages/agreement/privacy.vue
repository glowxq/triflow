/**
 * 隐私政策页面
 * @description 使用 CmsText 组件展示隐私政策
 */

<script lang="ts" setup>
import { TextKey } from '@/api/types/cms'
import CmsText from '@/components/cms-text/cms-text.vue'

defineOptions({
  name: 'PrivacyPolicy',
})

definePage({
  style: {
    navigationBarTitleText: '隐私政策',
  },
})
</script>

<template>
  <view class="agreement-page">
    <CmsText
      :text-key="TextKey.PRIVACY_POLICY"
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
