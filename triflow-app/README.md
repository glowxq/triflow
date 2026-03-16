# Triflow App

<p align="center">
  <img src="https://img.shields.io/badge/UniApp-Vue3-2b9939?style=flat-square" alt="UniApp">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883?style=flat-square&logo=vuedotjs" alt="Vue 3">
  <img src="https://img.shields.io/badge/Vite-5.x-646cff?style=flat-square&logo=vite" alt="Vite">
  <img src="https://img.shields.io/badge/TypeScript-5.x-3178c6?style=flat-square&logo=typescript" alt="TypeScript">
  <img src="https://img.shields.io/badge/UnoCSS-Latest-333333?style=flat-square" alt="UnoCSS">
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square" alt="License">
</p>

> Triflow App 端 - 基于 [unibest](https://github.com/feige996/unibest) 的跨端移动应用

## 支持平台

| H5 | iOS | Android | 微信小程序 | 支付宝小程序 | 字节小程序 | 其他小程序 |
|:--:|:---:|:-------:|:----------:|:------------:|:----------:|:----------:|
| ✓  | ✓   | ✓       | ✓          | ✓            | ✓          | ✓          |

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| UniApp | Vue3 版本 | 跨端框架 |
| Vue | 3.4+ | 核心框架 |
| Vite | 5.x | 构建工具 |
| TypeScript | 5.x | 类型支持 |
| UnoCSS | Latest | 原子化 CSS |
| Pinia | 2.x | 状态管理 |
| z-paging | 2.x | 分页组件 |

## 快速开始

### 环境要求

- Node.js 20+
- pnpm 9+
- 微信开发者工具（小程序开发）
- HBuilderX（App 打包，可选）

### 安装依赖

```bash
pnpm install
```

### 开发运行

```bash
# H5 开发
pnpm dev:h5

# 微信小程序开发
pnpm dev:mp

# App 开发
pnpm dev:app
```

### 构建发布

```bash
# H5 构建
pnpm build:h5

# 微信小程序构建
pnpm build:mp

# App 构建
pnpm build:app
```

## 项目结构

```
triflow-app/
├── src/
│   ├── api/              # API 接口
│   ├── components/       # 公共组件
│   ├── hooks/            # 组合式函数
│   ├── http/             # 请求封装
│   ├── layouts/          # 布局组件
│   ├── pages/            # 页面
│   ├── router/           # 路由配置
│   ├── static/           # 静态资源
│   ├── store/            # 状态管理
│   ├── style/            # 全局样式
│   └── utils/            # 工具函数
├── env/                  # 环境变量配置
└── vite-plugins/         # Vite 插件
```

## 特性

- **跨端开发**：一套代码，多端运行（H5、小程序、App）
- **约定式路由**：基于文件系统的自动路由
- **Layout 布局**：支持多种页面布局
- **请求封装**：统一的 HTTP 请求处理
- **状态管理**：Pinia 状态管理 + 持久化
- **代码规范**：ESLint + Prettier 代码规范

## 相关链接

- [unibest](https://github.com/feige996/unibest) - 基础模板
- [UniApp 官方文档](https://uniapp.dcloud.net.cn/)
- [Triflow Server](../triflow-server) - 后端服务
- [Triflow Web](../triflow-web) - Web 端

## 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源协议发布。

---

<p align="center">
  Made with ❤️ by glowxq
</p>
