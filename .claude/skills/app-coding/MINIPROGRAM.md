# 微信小程序兼容性指南

> 以下是开发过程中发现的微信小程序兼容性问题，编写代码时务必注意。

## 1. 条件渲染：v-if vs v-show

**问题**：`v-show` 在微信小程序中行为可能与 H5 不一致。

```vue
<!-- ❌ 小程序中可能有问题 -->
<view v-show="activeTab === 'password'" class="form">
  <!-- 表单内容 -->
</view>

<!-- ✅ 推荐：使用 v-if -->
<view v-if="activeTab === 'password'" class="form">
  <!-- 表单内容 -->
</view>
```

**原则**：Tab 切换、条件显示的内容块，**优先使用 `v-if`**。只有频繁切换且内容简单时才用 `v-show`。

---

## 2. 不支持的 CSS 属性

| CSS 属性 | 支持情况 | 替代方案 |
|---------|---------|---------|
| `backdrop-filter` | ❌ 不支持 | 纯色半透明背景 `rgba()` |
| `filter: blur()` | ⚠️ 部分支持 | 图片模糊处理 |
| `position: sticky` | ⚠️ 部分支持 | `position: fixed` + JS 监听 |
| `gap` (flex/grid) | ❌ 不支持 | `margin` 间距 |
| `aspect-ratio` | ❌ 不支持 | `padding-top` 百分比 hack |
| `-webkit-line-clamp > 10` | ⚠️ 有限制 | 控制在 10 行以内 |

### 毛玻璃效果替代

```scss
// ❌ 小程序不支持
.header { backdrop-filter: blur(10px); background: rgba(255, 255, 255, 0.8); }

// ✅ 使用纯色半透明
.header { background: rgba(255, 255, 255, 0.95); }
```

---

## 3. 渐变背景限制

```scss
// ❌ 避免复杂多层渐变
.page {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%),
              linear-gradient(to bottom, rgba(0,0,0,0.1), transparent);
}

// ✅ 简单单层渐变或纯色
.page { background: linear-gradient(135deg, #0ea5e9 0%, #38bdf8 100%); }
// ✅✅ 更推荐纯色
.page { background-color: #ffffff; }
```

---

## 4. 字体与文字

- **不支持** `@font-face` 加载自定义字体文件
- 部分字重（font-weight）可能不生效

```scss
// ✅ 使用系统字体栈
.text { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }

// ✅ 字重只使用常用值
.text-bold { font-weight: 600; }  // 避免 500、800
```

---

## 5. 安全区域适配

```scss
// 底部安全区域（iPhone X+）
.footer { padding-bottom: calc(24rpx + env(safe-area-inset-bottom, 20px)); }
.safe-bottom { height: calc(32rpx + env(safe-area-inset-bottom, 20px)); }
```

```typescript
// 顶部状态栏高度
const systemInfo = uni.getSystemInfoSync()
const statusBarHeight = systemInfo.statusBarHeight || 20
```

---

## 6. 按钮样式重置

小程序原生 `<button>` 有默认样式，需要完全重置：

```scss
button {
  margin: 0;
  padding: 0;
  background: transparent;
  border: none;
  line-height: normal;
  &::after { border: none; }
}
```

---

## 7. 微信能力

```vue
<!-- 获取微信头像 - 仅小程序 -->
<!-- #ifdef MP-WEIXIN -->
<button open-type="chooseAvatar" @chooseavatar="handleChooseAvatar">选择头像</button>
<!-- #endif -->

<!-- 获取微信昵称 -->
<!-- #ifdef MP-WEIXIN -->
<input type="nickname" placeholder="点击获取微信昵称" @blur="handleGetNickname" />
<!-- #endif -->
```

---

## 8. 图片处理

- 网络图片需要在后台配置合法域名
- 始终添加 `@error` 处理和 `lazy-load`

```vue
<image :src="imageUrl" mode="aspectFill" lazy-load @error="handleImageError" />
```

---

## 9. scroll-view

`scroll-view` 需要明确指定高度才能滚动：

```vue
<scroll-view
  scroll-y
  :style="{ height: `calc(100vh - ${headerHeight}px)` }"
>
  <!-- 内容 -->
</scroll-view>
```

---

## 10. Wot Design Uni 注意事项

| 问题 | 解决方案 |
|------|---------|
| Toast 不显示 | 页面中需有 `<wd-toast />` 组件 |
| 弹窗层级问题 | 调整 `z-index` 或使用 `custom-style` |
| 表单验证失效 | 确保 `wd-form` 和 `wd-form-item` 正确嵌套 |
| 图标不显示 | 检查图标名称，参考官方图标列表 |

参考：[Wot Design Uni FAQ](https://wot-ui.cn/guide/faq.html)

---

## 11. 条件编译

平台差异代码**必须**使用条件编译：

```vue
<template>
  <!-- #ifdef MP-WEIXIN -->
  <button @click="wxLogin">微信登录</button>
  <!-- #endif -->
  <!-- #ifdef H5 -->
  <button @click="h5Login">账号登录</button>
  <!-- #endif -->
  <!-- #ifndef MP-WEIXIN -->
  <view>非微信小程序显示</view>
  <!-- #endif -->
</template>

<script setup lang="ts">
// #ifdef MP-WEIXIN
function wxLogin() {
  uni.login({
    provider: 'weixin',
    success(res: UniApp.LoginRes) { /* 使用 res.code 调用后端登录接口 */ },
  })
}
// #endif
// #ifdef H5
function h5Login() { /* H5 逻辑 */ }
// #endif
</script>

<style lang="scss" scoped>
/* #ifdef H5 */
.container { max-width: 750px; margin: 0 auto; }
/* #endif */
</style>
```

> **注意**：`open-type="getUserInfo"` 已被微信废弃（2021年4月后），应使用 `wx.login()` 获取 code 后调用后端接口。

---

## 12. 分包加载

```typescript
// pages.config.ts
export default defineUniPages({
  pages: [
    { path: 'pages/index/index' },
    { path: 'pages/mine/index' },
  ],
  subPackages: [
    {
      root: 'pages/order',
      pages: [
        { path: 'list/index' },
        { path: 'detail/index' },
      ],
    },
  ],
})
```

---

## 13. 平台差异对照

| 功能 | 微信小程序 | H5 | App |
|------|-----------|-----|-----|
| 登录 | `wx.login()` | 手机号登录 | 手机号/微信登录 |
| 支付 | `wx.requestPayment()` | 跳转支付页 | 原生支付 |
| 分享 | `button[open-type="share"]` | 自定义分享 | 原生分享 |
| 扫码 | `wx.scanCode()` | 调用摄像头 | 原生扫码 |

---

## 14. 调试技巧

```typescript
// 条件打印日志
if (import.meta.env.DEV) {
  console.log('调试信息', data)
}

// 临时调试
uni.showModal({ title: '调试', content: JSON.stringify(data) })
```

### 发布检查清单

- [ ] 移除所有 `console.log`
- [ ] 检查环境变量和接口地址
- [ ] 检查小程序 AppID
- [ ] 运行 `pnpm type-check` 和 `pnpm lint`
- [ ] H5 和微信小程序两端测试通过
