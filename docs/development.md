# 开发指南

本文档详细介绍如何搭建本地开发环境并启动 Triflow 的各个项目。

---

## 目录

- [环境要求](#环境要求)
- [后端开发 (triflow-server)](#后端开发-triflow-server)
- [Web 前端开发 (triflow-web)](#web-前端开发-triflow-web)
- [移动端开发 (triflow-app)](#移动端开发-triflow-app)
- [服务端口汇总](#服务端口汇总)
- [IDE 配置](#ide-配置)
- [常见问题](#常见问题)

---

## 环境要求

### 后端

| 工具 | 版本 | 说明 |
|:---|:---:|:---|
| JDK | **25** | 项目使用 Java 25 特性（虚拟线程、模式匹配等） |
| Maven | 3.9+ | 依赖管理 |
| MySQL | 8.0+ | 开发环境可使用 Docker 部署的实例 |
| Redis | 7.0+ | 开发环境可使用 Docker 部署的实例 |

### 前端 / 移动端

| 工具 | 版本 | 说明 |
|:---|:---:|:---|
| Node.js | 20.12+ | JavaScript 运行时 |
| pnpm | 10+ | 包管理器（`npm i -g pnpm`） |
| 微信开发者工具 | 最新版 | 小程序开发（可选） |
| HBuilderX | 最新版 | App 原生打包（可选） |

---

## 后端开发 (triflow-server)

### 1. 配置 JDK 25

#### 方式一：direnv 自动切换（推荐）

项目已配置 `.envrc` 文件，使用 [direnv](https://direnv.net/) 进入目录时自动切换 JDK：

```bash
# macOS 安装
brew install direnv
echo 'eval "$(direnv hook zsh)"' >> ~/.zshrc
source ~/.zshrc

# 进入项目目录并允许配置
cd triflow-server
direnv allow
```

#### 方式二：手动设置

```bash
# macOS
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"

# Linux
export JAVA_HOME="/usr/lib/jvm/jdk-25"

export PATH="$JAVA_HOME/bin:$PATH"
java -version  # 应输出 java version "25"
```

### 2. 首次构建

项目使用 `${revision}` 作为版本变量，集成了 `flatten-maven-plugin`，**首次必须完整安装**：

```bash
cd triflow-server
mvn clean install -DskipTests
```

### 3. 启动后端服务

```bash
# 使用 local 配置启动（推荐，连接 Docker 部署的数据库）
mvn -pl base spring-boot:run -Dspring-boot.run.profiles=local

# 或使用 JAR 运行
mvn package -DskipTests
java -jar base/target/base-1.0.0.jar --spring.profiles.active=local
```

启动成功后：
- API 地址：http://localhost:7100
- API 文档：http://localhost:7100/doc.html

### 4. 环境配置（Profile）

| Profile | 说明 |
|:---|:---|
| `local` | 本地开发，连接本地或 Docker 数据库 |
| `dev` | 开发环境 |
| `test` | 测试环境 |
| `prod` | 生产环境 |

配置文件位于 `base/src/main/resources/config/{profile}/`，主要配置项：

| 文件 | 说明 |
|:---|:---|
| `application.yml` | 基础配置（端口、数据库、Redis） |
| `business.yml` | 业务配置（CORS、文件存储、微信） |
| `flyway.yml` | 数据库迁移配置 |

---

## Web 前端开发 (triflow-web)

### 1. 安装依赖

```bash
cd triflow-web
pnpm install
```

### 2. 启动开发服务器

```bash
# 启动后台管理系统
pnpm dev:admin

# 使用 Turbo 启动所有应用
pnpm dev

# 启动 Mock 服务
pnpm dev:mock
```

开发地址：http://localhost:5777

### 3. 构建与工具

| 命令 | 说明 |
|:---|:---|
| `pnpm build:admin` | 构建生产版本 |
| `pnpm lint` | 代码格式检查 |
| `pnpm format` | 代码格式化 |
| `pnpm clean` | 清理构建缓存 |

### 4. 项目结构

```
triflow-web/
├── apps/
│   └── web-admin/       # 后台管理应用
│       ├── src/
│       │   ├── views/   # 页面组件
│       │   ├── api/     # API 定义
│       │   └── router/  # 路由配置
│       └── vite.config.mts
├── packages/            # 共享包
│   ├── @core/           # 核心工具
│   └── types/           # 类型定义
└── package.json
```

---

## 移动端开发 (triflow-app)

### 1. 安装依赖

```bash
cd triflow-app
pnpm install

# 如果提示缺少 manifest.json
pnpm init-baseFiles
```

### 2. H5 开发

```bash
pnpm dev:h5           # 默认环境
pnpm dev:h5:test      # 测试环境
pnpm dev:h5:prod      # 生产环境
```

H5 开发地址：http://localhost:9000

### 3. 微信小程序开发

```bash
pnpm dev:mp
```

使用微信开发者工具打开 `dist/dev/mp-weixin` 目录。

### 4. App 开发

```bash
pnpm dev:app          # App 开发模式
```

> 详细打包流程参阅 [App 打包指南](app-build.md)

### 5. 构建命令

| 命令 | 说明 |
|:---|:---|
| `pnpm build:h5` | 构建 H5 版本 |
| `pnpm build:mp` | 构建微信小程序 |
| `pnpm build:app` | 构建 Android/iOS |

---

## 服务端口汇总

| 服务 | 端口 | 地址 |
|:---|:---:|:---|
| triflow-server（后端） | 7100 | http://localhost:7100 |
| triflow-web（管理后台） | 5777 | http://localhost:5777 |
| triflow-app（H5） | 9000 | http://localhost:9000 |
| API 文档 | 7100 | http://localhost:7100/doc.html |

---

## IDE 配置

### VS Code 推荐扩展

| 扩展 | 用途 |
|:---|:---|
| Vue - Official (Volar) | Vue 3 语言支持 |
| ESLint | 代码规范检查 |
| Prettier | 代码格式化 |
| UnoCSS | UnoCSS 智能提示 |
| Tailwind CSS IntelliSense | TailwindCSS 提示 |

### IntelliJ IDEA

1. **Project SDK** — 设置为 JDK 25
2. **插件** — 安装 Lombok、MyBatis 插件
3. **代码风格** — File → Settings → Editor → Code Style

### 推荐启动顺序

1. **后端服务** — 首先启动，提供 API
2. **Web 前端** — 调用后端 API
3. **移动端** — 调用后端 API

> 使用 Mock 服务时可不启动后端直接开发前端。

---

## 常见问题

### Maven 编译失败：`${revision}` 无法解析

```
Could not find artifact com.glowxq:build:pom:${revision}
```

**解决：** 项目使用 `flatten-maven-plugin`，需先完整安装：

```bash
cd triflow-server && mvn clean install -DskipTests
```

### Java 版本不匹配

```
UnsupportedClassVersionError: class file version 69.0
```

**解决：** 确保使用 JDK 25：

```bash
java -version
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"
```

### 缺少 ClientService Bean

```
No qualifying bean of type 'com.glowxq.common.security.service.ClientService'
```

**解决：** 项目已提供默认实现 `ClientServiceImpl`（`base/src/main/java/com/glowxq/base/service/`）。如需自定义请在 `business` 模块中实现。

### CORS 跨域错误

**解决：** 确保 `config/local/business.yml` 中配置了：

```yaml
web:
  cors:
    allowed-origins:
      - "*"
```

### pnpm install 失败

```bash
# 清理后重试
rm -rf node_modules
pnpm install

# 使用国内镜像
pnpm config set registry https://registry.npmmirror.com
pnpm install
```

### triflow-app 启动失败：缺少 manifest.json

```bash
cd triflow-app && pnpm init-baseFiles
```

### 端口被占用

```bash
lsof -i:7100    # 查看占用进程
kill -9 <PID>   # 终止进程
```
