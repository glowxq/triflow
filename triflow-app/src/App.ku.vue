<script setup lang="ts">
import type { CustomTabBarItem } from '@/tabbar/types'
import { ref } from 'vue'
import { getWechatTabbarList } from '@/api/wechat'
import FgTabbar from '@/tabbar/index.vue'
import { isPageTabbar, resetTabbarList, tabbarStore } from './tabbar/store'
import { currRoute } from './utils'

const isCurrentPageTabbar = ref(true)
onShow(() => {
  console.log('App.ku.vue onShow', currRoute())
  const { path } = currRoute()
  // “蜡笔小开心”提到本地是 '/pages/index/index'，线上是 '/' 导致线上 tabbar 不见了
  // 所以这里需要判断一下，如果是 '/' 就当做首页，也要显示 tabbar
  if (path === '/') {
    isCurrentPageTabbar.value = true
  }
  else {
    isCurrentPageTabbar.value = isPageTabbar(path)
  }

  refreshWechatTabbar()
})

async function refreshWechatTabbar() {
  // #ifdef MP-WEIXIN
  try {
    const list = await getWechatTabbarList()
    if (list?.length) {
      const items: CustomTabBarItem[] = list.map((item) => {
        let badge: CustomTabBarItem['badge']
        if (item.badge === 'dot') {
          badge = 'dot'
        }
        else if (item.badge && !Number.isNaN(Number(item.badge))) {
          badge = Number(item.badge)
        }
        return {
          text: item.text,
          pagePath: item.pagePath,
          iconType: item.iconType as CustomTabBarItem['iconType'],
          icon: item.icon,
          iconActive: item.iconActive,
          badge,
          isBulge: item.isBulge === 1,
        }
      })
      resetTabbarList(items)
      tabbarStore.setAutoCurIdx(currRoute().path)
    }
  }
  catch (error) {
    console.warn('获取微信底部菜单失败:', error)
  }
  // #endif
}

const helloKuRoot = ref('Hello AppKuVue')

const exposeRef = ref('this is form app.Ku.vue')

defineExpose({
  exposeRef,
})
</script>

<template>
  <view>
    <!-- 这个先隐藏了，知道这样用就行 -->
    <view class="hidden text-center">
      {{ helloKuRoot }}，这里可以配置全局的东西
    </view>

    <KuRootView />

    <FgTabbar v-if="isCurrentPageTabbar" />
  </view>
</template>
