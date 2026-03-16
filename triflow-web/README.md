# Triflow Web

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883?style=flat-square&logo=vuedotjs" alt="Vue 3">
  <img src="https://img.shields.io/badge/Vite-7.x-646cff?style=flat-square&logo=vite" alt="Vite">
  <img src="https://img.shields.io/badge/TypeScript-5.x-3178c6?style=flat-square&logo=typescript" alt="TypeScript">
  <img src="https://img.shields.io/badge/Element%20Plus-2.x-409eff?style=flat-square&logo=element" alt="Element Plus">
  <img src="https://img.shields.io/badge/pnpm-10.x-f69220?style=flat-square&logo=pnpm" alt="pnpm">
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square" alt="License">
</p>

> Triflow Web 端 - 基于 Vue Vben Admin (Element Plus 版本) 的企业级后台管理系统

## 项目结构

```
triflow-web/
├── apps/
│   ├── web-admin/            # 后台管理系统 (Element Plus)
│   ├── web-home/             # 官网 (预留)
│   └── backend-mock/         # Mock 服务
├── packages/                 # 共享包
│   ├── @core/               # 核心组件
│   ├── effects/             # 功能模块
│   └── ...
├── internal/                # 内部配置
└── scripts/                 # 构建脚本
```

## 技术栈

| 技术         | 版本 | 说明      |
| ------------ | ---- | --------- |
| Vue          | 3.5+ | 核心框架  |
| Vite         | 7.x  | 构建工具  |
| TypeScript   | 5.x  | 类型支持  |
| Element Plus | 2.x  | UI 组件库 |
| Pinia        | 3.x  | 状态管理  |
| Vue Router   | 4.x  | 路由管理  |
| TailwindCSS  | 3.x  | CSS 框架  |

## 快速开始

### 环境要求

- Node.js 20.12.0+
- pnpm 10.0.0+

### 安装依赖

```bash
pnpm install
```

### 启动开发服务器

```bash
# 启动后台管理系统
pnpm dev:admin

# 启动 Mock 服务
pnpm dev:mock

# 同时启动所有应用
pnpm dev
```

### 构建生产版本

```bash
pnpm build:admin
```

## 特性

- **现代化框架**：基于 Vue 3 + TypeScript + Vite
- **Element Plus UI**：成熟稳定的 UI 组件库
- **权限管理**：内置动态路由权限控制
- **国际化**：完整的多语言支持
- **主题定制**：支持多种主题色彩配置
- **Mock 服务**：内置 Nitro Mock 服务，便于前端开发

## 相关链接

- [Vue Vben Admin](https://github.com/vbenjs/vue-vben-admin) - 基础模板
- [Element Plus](https://element-plus.org/) - UI 组件库
- [Triflow Server](../triflow-server) - 后端服务

## 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源协议发布。

---

<p align="center">
  Made with ❤️ by glowxq
</p>
