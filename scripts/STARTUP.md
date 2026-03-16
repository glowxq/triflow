# Triflow 项目启动指南

> 本文档详细说明如何启动 Triflow 三端项目的各个模块。

## 项目概览

```
triflow/
├── triflow-server/      # 后端服务 (Java 25 + Spring Boot 3.5)
│   ├── base/            # 启动模块 (端口: 7100)
│   ├── common/          # 通用模块
│   ├── business/        # 业务模块
│   └── demo/            # 示例模块
├── triflow-web/         # Web 前端 (Vue 3 + Element Plus)
│   └── apps/
│       ├── web-admin/   # 后台管理系统 (端口: 5777)
│       └── backend-mock/# Mock 服务
└── triflow-app/         # 移动端 (UniApp)
    └── 支持: H5 (端口: 9000) / 微信小程序 / Android / iOS
```

---

## 环境要求

### 后端 (triflow-server)

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| JDK | **25** | 项目使用 Java 25 新特性 |
| Maven | 3.9+ | 构建工具 |
| MySQL | 8.0+ | 关系数据库 (可选) |
| Redis | 7.0+ | 缓存服务 (可选) |

### Web 前端 (triflow-web)

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| Node.js | 20.12.0+ | JavaScript 运行时 |
| pnpm | 10.0.0+ | 包管理器 |

### 移动端 (triflow-app)

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| Node.js | 20+ | JavaScript 运行时 |
| pnpm | 10+ | 包管理器 |
| 微信开发者工具 | 最新版 | 小程序开发 (可选) |
| HBuilderX | 最新版 | App 打包 (可选) |

---

## 一键启动

### macOS / Linux

使用项目根目录的启动脚本：

```bash
# 赋予执行权限
chmod +x scripts/start.sh

# 启动所有服务 (后端 + Web)
./scripts/start.sh

# 启动所有服务 (包括移动端)
./scripts/start.sh all

# 仅启动后端
./scripts/start.sh server

# 仅启动 Web 前端
./scripts/start.sh web

# 仅启动移动端 (H5)
./scripts/start.sh app h5

# 停止所有服务
./scripts/start.sh stop

# 查看服务状态
./scripts/start.sh status
```

### Windows

**方式一：双击运行 (推荐)**

直接双击 `scripts/start.bat`，会显示交互式菜单选择要执行的操作。

**方式二：PowerShell 命令行**

```powershell
# 启动所有服务 (后端 + Web)
.\scripts\start.ps1

# 启动所有服务 (包括移动端)
.\scripts\start.ps1 all

# 仅启动后端
.\scripts\start.ps1 server

# 仅启动 Web 前端
.\scripts\start.ps1 web

# 仅启动移动端 (H5)
.\scripts\start.ps1 app h5

# 停止所有服务
.\scripts\start.ps1 stop

# 查看服务状态
.\scripts\start.ps1 status
```

**方式三：CMD 命令行**

```cmd
scripts\start.bat server
scripts\start.bat web
scripts\start.bat stop
```

---

## 手动启动方式

### 1. 后端服务 (triflow-server)

#### 首次启动准备

**重要：** 项目使用 `${revision}` 作为版本变量，并集成了 `flatten-maven-plugin`。首次运行必须完整编译安装。

```bash
cd triflow-server

# 1. 设置 Java 25 环境 (重要！)
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# 验证 Java 版本
java -version
# 应输出: java version "25" ...

# 2. 首次编译安装所有模块到本地仓库
mvn clean install -DskipTests
```

#### 启动后端服务

```bash
cd triflow-server

# 设置 Java 25 (如未使用 direnv)
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"

# 启动服务 (使用 local 配置)
mvn -pl base spring-boot:run -Dspring-boot.run.profiles=local

# 或者在 IDEA 中直接运行 BaseApplication.java
```

**环境配置说明：**

| Profile | 说明 |
|---------|------|
| `local` | 本地开发环境 (推荐) |
| `dev` | 开发环境 |
| `test` | 测试环境 |
| `prod` | 生产环境 |

**服务地址：**
- API 地址: http://localhost:7100
- API 文档: http://localhost:7100/doc.html (Knife4j)

---

### 2. Web 前端 (triflow-web)

```bash
cd triflow-web

# 安装依赖 (首次运行)
pnpm install

# 启动后台管理系统
pnpm dev:admin

# 或使用 turbo 启动所有应用
pnpm dev

# 启动 Mock 服务 (独立运行)
pnpm dev:mock
```

**服务地址：** http://localhost:5777 (如端口被占用会自动切换)

**其他命令：**

| 命令 | 说明 |
|------|------|
| `pnpm build:admin` | 构建后台管理系统 |
| `pnpm lint` | 代码检查 |
| `pnpm format` | 代码格式化 |
| `pnpm clean` | 清理构建缓存 |

---

### 3. 移动端 (triflow-app)

```bash
cd triflow-app

# 安装依赖 (首次运行)
pnpm install

# H5 开发模式
pnpm dev:h5

# 微信小程序开发模式
pnpm dev:mp

# Android/iOS App 开发模式
pnpm dev:app

# 指定环境运行
pnpm dev:h5:test    # 测试环境
pnpm dev:h5:prod    # 生产环境
```

**服务地址：**
- H5: http://localhost:9000
- 小程序: 使用微信开发者工具打开 `dist/dev/mp-weixin` 目录

**构建命令：**

| 命令 | 说明 |
|------|------|
| `pnpm build:h5` | 构建 H5 版本 |
| `pnpm build:mp` | 构建微信小程序 |
| `pnpm build:app` | 构建 Android/iOS |

---

## 启动顺序 (推荐)

1. **后端服务** - 首先启动，提供 API 接口
2. **Web 前端** - 需要调用后端 API
3. **移动端** - 需要调用后端 API

> 如果使用 Mock 服务，可以不启动后端直接开发前端。

---

## 端口汇总

| 服务 | 端口 | 说明 |
|------|------|------|
| triflow-server | 7100 | 后端 API |
| web-admin | 5777 | 后台管理系统 |
| triflow-app (H5) | 9000 | 移动端 H5 |

---

## 常见问题

### 1. Maven 编译失败：${revision} 无法解析

**错误信息：**
```
Could not find artifact com.glowxq:build:pom:${revision}
```

**原因：** 项目使用 `flatten-maven-plugin` 处理版本变量，需要先安装父模块。

**解决方案：**
```bash
cd triflow-server
mvn clean install -DskipTests
```

### 2. Java 版本不匹配

**错误信息：**
```
UnsupportedClassVersionError: class file version 69.0
```

**原因：** 项目使用 Java 25 编译，但运行时使用的是其他版本。

**解决方案：**
```bash
# 检查 Java 版本
java -version

# 设置 Java 25
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

### 3. 缺少 ClientService Bean

**错误信息：**
```
No qualifying bean of type 'com.glowxq.common.security.service.ClientService'
```

**原因：** 这是脚手架项目，部分业务接口需要自行实现。

**解决方案：** 项目已提供默认实现 `ClientServiceImpl`，如需自定义请在 `business` 模块中实现。

### 4. pnpm install 失败

```bash
# 清理 node_modules 后重试
rm -rf node_modules pnpm-lock.yaml
pnpm install

# 或使用国内镜像
pnpm config set registry https://registry.npmmirror.com
pnpm install
```

### 5. 端口占用

```bash
# 查看端口占用
lsof -i:7100

# 杀死进程
kill -9 <PID>
```

---

## 开发建议

1. **首次运行**：建议按顺序分别启动各服务，确保配置正确
2. **日常开发**：使用一键启动脚本 `./scripts/start.sh`
3. **前端独立开发**：启用 Mock 服务 (`VITE_NITRO_MOCK=true`)
4. **API 调试**：使用 Knife4j 在线文档 (`/doc.html`)
5. **Java 环境**：推荐使用 `direnv` 自动切换 JDK 版本

---

*Made with ❤️ by Triflow Team*
