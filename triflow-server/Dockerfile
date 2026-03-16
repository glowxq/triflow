# ====== Stage 1: Maven 构建 ======
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /build

# 先复制 pom 文件利用 Docker 层缓存下载依赖
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY common/common-core/pom.xml common/common-core/pom.xml
COPY common/common-security/pom.xml common/common-security/pom.xml
COPY common/common-db-mysql/pom.xml common/common-db-mysql/pom.xml
COPY common/common-db-redis/pom.xml common/common-db-redis/pom.xml
COPY common/common-oss/pom.xml common/common-oss/pom.xml
COPY common/common-sms/pom.xml common/common-sms/pom.xml
COPY common/common-wechat/pom.xml common/common-wechat/pom.xml
COPY common/common-excel/pom.xml common/common-excel/pom.xml
COPY common/common-log/pom.xml common/common-log/pom.xml
COPY common/common-business/pom.xml common/common-business/pom.xml
COPY common/common-ai/pom.xml common/common-ai/pom.xml
COPY dependencies/pom.xml dependencies/pom.xml
COPY business/pom.xml business/pom.xml
COPY demo/pom.xml demo/pom.xml
COPY base/pom.xml base/pom.xml

# 下载依赖（利用 Docker 缓存，源码不变则跳过）
RUN mvn dependency:go-offline -B -DskipTests 2>/dev/null || true

# 复制全部源码
COPY . .

# 构建 (跳过测试和代码格式化)
RUN mvn clean package -DskipTests -pl base -am -B

# ====== Stage 2: 运行 ======
FROM eclipse-temurin:25-jre-alpine

LABEL maintainer="Triflow <https://github.com/glowxq/triflow>"

WORKDIR /app

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

# 从构建阶段复制 JAR
COPY --from=builder /build/base/target/*.jar app.jar

# 暴露端口
EXPOSE 7100

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 启动
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
