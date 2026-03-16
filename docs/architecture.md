# 架构说明

本文档介绍 Triflow 的整体架构、模块职责和设计理念。

---

## 目录

- [总体架构](#总体架构)
- [后端架构 (triflow-server)](#后端架构-triflow-server)
- [Web 前端架构 (triflow-web)](#web-前端架构-triflow-web)
- [移动端架构 (triflow-app)](#移动端架构-triflow-app)
- [部署架构](#部署架构)
- [设计理念](#设计理念)

---

## 总体架构

Triflow 采用**前后端分离**的三端架构，后端提供统一的 RESTful API，Web 端和移动端独立开发、独立部署。

```
                  ┌─────────────────────────────────────────────┐
                  │                  客户端层                    │
                  │                                              │
                  │  ┌─────────────┐  ┌───────────────────────┐  │
                  │  │ triflow-web │  │     triflow-app       │  │
                  │  │ Vue 3 Admin │  │ UniApp 多端应用        │  │
                  │  │ Element Plus│  │ 微信小程序/Android/iOS │  │
                  │  └──────┬──────┘  └───────────┬───────────┘  │
                  └─────────┼─────────────────────┼──────────────┘
                            │     RESTful API     │
                  ┌─────────▼─────────────────────▼──────────────┐
                  │              triflow-server                   │
                  │         Spring Boot 3.5 + Java 25             │
                  │                                               │
                  │  ┌──────────────────────────────────────────┐ │
                  │  │ Controller 层 — 路由、参数校验、响应封装   │ │
                  │  ├──────────────────────────────────────────┤ │
                  │  │ Service 层 — 业务逻辑                     │ │
                  │  ├──────────────────────────────────────────┤ │
                  │  │ Mapper 层 — 数据访问 (MyBatis-Flex)       │ │
                  │  └──────────────────────────────────────────┘ │
                  │                                               │
                  │  ┌─────────┐  ┌─────────┐  ┌─────────┐      │
                  │  │  MySQL  │  │  Redis  │  │  MinIO  │      │
                  │  │ 关系存储 │  │ 缓存/会话│  │ 对象存储 │      │
                  │  └─────────┘  └─────────┘  └─────────┘      │
                  └──────────────────────────────────────────────┘
```

---

## 后端架构 (triflow-server)

### 模块结构

后端采用 Maven 多模块架构，分为**公共层**、**业务层**和**启动层**三个层次：

```
triflow-server/
│
├── dependencies/          # BOM 依赖管理（统一版本号）
│
├── common/                # 公共模块层（可复用组件）
│   ├── common-core        #   核心工具：响应封装、异常处理、通用工具类
│   ├── common-security    #   安全认证：Sa-Token + JWT、权限注解
│   ├── common-db-mysql    #   数据库：MyBatis-Flex 配置、分页、数据权限
│   ├── common-db-redis    #   缓存：Redis 配置、缓存工具
│   ├── common-oss         #   对象存储：MinIO / 阿里云 OSS / 本地存储
│   ├── common-ai          #   AI 能力：Spring AI 多模型路由
│   ├── common-excel       #   Excel：导入导出工具
│   ├── common-log         #   日志：操作日志注解与切面
│   ├── common-sms         #   短信：阿里云短信服务
│   ├── common-wechat      #   微信：小程序登录、支付、公众号
│   └── common-business    #   业务公共：业务层共享逻辑
│
├── business/              # 业务模块层（项目特有业务）
│
└── base/                  # 启动模块（Spring Boot 入口）
    └── src/main/java/.../base/
        ├── auth/          #   认证登录
        ├── system/        #   系统管理（用户/角色/部门/菜单）
        ├── config/        #   系统配置
        ├── file/          #   文件管理
        ├── ai/            #   AI 管理
        ├── cms/           #   内容管理
        ├── wallet/        #   钱包系统
        ├── wechat/        #   微信集成
        ├── notify/        #   通知推送
        ├── analytics/     #   数据分析
        ├── captcha/       #   验证码
        ├── health/        #   健康检查
        └── api/           #   开放 API
```

### 分层约定

每个功能模块内部遵循统一的分层结构：

```
module/
├── entity/        # 数据库实体类（@Table 映射）
├── mapper/        # MyBatis-Flex Mapper 接口
├── service/       # 业务接口
│   └── impl/      # 业务实现
├── pojo/          # 数据传输对象
│   ├── dto/       #   请求 DTO
│   └── vo/        #   响应 VO
├── enums/         # 枚举定义
└── controller/    # REST 控制器（仅 base 模块）
```

### 关键技术决策

| 方面 | 选择 | 原因 |
|:---|:---|:---|
| ORM | MyBatis-Flex | 比 MyBatis-Plus 更轻量，链式查询更优雅 |
| 认证 | Sa-Token + JWT | 灵活的权限模型，支持多端会话管理 |
| API 文档 | Knife4j (OpenAPI 3) | 自动生成、在线调试、UI 友好 |
| AI 集成 | Spring AI | Spring 官方 AI 抽象层，支持多模型切换 |
| 文件存储 | MinIO + S3 协议 | 兼容 AWS S3，可无缝切换云存储 |
| 数据库迁移 | Flyway | 版本化迁移脚本，Docker 初始化用 init.sql |

### API 设计规范

- **路径前缀：** `/base/client/` 面向客户端，`/base/admin/` 面向管理端
- **统一响应格式：** `{ code, message, data, success }`
- **分页约定：** 请求用 `pageNum` + `pageSize`，响应用标准分页封装
- **认证方式：** Header 携带 `Authorization: Bearer {token}`

---

## Web 前端架构 (triflow-web)

基于 [Vue Vben Admin](https://github.com/vbenjs/vue-vben-admin) 二次开发，采用 Monorepo 架构：

```
triflow-web/
├── apps/
│   └── web-admin/           # 后台管理应用
│       ├── src/
│       │   ├── views/       #   页面组件（按模块组织）
│       │   ├── api/         #   API 请求定义
│       │   ├── router/      #   路由配置
│       │   ├── store/       #   Pinia 状态管理
│       │   └── layouts/     #   布局组件
│       └── vite.config.mts  #   Vite 构建配置
│
├── packages/                # 共享包
│   ├── @core/               #   核心工具、请求封装
│   ├── icons/               #   图标库
│   ├── types/               #   TypeScript 类型定义
│   └── utils/               #   通用工具函数
│
└── scripts/                 # 构建脚本
```

### 技术要点

- **构建工具：** Vite 7 + Turbo（Monorepo 构建编排）
- **状态管理：** Pinia 3，按模块拆分 Store
- **请求层：** 基于 Axios 封装，支持 Token 刷新、请求重试
- **权限控制：** 路由级 + 按钮级，后端返回菜单数据动态生成路由
- **表格组件：** VXE Table，支持虚拟滚动、编辑行、导出 Excel
- **样式方案：** TailwindCSS + CSS Variables 主题切换

---

## 移动端架构 (triflow-app)

基于 [unibest](https://github.com/feige996/unibest) 二次开发，一套代码多端运行：

```
triflow-app/
├── src/
│   ├── pages/           # 页面（tabbar 页 + 子页面）
│   ├── components/      # 全局组件
│   ├── composables/     # 组合式函数
│   ├── store/           # Pinia 状态管理
│   ├── service/         # API 服务层
│   ├── interceptors/    # 请求/路由拦截器
│   ├── static/          # 静态资源
│   └── uni_modules/     # UniApp 模块
├── env/                 # 多环境配置
│   ├── .env             #   默认环境变量
│   └── .env.production  #   生产环境变量
└── manifest.config.ts   # UniApp 应用配置
```

### 技术要点

- **跨端框架：** UniApp Vue 3，编译到微信小程序 / Android / iOS / H5
- **UI 组件库：** Wot Design Uni，移动端优化
- **请求库：** Alova 3，轻量级请求策略库
- **样式方案：** UnoCSS，原子化 CSS 跨端兼容
- **分页组件：** z-paging，支持下拉刷新 + 上拉加载
- **路由管理：** uni-mini-router，声明式路由

---

## 部署架构

### All-in-One 单容器

```
┌─── Docker Container ────────────────────┐
│  Supervisor (进程管理)                   │
│  ├── MySQL 8.0      ← /data/mysql      │
│  ├── Redis 7        ← /data/redis      │
│  ├── MinIO          ← /data/minio      │
│  ├── Java Backend   ← /app/app.jar     │
│  └── Nginx          ← 反向代理          │
│       ├── /     → Web Admin             │
│       ├── /h5/  → H5 App               │
│       └── /api/ → Backend :7100         │
│                                          │
│  Volume: triflow-aio-data → /data       │
│  Port: 7200                              │
└──────────────────────────────────────────┘
```

### Docker Compose 多容器

```
┌─────────────────────────────────────────────────────────────┐
│                     triflow-network                          │
│                                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                   │
│  │  MySQL   │  │  Redis   │  │  MinIO   │  ← 基础设施层    │
│  │  :3306   │  │  :6379   │  │:9000/9001│                   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘                   │
│       │              │             │                          │
│  ┌────▼──────────────▼─────────────▼─────┐                   │
│  │          triflow-server               │  ← 应用服务层    │
│  │          :7100                         │                   │
│  └────────────────┬──────────────────────┘                   │
│                   │                                          │
│  ┌────────────────▼──────────────────────┐                   │
│  │          triflow-web (Nginx)          │  ← 前端服务层    │
│  │          :7200                         │                   │
│  └───────────────────────────────────────┘                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 设计理念

### 1. 开箱即用

预置完整的用户管理、权限控制、文件存储、AI 集成等通用模块，`git clone` 后即可运行。

### 2. 模块化解耦

每个 `common-*` 模块独立封装，按需引入。不需要短信功能？移除 `common-sms` 依赖即可。

### 3. 三端统一

后端提供统一 API，Web 端和移动端共享相同的接口规范和数据模型。

### 4. 配置驱动

通过 Spring Profile + 环境变量控制行为，同一套代码适配开发、测试、生产多种环境。

### 5. Docker 优先

提供 All-in-One 和 Docker Compose 两种部署方案，无需手动安装任何依赖。
