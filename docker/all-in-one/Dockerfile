# =====================================================
# Triflow All-in-One Docker Image
# 将 MySQL + Redis + MinIO + Backend + Web + H5
# 全部打包到单个容器中，适用于快速体验和演示
# =====================================================

# ====== Stage 1: Build Java Backend ======
FROM maven:3.9-eclipse-temurin-25 AS server-builder

WORKDIR /build

# 先复制 pom 文件利用 Docker 层缓存
COPY triflow-server/pom.xml .
COPY triflow-server/common/pom.xml common/pom.xml
COPY triflow-server/common/common-core/pom.xml common/common-core/pom.xml
COPY triflow-server/common/common-security/pom.xml common/common-security/pom.xml
COPY triflow-server/common/common-db-mysql/pom.xml common/common-db-mysql/pom.xml
COPY triflow-server/common/common-db-redis/pom.xml common/common-db-redis/pom.xml
COPY triflow-server/common/common-oss/pom.xml common/common-oss/pom.xml
COPY triflow-server/common/common-sms/pom.xml common/common-sms/pom.xml
COPY triflow-server/common/common-wechat/pom.xml common/common-wechat/pom.xml
COPY triflow-server/common/common-excel/pom.xml common/common-excel/pom.xml
COPY triflow-server/common/common-log/pom.xml common/common-log/pom.xml
COPY triflow-server/common/common-business/pom.xml common/common-business/pom.xml
COPY triflow-server/common/common-ai/pom.xml common/common-ai/pom.xml
COPY triflow-server/dependencies/pom.xml dependencies/pom.xml
COPY triflow-server/business/pom.xml business/pom.xml
COPY triflow-server/demo/pom.xml demo/pom.xml
COPY triflow-server/base/pom.xml base/pom.xml

RUN mvn dependency:go-offline -B -DskipTests 2>/dev/null || true

COPY triflow-server/ .
RUN mvn clean package -DskipTests -pl base -am -B

# ====== Stage 2: Build Web Admin ======
FROM node:22-slim AS web-builder

ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
ENV NODE_OPTIONS=--max-old-space-size=8192

RUN npm i -g corepack

WORKDIR /app
COPY triflow-web/ .
RUN --mount=type=cache,id=pnpm-web,target=/pnpm/store pnpm install --frozen-lockfile
RUN pnpm run build:admin

# ====== Stage 3: Build H5 App ======
FROM node:22-slim AS h5-builder

ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
ENV NODE_OPTIONS=--max-old-space-size=8192

RUN npm i -g corepack

WORKDIR /app
COPY triflow-app/ .

# 覆盖 H5 生产环境配置：API 使用 nginx 反向代理
RUN printf 'NODE_ENV=production\nVITE_DELETE_CONSOLE=true\nVITE_SHOW_SOURCEMAP=false\nVITE_SERVER_BASEURL=/api\n' > env/.env.production \
    && printf 'VITE_APP_TITLE=Triflow\nVITE_APP_PORT=7300\nVITE_UNI_APPID=__UNI__TRIFLOW\nVITE_WX_APPID=\nVITE_APP_PUBLIC_BASE=/h5/\nVITE_SERVER_BASEURL=/api\nVITE_APP_PROXY_ENABLE=false\nVITE_AUTH_MODE=single\nVITE_COPY_NATIVE_RES_ENABLE=false\n' > env/.env

RUN --mount=type=cache,id=pnpm-h5,target=/pnpm/store pnpm install --frozen-lockfile
RUN pnpm build:h5

# ====== Stage 4: JRE 提取 ======
FROM eclipse-temurin:25-jre AS jre-source

# ====== Stage 5: Runtime - All In One ======
FROM ubuntu:24.04

LABEL maintainer="Triflow <https://github.com/glowxq/triflow>"
LABEL description="Triflow All-in-One: MySQL + Redis + MinIO + Backend + Web + H5"

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8

# ---- Install JRE ----
COPY --from=jre-source /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# ---- Install system packages ----
RUN apt-get update && apt-get install -y --no-install-recommends \
    mysql-server \
    redis-server \
    nginx \
    supervisor \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# ---- Install MinIO (支持 amd64 / arm64) ----
RUN arch=$(dpkg --print-architecture) && \
    curl -fsSL -o /usr/local/bin/minio https://dl.min.io/server/minio/release/linux-${arch}/minio && \
    curl -fsSL -o /usr/local/bin/mc https://dl.min.io/client/mc/release/linux-${arch}/mc && \
    chmod +x /usr/local/bin/minio /usr/local/bin/mc

# ---- Prepare directories ----
RUN mkdir -p /data/mysql /data/redis /data/minio /data/logs \
    /var/run/mysqld /var/run/redis /app \
    /usr/share/nginx/html/admin \
    /usr/share/nginx/html/h5 \
    && chown mysql:mysql /var/run/mysqld /data/mysql \
    && chown redis:redis /var/run/redis /data/redis \
    && rm -rf /var/lib/mysql/*

# ---- Copy built artifacts ----
COPY --from=server-builder /build/base/target/*.jar /app/app.jar
COPY --from=web-builder /app/apps/web-admin/dist /usr/share/nginx/html/admin
COPY --from=h5-builder /app/dist/build/h5 /usr/share/nginx/html/h5

# ---- Copy config files ----
COPY docker/mysql/init.sql /docker-entrypoint-initdb.d/init.sql
COPY docker/all-in-one/supervisord.conf /etc/supervisor/supervisord.conf
COPY docker/all-in-one/nginx.conf /etc/nginx/nginx.conf
COPY docker/all-in-one/entrypoint.sh /entrypoint.sh
COPY docker/all-in-one/start-server.sh /app/start-server.sh

RUN chmod +x /entrypoint.sh /app/start-server.sh

# ---- Default environment ----
ENV MYSQL_PASSWORD=triflow123
ENV MYSQL_DATABASE=triflow
ENV REDIS_PASSWORD=triflow123
ENV MINIO_ACCESS_KEY=triflow
ENV MINIO_SECRET_KEY=triflow123
ENV MINIO_BUCKET=triflow
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 7200

ENTRYPOINT ["/entrypoint.sh"]
