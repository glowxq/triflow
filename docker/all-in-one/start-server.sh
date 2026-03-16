#!/bin/bash
# 等待 MySQL、Redis、MinIO 就绪后启动 Java 后端

echo "[Server] Waiting for dependencies..."

# 等待 MySQL
until mysqladmin ping -u root -p"${MYSQL_PASSWORD}" -h 127.0.0.1 --silent 2>/dev/null; do
    sleep 2
done
echo "[Server] MySQL is ready"

# 等待 Redis
until redis-cli -a "${REDIS_PASSWORD}" -h 127.0.0.1 ping 2>/dev/null | grep -q PONG; do
    sleep 1
done
echo "[Server] Redis is ready"

# 等待 MinIO
until curl -sf http://127.0.0.1:9000/minio/health/live >/dev/null 2>&1; do
    sleep 2
done
echo "[Server] MinIO is ready"

# 初始化 MinIO Bucket (首次)
if ! mc alias list triflow >/dev/null 2>&1; then
    mc alias set triflow http://127.0.0.1:9000 "${MINIO_ACCESS_KEY}" "${MINIO_SECRET_KEY}" 2>/dev/null
    mc mb triflow/"${MINIO_BUCKET}" --ignore-existing 2>/dev/null
    mc anonymous set download triflow/"${MINIO_BUCKET}" 2>/dev/null
    echo "[Server] MinIO bucket '${MINIO_BUCKET}' initialized"
fi

echo "[Server] All dependencies ready, starting Java backend..."

exec java ${JAVA_OPTS} \
    -Dspring.profiles.active=local \
    -Dspring.datasource.url="jdbc:mysql://127.0.0.1:3306/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true" \
    -Dspring.datasource.username=root \
    -Dspring.datasource.password="${MYSQL_PASSWORD}" \
    -Dspring.data.redis.host=127.0.0.1 \
    -Dspring.data.redis.port=6379 \
    -Dspring.data.redis.password="${REDIS_PASSWORD}" \
    -Dspring.data.redis.database=6 \
    -Dflyway.framework.enabled=false \
    -jar /app/app.jar
