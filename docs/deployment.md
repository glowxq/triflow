# 部署指南

本文档详细介绍 Triflow 的两种 Docker 部署方式，以及配置、管理和故障排查。

---

## 目录

- [环境要求](#环境要求)
- [方式一：All-in-One 单容器部署](#方式一all-in-one-单容器部署)
- [方式二：Docker Compose 多容器部署](#方式二docker-compose-多容器部署)
- [自定义配置](#自定义配置)
- [管理命令](#管理命令)
- [数据持久化](#数据持久化)
- [生产环境建议](#生产环境建议)
- [故障排查](#故障排查)

---

## 环境要求

| 依赖 | 版本 | 说明 |
|:---|:---:|:---|
| Docker | 20.10+ | 容器运行时 |
| Docker Compose | v2+ | 仅 Docker Compose 部署需要 |
| 磁盘空间 | 5 GB+ | 镜像构建 + 数据存储 |
| 内存 | 2 GB+ | 推荐 4 GB 以上 |

```bash
# 验证 Docker 环境
docker --version
docker compose version
```

---

## 方式一：All-in-One 单容器部署

将 MySQL、Redis、MinIO、Java 后端、Web 管理后台、H5 移动端全部打包在**单个 Docker 容器**中。

**适用场景：** 快速体验、演示、个人开发

### 快速启动

```bash
chmod +x deploy-all-in-one.sh
./deploy-all-in-one.sh start
```

首次启动会自动构建镜像（约 5-10 分钟），完成后访问：

| 服务 | 地址 | 说明 |
|:---|:---|:---|
| 管理后台 | http://localhost:7200 | 账号 `root` / `root` |
| H5 移动端 | http://localhost:7200/h5/ | 移动端 Web 版 |
| 后端 API | http://localhost:7200/api/ | RESTful 接口 |
| API 文档 | http://localhost:7200/api/doc.html | Knife4j 文档 |

### 架构说明

容器内部通过 [Supervisor](http://supervisord.org/) 管理 5 个进程：

```
                    ┌─────────────────────────────────┐
                    │    Docker Container (7200)       │
                    │                                  │
 localhost:7200 ──▶ │  ┌───────────────────────────┐  │
                    │  │  Nginx (反向代理)           │  │
                    │  │  / → Web Admin              │  │
                    │  │  /h5/ → H5 App              │  │
                    │  │  /api/ → Backend :7100       │  │
                    │  └───────────┬───────────────┘  │
                    │              │                    │
                    │  ┌───────────▼───────────────┐  │
                    │  │  Java Backend (:7100)      │  │
                    │  └───────────┬───────────────┘  │
                    │              │                    │
                    │  ┌───────┐ ┌─▼───┐ ┌─────────┐  │
                    │  │ MySQL │ │Redis│ │  MinIO   │  │
                    │  │ :3306 │ │:6379│ │  :9000   │  │
                    │  └───────┘ └─────┘ └─────────┘  │
                    │                                  │
                    │  Volume: /data (持久化)           │
                    └─────────────────────────────────┘
```

### 自定义端口和密码

```bash
# 自定义端口
TRIFLOW_PORT=8080 ./deploy-all-in-one.sh start

# 自定义数据库密码
MYSQL_PASSWORD=your_password REDIS_PASSWORD=your_password ./deploy-all-in-one.sh start
```

### 支持的环境变量

| 变量 | 默认值 | 说明 |
|:---|:---:|:---|
| `TRIFLOW_PORT` | `7200` | 对外暴露端口 |
| `MYSQL_PASSWORD` | `triflow123` | MySQL root 密码 |
| `MYSQL_DATABASE` | `triflow` | 数据库名 |
| `REDIS_PASSWORD` | `triflow123` | Redis 密码 |
| `MINIO_ACCESS_KEY` | `triflow` | MinIO 用户名 |
| `MINIO_SECRET_KEY` | `triflow123` | MinIO 密码 |
| `MINIO_BUCKET` | `triflow` | MinIO 存储桶 |
| `JAVA_OPTS` | `-Xms256m -Xmx512m` | JVM 参数 |

---

## 方式二：Docker Compose 多容器部署

各服务运行在独立容器中，通过 Docker 网络通信。

**适用场景：** 团队开发、准生产部署

### 快速启动

```bash
chmod +x deploy.sh
./deploy.sh start
```

首次启动会自动构建后端和 Web 镜像，完成后访问：

| 服务 | 地址 | 说明 |
|:---|:---|:---|
| 管理后台 | http://localhost:7200 | 账号 `root` / `root` |
| 后端 API | http://localhost:7100 | RESTful 接口 |
| API 文档 | http://localhost:7100/doc.html | Knife4j 文档 |
| MinIO 控制台 | http://localhost:9001 | 账号 `triflow` / `triflow123` |

### 容器架构

```
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│  MySQL   │  │  Redis   │  │  MinIO   │  │  Server  │  │   Web    │
│  :3306   │  │  :6379   │  │:9000/9001│  │  :7100   │  │  :7200   │
└────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘
     └──────────────┴──────────────┴──────────────┴──────────────┘
                          triflow-network (bridge)
```

### 自定义配置

```bash
# 首次启动会自动生成 .env 文件
# 或手动复制模板
cp .env.example .env
vim .env
```

`.env` 文件支持的关键配置：

| 变量 | 默认值 | 说明 |
|:---|:---:|:---|
| `MYSQL_PORT` | `3306` | MySQL 映射端口 |
| `REDIS_PORT` | `6379` | Redis 映射端口 |
| `SERVER_PORT` | `7100` | 后端 API 端口 |
| `WEB_PORT` | `7200` | Web 管理后台端口 |
| `MINIO_API_PORT` | `9000` | MinIO API 端口 |
| `MINIO_CONSOLE_PORT` | `9001` | MinIO 控制台端口 |
| `MYSQL_PASSWORD` | `triflow123` | MySQL 密码 |
| `REDIS_PASSWORD` | `triflow123` | Redis 密码 |

---

## 管理命令

两种部署方式的管理命令格式一致：

### All-in-One

```bash
./deploy-all-in-one.sh start      # 启动（首次自动构建）
./deploy-all-in-one.sh stop       # 停止
./deploy-all-in-one.sh restart    # 重启
./deploy-all-in-one.sh status     # 查看状态
./deploy-all-in-one.sh logs       # 查看所有日志
./deploy-all-in-one.sh logs server  # 查看后端日志
./deploy-all-in-one.sh clean      # 清理所有数据（危险）
./deploy-all-in-one.sh build      # 仅构建镜像
```

### Docker Compose

```bash
./deploy.sh start         # 启动（首次自动构建）
./deploy.sh stop          # 停止
./deploy.sh restart       # 重启
./deploy.sh status        # 查看状态
./deploy.sh logs          # 查看所有日志
./deploy.sh logs server   # 查看后端日志
./deploy.sh clean         # 清理所有数据（危险）
```

---

## 数据持久化

### All-in-One

数据存储在 Docker 命名卷 `triflow-aio-data` 中：

```
/data/
├── mysql/     # MySQL 数据文件
├── redis/     # Redis 持久化数据
├── minio/     # MinIO 对象存储文件
└── logs/      # 所有服务日志
```

### Docker Compose

各服务使用独立命名卷：

| 卷名 | 用途 |
|:---|:---|
| `mysql-data` | MySQL 数据 |
| `redis-data` | Redis 持久化 |
| `minio-data` | MinIO 文件 |

```bash
# 查看卷列表
docker volume ls | grep triflow

# 备份 MySQL 数据
docker exec triflow-mysql mysqldump -u root -ptriflow123 triflow > backup.sql
```

---

## 生产环境建议

### 安全加固

1. **修改所有默认密码** — MySQL、Redis、MinIO 的默认密码务必修改
2. **配置 JWT Secret** — 在 `.env` 中设置 `SA_TOKEN_JWT_SECRET` 为随机强密钥
3. **限制端口暴露** — 仅暴露必要端口（如只暴露 7200），数据库端口不对外开放
4. **启用 HTTPS** — 在 Nginx 或负载均衡器层配置 SSL 证书

### 性能优化

1. **JVM 参数** — 根据服务器内存调整 `JAVA_OPTS`，建议生产环境至少 `-Xmx1g`
2. **MySQL 调优** — 配置 `innodb_buffer_pool_size`、`max_connections` 等参数
3. **Redis 内存** — 设置 `maxmemory` 防止内存溢出

### 推荐方案

| 场景 | 推荐方式 | 说明 |
|:---|:---|:---|
| 个人体验 | All-in-One | 最简单，一条命令搞定 |
| 团队开发 | Docker Compose | 服务独立，便于调试 |
| 生产部署 | Docker Compose + 外部数据库 | 数据库和缓存使用云服务 |

---

## 故障排查

### 容器启动失败

```bash
# 查看容器日志
docker logs triflow-aio          # All-in-One
docker logs triflow-server       # Compose 后端

# 进入容器排查
docker exec -it triflow-aio bash
supervisorctl status             # 查看各服务状态
```

### 端口被占用

```bash
# 检查端口占用
lsof -i:7200
lsof -i:7100
lsof -i:3306

# 修改端口
TRIFLOW_PORT=8080 ./deploy-all-in-one.sh start
```

### 数据库连接失败

```bash
# 检查 MySQL 是否正常运行
docker exec triflow-aio mysqladmin ping -u root -ptriflow123 -h 127.0.0.1

# 查看 MySQL 日志
docker exec triflow-aio cat /data/logs/mysql-error.log
```

### 重新初始化

```bash
# 停止并清理所有数据（会删除数据库！）
./deploy-all-in-one.sh clean
# 或
./deploy.sh clean

# 重新启动
./deploy-all-in-one.sh start
```

### 镜像重新构建

```bash
# All-in-One：强制重新构建
./deploy-all-in-one.sh build

# Docker Compose：重新构建
docker compose build --no-cache
./deploy.sh start
```
