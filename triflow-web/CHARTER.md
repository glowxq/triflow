# Triflow-Web 组件宪章

> 本文档记录了 triflow-web 项目中已封装的组件、Hooks 和工具函数，供后续开发参考，避免重复造轮子。

## 目录

- [Web-Admin 业务组件](#web-admin-业务组件)
- [Effects 公共组件](#effects-公共组件)
- [Composables / Hooks](#composables--hooks)
- [工具函数库](#工具函数库)
- [使用指南](#使用指南)

---

## Web-Admin 业务组件

**位置**: `apps/web-admin/src/components/`

### 1. 状态组件 (status/)

| 组件 | 用途 | Props |
| --- | --- | --- |
| `StatusTag.vue` | 表格中显示状态标签 | `status`, `enabledText`, `disabledText`, `size` |
| `StatusSelect.vue` | 表单中状态下拉选择 | `placeholder`, `clearable`, `disabled`, `width` |
| `StatusSwitch.vue` | 表单中状态开关 | `disabled`, `activeText`, `inactiveText`, `inlinePrompt` |

```vue
<!-- 使用示例 -->
<StatusTag :status="row.status" />
<StatusSelect v-model="form.status" />
<StatusSwitch v-model="form.status" />
```

### 2. 编辑器组件 (editor/)

| 组件                 | 用途                   | 依赖               |
| -------------------- | ---------------------- | ------------------ |
| `RichTextEditor.vue` | 富文本编辑             | @wangeditor/editor |
| `MarkdownEditor.vue` | Markdown 编辑          | md-editor-v3       |
| `ContentEditor.vue`  | 智能编辑器（自动切换） | -                  |
| `ContentPreview.vue` | 内容预览               | -                  |

**RichTextEditor Props**:

- `placeholder`: 占位符
- `readonly`: 只读模式
- `height`: 编辑器高度

**Expose Methods**:

- `getEditor()`, `getText()`, `getHtml()`, `setHtml()`, `clear()`, `focus()`

```vue
<RichTextEditor v-model="content" height="500px" />
<MarkdownEditor v-model="markdown" :previewMode="'both'" />
<ContentEditor v-model="content" :contentType="'html'" />
```

### 3. 图标选择器 (icon-select/)

| 组件              | 用途             | 特性                          |
| ----------------- | ---------------- | ----------------------------- |
| `icon-select.vue` | Iconify 图标选择 | 150+ Lucide + 18+ Carbon 图标 |

```vue
<IconSelect v-model="form.icon" placeholder="请选择图标" />
<!-- 值格式: "lucide:home" -->
```

### 4. 图标选择器封装 (icon-picker/)

| 组件 | 用途 | 特性 |
| --- | --- | --- |
| `icon-picker.vue` | Tabbar 图标选择 | 统一 IconSelect + 资源输入，适配 unocss/iconfont/uiLib/image |

```vue
<IconPicker v-model="form.icon" :icon-type="form.iconType" />
```

### 5. 枚举选择器 (enum-select/)

| 组件              | 用途             | 特性             |
| ----------------- | ---------------- | ---------------- |
| `enum-select.vue` | 后端枚举下拉选择 | 自动加载枚举选项 |

**Props**:

- `enumClass` (必需): 枚举类简称，如 `"StatusEnum"`
- `multiple`: 是否多选
- `filterable`: 是否可搜索

```vue
<EnumSelect v-model="form.type" enumClass="ConfigTypeEnum" />
```

### 6. 文件选择器 (file-select/)

| 组件              | 用途               | 特性               |
| ----------------- | ------------------ | ------------------ |
| `file-select.vue` | 从已上传文件中选择 | 支持分类筛选、预览 |

**Props**:

- `fileType`: 文件类型筛选
- `bizType`: 业务类型筛选
- `multiple`: 是否多选
- `valueType`: 返回值类型 (`'id'` | `'object'`)

```vue
<FileSelect v-model="form.fileId" :multiple="false" />
```

### 7. 图片上传 (image-upload/)

| 组件               | 用途         | 特性                 |
| ------------------ | ------------ | -------------------- |
| `image-upload.vue` | 图片上传组件 | 支持预览、进度、拖拽 |

**Props**:

- `multiple`: 单图/多图模式
- `limit`: 最大上传数量
- `maxSize`: 最大文件大小(MB)
- `bizType`, `bizId`: 业务标识
- `width`, `height`: 显示尺寸

```vue
<!-- 单图 -->
<ImageUpload v-model="form.avatar" />
<!-- 多图 -->
<ImageUpload v-model="form.images" :multiple="true" :limit="9" />
```

### 8. Monaco Editor (monaco-editor/)

| 组件        | 用途       | 特性               |
| ----------- | ---------- | ------------------ |
| `index.vue` | 代码编辑器 | VS Code 编辑器内核 |

```vue
<MonacoEditor v-model="code" language="json" />
```

---

## Effects 公共组件

**位置**: `packages/effects/`

### 布局组件 (layouts/)

```
layouts/src/
├── basic/              # 基础布局
│   ├── layout.vue      # 主布局容器
│   ├── header/         # 顶部导航
│   ├── sidebar/        # 侧边栏
│   ├── content/        # 内容区域
│   ├── footer/         # 底部
│   ├── menu/           # 菜单组件
│   └── tabbar/         # 标签页栏
├── authentication/     # 认证页面布局
├── iframe/             # IFrame 布局
└── widgets/            # 小部件
    ├── preferences/    # 偏好设置
    ├── user-dropdown/  # 用户下拉菜单
    ├── notification/   # 通知
    ├── lock-screen/    # 锁屏
    ├── global-search/  # 全局搜索
    ├── breadcrumb.vue  # 面包屑
    └── theme-toggle/   # 主题切换
```

### 公共 UI (common-ui/)

```
common-ui/src/ui/
├── authentication/     # 认证相关
│   ├── login.vue       # 登录页
│   ├── register.vue    # 注册页
│   ├── forget-password.vue
│   ├── code-login.vue  # 验证码登录
│   └── qrcode-login.vue # 二维码登录
├── dashboard/          # 仪表盘
│   ├── workbench/      # 工作台组件
│   └── analysis/       # 数据分析组件
├── profile/            # 个人资料
│   ├── profile.vue
│   ├── base-setting.vue
│   └── security-setting.vue
└── fallback/           # 错误页面
    ├── fallback.vue
    └── icons/          # 404/403/500 图标
```

### 插件组件 (plugins/)

| 组件                         | 用途              |
| ---------------------------- | ----------------- |
| `echarts/echarts-ui.vue`     | ECharts 图表封装  |
| `vxe-table/use-vxe-grid.vue` | VXE Grid 表格封装 |

---

## Composables / Hooks

### @core/composables

**位置**: `packages/@core/composables/src/`

| Hook | 用途 | 返回值 |
| --- | --- | --- |
| `useIsMobile()` | 检测移动设备 | `{ isMobile }` |
| `useNamespace(block)` | BEM 类名生成器 | `{ b, e, m, be, em, bm, bem, is }` |
| `useSortable(el, options)` | 拖拽排序 | `{ initializeSortable }` |
| `useScrollLock()` | 锁定页面滚动 | - |
| `useLayoutContentStyle()` | 内容区域样式 | `{ contentElement, overlayStyle }` |
| `useLayoutHeaderStyle()` | 头部样式管理 | `{ getLayoutHeaderHeight, setLayoutHeaderHeight }` |
| `usePriorityValue(key, props, state)` | 优先级取值 | `ComputedRef` |

```typescript
// BEM 类名示例
const { b, e, m, is } = useNamespace('button');
b(); // 'vben-button'
e('text'); // 'vben-button__text'
m('primary'); // 'vben-button--primary'
is('active'); // 'is-active'
```

### effects/hooks

**位置**: `packages/effects/hooks/src/`

| Hook | 用途 | 主要方法 |
| --- | --- | --- |
| `useTabs()` | 标签页管理 | `closeCurrentTab`, `refreshTab`, `setTabTitle`, `pinTab` |
| `usePagination(list, pageSize)` | 数组分页 | `currentPage`, `paginationList`, `setCurrentPage` |
| `useWatermark()` | 页面水印 | `initWatermark`, `updateWatermark`, `destroyWatermark` |
| `useContentMaximize()` | 内容最大化 | `contentIsMaximize`, `toggleMaximize` |
| `useRefresh()` | 刷新当前页 | `refresh()` |
| `useAppConfig(env, isProduction)` | 应用配置 | 返回配置对象 |
| `useElementPlusDesignTokens()` | Element Plus 设计令牌 | 响应主题变化 |

```typescript
// 标签页管理示例
const { closeCurrentTab, setTabTitle, refreshTab } = useTabs()
await setTabTitle('动态标题')
await refreshTab()

// 分页示例
const list = ref([...])
const { currentPage, paginationList, setCurrentPage } = usePagination(list, 10)

// 水印示例
const { initWatermark } = useWatermark()
await initWatermark({ content: '机密文档' })
```

---

## 工具函数库

**位置**: `packages/utils/src/helpers/`

| 函数 | 用途 | 参数 |
| --- | --- | --- |
| `generateMenus(routes, router)` | 路由生成菜单 | 路由配置、Router 实例 |
| `findMenuByPath(list, path)` | 按路径查找菜单 | 菜单列表、路径 |
| `findRootMenuByPath(menus, path)` | 查找根菜单 | 返回 `{ findMenu, rootMenu, rootMenuPath }` |
| `generateRoutesByBackend(options)` | 后端动态路由 | `{ fetchMenuListAsync, layoutMap, pageMap }` |
| `resetStaticRoutes(router, routes)` | 重置路由 | 清除动态路由 |
| `getPopupContainer(trigger)` | 获取弹窗容器 | 用于 Element Plus 弹窗挂载 |
| `unmountGlobalLoading()` | 卸载全局 Loading | 初始化完成后调用 |

```typescript
// 路由工具示例
import { generateMenus, findMenuByPath, resetStaticRoutes } from '@vben/utils';

const menus = generateMenus(routes, router);
const menu = findMenuByPath(menus, '/system/user');
resetStaticRoutes(router, staticRoutes);
```

---

## 使用指南

### 导入方式

```typescript
// Web-Admin 本地组件
import StatusTag from '#/components/status/StatusTag.vue';
import RichTextEditor from '#/components/editor/RichTextEditor.vue';
import ImageUpload from '#/components/image-upload/image-upload.vue';

// Effects 组件（通过 @vben 别名）
import { Page } from '@vben/common-ui';
import { useTabs, useWatermark } from '@vben/hooks';
import { useNamespace, useIsMobile } from '@vben/composables';
import { generateMenus, resetStaticRoutes } from '@vben/utils';
```

### 组件选择建议

#### 状态显示

| 场景           | 推荐组件       |
| -------------- | -------------- |
| 表格列显示状态 | `StatusTag`    |
| 表单选择状态   | `StatusSelect` |
| 表单开关切换   | `StatusSwitch` |

#### 内容编辑

| 场景          | 推荐组件         |
| ------------- | ---------------- |
| 富文本内容    | `RichTextEditor` |
| Markdown 文档 | `MarkdownEditor` |
| 动态内容类型  | `ContentEditor`  |
| 纯预览        | `ContentPreview` |
| 代码编辑      | `MonacoEditor`   |

#### 选择器

| 场景     | 推荐组件      |
| -------- | ------------- |
| 后端枚举 | `EnumSelect`  |
| 图标选择 | `IconSelect`  |
| 文件选择 | `FileSelect`  |
| 图片上传 | `ImageUpload` |

---

## 组件统计

| 类别          | 数量   | 位置                             |
| ------------- | ------ | -------------------------------- |
| 业务组件      | 11 个  | `apps/web-admin/src/components/` |
| 布局组件      | 30+ 个 | `packages/effects/layouts/`      |
| 公共 UI       | 20+ 个 | `packages/effects/common-ui/`    |
| Core Hooks    | 8 个   | `packages/@core/composables/`    |
| Effects Hooks | 8 个   | `packages/effects/hooks/`        |
| 工具函数      | 8 个   | `packages/utils/src/helpers/`    |

---

## 新增组件规范

### 命名规范

- 组件文件：`kebab-case.vue` (如 `status-tag.vue`)
- 组件导出名：`PascalCase` (如 `StatusTag`)
- Hooks：`use` 前缀 (如 `useXxx`)

### 存放位置

- **通用 UI 组件** → `packages/effects/common-ui/`
- **业务组件** → `apps/web-admin/src/components/`
- **公共 Hooks** → `packages/effects/hooks/` 或 `packages/@core/composables/`
- **工具函数** → `packages/utils/src/helpers/`

### 文档要求

新增组件后请更新本宪章文档，包含：

1. 组件用途说明
2. Props 定义
3. 使用示例
4. 导入方式
