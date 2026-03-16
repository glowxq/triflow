# App 组件开发规范详解

## 1. Wot Design Uni 组件使用

项目已配置自动导入，直接在模板中使用 `wd-xxx` 组件。

### 常用组件速查

| 组件 | 用途 | 文档 |
|------|------|------|
| `wd-button` | 按钮 | [按钮](https://wot-ui.cn/component/button.html) |
| `wd-input` | 输入框 | [输入框](https://wot-ui.cn/component/input.html) |
| `wd-picker` | 选择器 | [选择器](https://wot-ui.cn/component/picker.html) |
| `wd-cell` | 单元格 | [单元格](https://wot-ui.cn/component/cell.html) |
| `wd-toast` | 轻提示 | [轻提示](https://wot-ui.cn/component/toast.html) |
| `wd-message-box` | 弹框 | [弹框](https://wot-ui.cn/component/message-box.html) |
| `wd-popup` | 弹出层 | [弹出层](https://wot-ui.cn/component/popup.html) |
| `wd-form` | 表单 | [表单](https://wot-ui.cn/component/form.html) |
| `wd-tabs` | 标签页 | [标签页](https://wot-ui.cn/component/tabs.html) |
| `wd-swiper` | 轮播图 | [轮播图](https://wot-ui.cn/component/swiper.html) |

### 使用示例

```vue
<template>
  <!-- 按钮 -->
  <wd-button type="primary" @click="handleClick">主要按钮</wd-button>
  <wd-button type="success">成功按钮</wd-button>
  <wd-button type="error" block>危险操作</wd-button>

  <!-- 表单 -->
  <wd-input v-model="username" label="用户名" placeholder="请输入用户名" />
  <wd-input v-model="password" label="密码" show-password />
  <wd-picker v-model="city" label="城市" :columns="cityColumns" />

  <!-- 反馈 -->
  <wd-toast />
  <wd-message-box />

  <!-- 展示 -->
  <wd-cell title="设置" is-link @click="goSettings" />
  <wd-cell-group title="用户信息">
    <wd-cell title="头像" />
    <wd-cell title="昵称" :value="nickname" />
  </wd-cell-group>
</template>
```

### 图标使用

```vue
<wd-icon name="home" size="24px" color="#333" />
<wd-button icon="search">搜索</wd-button>
```

图标列表：[https://wot-ui.cn/component/icon.html](https://wot-ui.cn/component/icon.html)

### 主题定制

```scss
// style/variables.scss
page {
  --wot-color-theme: #1890ff;
  --wot-color-success: #52c41a;
  --wot-color-warning: #faad14;
  --wot-color-danger: #ff4d4f;
}
```

---

## 2. Uni ECharts 图表

### Vite 配置（已集成）

- `UniEcharts()` 插件已启用
- `UniComponents` 已加入 `UniEchartsResolver()`
- `optimizeDeps.exclude` 已排除 `uni-echarts`

### 使用示例

```vue
<template>
  <uni-echarts custom-class="chart" :option="option" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { DatasetComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { PieChart } from 'echarts/charts'
import { ref } from 'vue'
import { provideEcharts } from 'uni-echarts/shared'

provideEcharts(echarts)
echarts.use([DatasetComponent, LegendComponent, TooltipComponent, PieChart, CanvasRenderer])

const option = ref({
  tooltip: { trigger: 'item' },
  series: [{ type: 'pie' }],
  dataset: {
    dimensions: ['name', 'value'],
    source: [['A', 120], ['B', 200]],
  },
})
</script>

<style>
.chart { height: 300px; }
</style>
```

### 常见问题

- **PNPM 依赖提升**：确保 `.npmrc` 中 `shamefully-hoist=true`
- **小程序 class/style 无效**：使用 `custom-class` / `custom-style`
- **弹窗内渲染失败**：在 `wd-popup` 的 `after-enter` 后再渲染
- **不支持**：SVG 渲染器、`toolbox.feature.saveAsImage`、`series-map`

---

## 3. 已封装公共组件

### 3.1 ImageUpload 图片上传

**位置**：`src/components/image-upload/image-upload.vue`

```vue
<!-- 单图上传（头像） -->
<ImageUpload
  v-model="avatar"
  biz-type="avatar"
  width="180rpx"
  height="180rpx"
  tip="支持 jpg、png 格式"
/>

<!-- 多图上传 -->
<ImageUpload
  v-model="images"
  multiple
  :limit="5"
  biz-type="article"
  tip="最多上传5张图片"
/>
```

**Props**：

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `v-model` | `string \| string[] \| null` | - | 图片 URL |
| `multiple` | `boolean` | `false` | 多图模式 |
| `limit` | `number` | `9` | 最大数量 |
| `maxSize` | `number` | `10` | 最大文件大小（MB） |
| `bizType` | `string` | `'image'` | 业务类型 |
| `width` / `height` | `string` | `'160rpx'` | 尺寸 |
| `tip` | `string` | `''` | 提示文字 |

### 3.2 CmsText 文本展示

**位置**：`src/components/cms-text/cms-text.vue`

```vue
<CmsText
  :text-key="TextKey.USER_AGREEMENT"
  :show-title="true"
  :show-update-time="true"
/>
```

**常用 TextKey**：`USER_AGREEMENT`、`PRIVACY_POLICY`、`ABOUT_US`、`CONTACT_US`

### 3.3 AccessControl 权限控制

**位置**：`src/components/access-control/access-control.vue`

根据用户权限控制元素显示/隐藏。

---

## 4. 系统开关模块

### API 封装

```typescript
// api/switch.ts
import { SwitchKey } from './types/switch'

// 获取单个开关（带 5 分钟缓存）
export async function getSwitchStatus(switchKey: SwitchKey): Promise<boolean>

// 批量获取开关
export async function getBatchSwitchStatus(switchKeys: SwitchKey[]): Promise<Record<string, boolean>>

// 清除缓存
export function clearSwitchCache(switchKey?: SwitchKey): void
```

### 使用示例

```vue
<script setup lang="ts">
import { getSwitchStatus, getBatchSwitchStatus, SwitchKey } from '@/api/switch'

const showRegister = ref(false)

onMounted(async () => {
  showRegister.value = await getSwitchStatus(SwitchKey.USER_REGISTER)

  const switches = await getBatchSwitchStatus([
    SwitchKey.USER_LOGIN_PHONE,
    SwitchKey.USER_LOGIN_SOCIAL,
  ])
})
</script>
```

### 常用 SwitchKey

| 枚举值 | 开关键 | 说明 |
|--------|--------|------|
| `USER_REGISTER` | `user.register.enabled` | 用户注册 |
| `USER_LOGIN_PHONE` | `user.login.phone.enabled` | 手机号登录 |
| `SECURITY_CAPTCHA` | `security.captcha.enabled` | 验证码 |
| `SYSTEM_MAINTENANCE` | `system.maintenance.enabled` | 系统维护模式 |
| `NOTIFY_SMS` | `notify.sms.enabled` | 短信通知 |

---

## 5. 分页列表（z-paging）

### 完整模板

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { getProductList } from '@/api/product'

const pagingRef = ref()
const list = ref<ProductVO[]>([])

async function queryList(pageNum: number, pageSize: number) {
  const res = await getProductList({ pageNum, pageSize })
  pagingRef.value?.complete(res.list)
}

function refresh() {
  pagingRef.value?.reload()
}
</script>

<template>
  <z-paging
    ref="pagingRef"
    v-model="list"
    :refresher-enabled="true"
    :default-page-size="10"
    @query="queryList"
  >
    <view v-for="item in list" :key="item.id">
      <ProductCard :product="item" />
    </view>
    <template #empty>
      <view class="empty">
        <image src="@/static/images/empty.png" />
        <text>暂无数据</text>
      </view>
    </template>
  </z-paging>
</template>
```

---

## 6. 骨架屏

```vue
<template>
  <view v-if="loading" class="skeleton">
    <view class="skeleton-image" />
    <view class="skeleton-text" />
    <view class="skeleton-text short" />
  </view>
  <view v-else>
    <!-- 实际内容 -->
  </view>
</template>

<style lang="scss" scoped>
.skeleton {
  &-image {
    width: 100%;
    height: 300rpx;
    background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    animation: skeleton-loading 1.5s infinite;
  }
  &-text {
    height: 32rpx;
    margin-top: 16rpx;
    background: #f0f0f0;
    border-radius: 4rpx;
    &.short { width: 60%; }
  }
}
@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
```
