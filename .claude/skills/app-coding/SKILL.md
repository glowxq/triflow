---
name: app-coding
description: Triflow App 移动端开发规范。包括 UniApp + Wot Design Uni + Alova 请求、API 路径前缀、z-paging 分页、UnoCSS 样式、跨端兼容性等项目约定。在编写或修改 triflow-app 代码时必须遵循。
---

# Triflow App 移动端开发规范

> 本 Skill 定义了 Triflow App（移动端）的代码编写规范，所有代码变更必须遵守。

## 1. 技术栈

| 技术 | 版本 | 用途 | 文档 |
|------|------|------|------|
| Unibest | 4.3.x | 项目模板 | [unibest.tech](https://unibest.tech/) |
| Wot Design Uni | 1.14.x | UI 组件库 | [wot-ui.cn](https://wot-ui.cn/) |
| UniApp | 3.0.x (Vite) | 跨端框架 | [uniapp.dcloud.net.cn](https://uniapp.dcloud.net.cn/) |
| Vue | 3.4+ | UI 框架 | - |
| TypeScript | 5.8+ | 类型系统 | - |
| Pinia | 2.0.x | 状态管理 | - |
| UnoCSS | 66.x | 原子化 CSS | [unocss.dev](https://unocss.dev/) |
| Alova | 3.x | 请求库 | [alova.js.org](https://alova.js.org/) |
| z-paging | 2.8.x | 分页组件 | [z-paging.zxlee.cn](https://z-paging.zxlee.cn/) |
| pnpm | 10.0.0+ | 包管理器 | - |

### 开发原则

1. **优先使用 Wot Design 组件** - 表单、弹窗、按钮等 UI 元素应优先使用 `wd-xxx` 组件
2. **遵循 Unibest 约定** - 文件路由、布局系统、请求封装按 Unibest 规范
3. **跨端兼容性** - 始终考虑微信小程序、H5、App 的差异
4. **查阅官方文档** - 遇到问题先查阅技术栈官方文档

---

## 2. 项目结构

```
triflow-app/src/
├── api/             # API 接口定义
├── components/      # 公共组件
├── hooks/           # 组合式函数
├── http/            # Alova 请求配置
├── layouts/         # 布局组件
├── pages/           # 页面（文件路由）
├── service/         # 业务服务
├── static/          # 静态资源
├── store/           # Pinia 状态
├── style/           # 全局样式
├── tabbar/          # TabBar 页面
├── types/           # 类型定义
└── utils/           # 工具函数
```

### 页面文件结构（文件路由）

```
pages/
├── index/index.vue              # 首页
├── user/
│   ├── profile/index.vue        # /pages/user/profile/index
│   ├── settings/index.vue
│   └── components/              # 模块私有组件（不生成路由）
└── order/
    ├── list/index.vue
    └── detail/index.vue
```

TabBar 页面放在 `tabbar/` 目录下。

---

## 3. API 路径前缀规范（重要）

### 环境变量

```bash
# env/.env.development
VITE_SERVER_BASEURL=http://127.0.0.1:7100
```

> 注意：环境变量是 `VITE_SERVER_BASEURL`，**不包含**模块前缀。

### API 定义必须包含 `/base/` 前缀

```typescript
// ✅ 正确 - 显式包含 /base 前缀
export function login(data: LoginDTO) {
  return http.post<LoginVO>('/base/auth/login', data)
  // 实际请求: http://127.0.0.1:7100/base/auth/login
}

export function sendSmsCode(phone: string, type: string) {
  return http.post<void>('/base/public/sms/send', { phone, type })
}

// ❌ 错误 - 缺少模块前缀
export function login(data: LoginDTO) {
  return http.post<LoginVO>('/auth/login', data)  // ❌ 会请求 /auth/login
}
```

### 常用接口路径对照

| 功能 | API 路径 |
|------|----------|
| 登录 | `/base/auth/login` |
| 注册 | `/base/auth/register` |
| 用户信息 | `/base/auth/user/info` |
| 发送短信 | `/base/public/sms/send` |
| 验证码 | `/base/public/captcha/image` |
| CMS 文本 | `/base/public/text/{key}` |
| 开关状态 | `/base/public/switch/{key}` |

---

## 4. 组件规范

### 组件模板

```vue
<script setup lang="ts">
// ==================== Props ====================
interface Props {
  product: ProductVO
  showPrice?: boolean
  mode?: 'card' | 'list'
}
const props = withDefaults(defineProps<Props>(), {
  showPrice: true,
  mode: 'card',
})

// ==================== Emits ====================
const emit = defineEmits<{
  (e: 'click', product: ProductVO): void
}>()

// ==================== Methods ====================
function handleClick() {
  emit('click', props.product)
}
</script>

<template>
  <view class="product-card" :class="[`product-card--${mode}`]" @click="handleClick">
    <image class="product-card__image" :src="product.image" mode="aspectFill" lazy-load />
    <view class="product-card__info">
      <text class="product-card__title">{{ product.name }}</text>
      <text v-if="showPrice" class="product-card__price">{{ product.price }}</text>
    </view>
  </view>
</template>
```

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件文件 | `kebab-case.vue` | `product-card.vue` |
| 页面目录 | `kebab-case` | `user-center/` |
| 变量/函数 | `camelCase` | `handleClick` |
| 常量 | `UPPER_SNAKE_CASE` | `MAX_COUNT` |
| 类型/接口 | `PascalCase` | `ProductVO` |

### Wot Design Uni 组件

已配置自动导入，直接在模板中使用 `wd-xxx`：

```vue
<wd-button type="primary" @click="handleClick">主要按钮</wd-button>
<wd-input v-model="username" label="用户名" placeholder="请输入" />
<wd-cell title="设置" is-link @click="goSettings" />
<wd-toast />
```

常用组件：`wd-button`、`wd-input`、`wd-picker`、`wd-cell`、`wd-toast`、`wd-message-box`、`wd-popup`、`wd-form`、`wd-tabs`、`wd-swiper`

> 详细组件使用、已封装公共组件列表见 [COMPONENTS.md](./COMPONENTS.md)

---

## 5. 请求规范（Alova）

### 请求配置

```typescript
// http/index.ts
export const alovaInstance = createAlova({
  baseURL: import.meta.env.VITE_SERVER_BASEURL,
  ...AdapterUniapp(),
  timeout: 30000,
  beforeRequest(method) {
    const userStore = useUserStore()
    if (userStore.token) {
      method.config.headers.Authorization = `Bearer ${userStore.token}`
    }
  },
  responded: {
    onSuccess: async (response) => {
      const data = response.data
      if (data.code !== 0) {
        uni.showToast({ title: data.message || '请求失败', icon: 'none' })
        throw new Error(data.message)
      }
      return data.data
    },
  },
})
```

### API 定义

```typescript
// api/user.ts
import { alovaInstance } from '@/http'

export interface UserVO {
  id: number
  nickname: string
  avatar: string
  phone: string
}

export function login(data: LoginDTO) {
  return alovaInstance.Post<LoginVO>('/base/auth/login/sms', data)  // ✅ 包含 /base/
}

export function getUserInfo() {
  return alovaInstance.Get<UserVO>('/base/auth/user/info')  // ✅ 包含 /base/
}
```

### 请求使用

```vue
<script setup lang="ts">
import { useRequest } from 'alova/client'
import { getUserInfo } from '@/api/user'

// 自动请求
const { data: userInfo, loading } = useRequest(getUserInfo)

// 手动请求
const { send: fetchUser } = useRequest(getUserInfo, { immediate: false })
</script>
```

---

## 6. 状态管理规范

```typescript
export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref<string>('')
    const userInfo = ref<UserVO | null>(null)
    const isLoggedIn = computed(() => !!token.value)

    function setToken(newToken: string) { token.value = newToken }
    function logout() { token.value = ''; userInfo.value = null }

    return { token, userInfo, isLoggedIn, setToken, logout }
  },
  {
    persist: {
      storage: { getItem: uni.getStorageSync, setItem: uni.setStorageSync },
    },
  },
)
```

使用时：`storeToRefs` 解构响应式数据，actions 可直接解构。

---

## 7. 分页列表规范（z-paging）

使用 `z-paging` 组件实现下拉刷新 + 上拉加载分页列表。核心模式：`@query` 回调中请求数据，通过 `pagingRef.value?.complete(list)` 通知组件。

> 完整 z-paging 模板和配置示例见 [COMPONENTS.md](./COMPONENTS.md) §5

---

## 8. 样式规范

### UnoCSS 原子类优先

```vue
<view class="flex items-center justify-between p-4 bg-white rounded-lg">
  <text class="text-lg font-bold text-gray-800">标题</text>
</view>
```

### 尺寸单位

| 单位 | 使用场景 |
|------|----------|
| `rpx` | 推荐，自动适配不同屏幕 |
| `px` | 边框、小图标 |
| `%` | 弹性布局 |

### 自定义样式用 SCSS + BEM

```scss
.product-card {
  &__image { width: 100%; height: 340rpx; }
  &__title { font-size: 28rpx; }
  &--list { display: flex; width: 100%; }
}
```

---

## 9. 页面配置

```vue
<!-- 推荐：route 块 -->
<route lang="json">
{
  "style": { "navigationBarTitleText": "用户中心" },
  "needLogin": true
}
</route>
```

---

## 10. 跨端兼容（重要）

### 核心规则

- **优先 `v-if`** 而非 `v-show`（小程序中 `v-show` 行为可能不一致）
- **条件编译**：平台差异代码必须使用 `#ifdef` / `#endif`
- 新功能**必须**在 H5 和微信小程序两端测试

```vue
<!-- #ifdef MP-WEIXIN -->
<button open-type="chooseAvatar" @chooseavatar="handleChooseAvatar">选择头像</button>
<!-- #endif -->
<!-- #ifdef H5 -->
<button @click="h5Upload">上传头像</button>
<!-- #endif -->
```

### 小程序不支持的 CSS

| CSS 属性 | 替代方案 |
|---------|---------|
| `backdrop-filter` | 纯色半透明 `rgba()` |
| `gap` (flex/grid) | `margin` 间距 |
| `aspect-ratio` | `padding-top` 百分比 |
| `@font-face` 自定义字体 | 系统字体栈 |

> 详细小程序兼容性问题和解决方案见 [MINIPROGRAM.md](./MINIPROGRAM.md)

---

## 11. 错误处理

### Token 过期

Alova 响应拦截器统一处理 `code === 401`，自动清除登录态并跳转登录页。

### 全局错误捕获

```typescript
app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误:', err)
  reportError(err)
}
```

---

## 12. 已封装公共组件

| 组件 | 位置 | 用途 |
|------|------|------|
| `ImageUpload` | `components/image-upload/` | 单图/多图上传，支持 OSS 直传 |
| `CmsText` | `components/cms-text/` | 根据 textKey 展示 CMS 文本内容 |
| `AccessControl` | `components/access-control/` | 根据权限控制元素显示/隐藏 |

> 组件详细 Props 和使用示例见 [COMPONENTS.md](./COMPONENTS.md)

---

## 13. 系统开关模块

调用后端 `/base/public/switch` 接口查询开关状态，使用 `SwitchKey` 枚举，带 5 分钟本地缓存。

规则：
- **禁止**自行实现开关逻辑，**必须**调用封装好的 `getSwitchStatus` / `getBatchSwitchStatus`
- 开关键**必须**使用 `SwitchKey` 枚举，**禁止**硬编码字符串
- 接口异常时默认返回 `false`

> 完整 API 签名、SwitchKey 枚举列表和使用示例见 [COMPONENTS.md](./COMPONENTS.md) §4
