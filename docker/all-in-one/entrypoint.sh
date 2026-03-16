#!/bin/bash
set -e

echo "╔════════════════════════════════════════════════╗"
echo "║       Triflow All-in-One Container             ║"
echo "╚════════════════════════════════════════════════╝"

# ============================================================
# MySQL 初始化 (仅首次启动)
# ============================================================
if [ ! -d "/data/mysql/mysql" ]; then
    echo "[→] Initializing MySQL..."

    chown -R mysql:mysql /data/mysql
    mysqld --initialize-insecure --user=mysql --datadir=/data/mysql

    # 临时启动 MySQL 执行初始化
    mysqld --user=mysql --datadir=/data/mysql \
           --socket=/var/run/mysqld/mysqld.sock \
           --skip-networking --skip-grant-tables &
    MYSQL_PID=$!

    # 等待 MySQL 就绪
    for i in $(seq 1 30); do
        if mysqladmin ping --socket=/var/run/mysqld/mysqld.sock 2>/dev/null; then
            break
        fi
        sleep 1
    done

    # 创建数据库、设置密码
    mysql --socket=/var/run/mysqld/mysqld.sock <<-EOSQL
        FLUSH PRIVILEGES;
        ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_PASSWORD}';
        CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED BY '${MYSQL_PASSWORD}';
        GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;
        CREATE DATABASE IF NOT EXISTS \`${MYSQL_DATABASE}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
        FLUSH PRIVILEGES;
EOSQL

    # 导入初始化数据
    mysql -u root -p"${MYSQL_PASSWORD}" --socket=/var/run/mysqld/mysqld.sock "${MYSQL_DATABASE}" < /docker-entrypoint-initdb.d/init.sql
    echo "[✓] Database initialized with init.sql"

    # 停止临时 MySQL
    mysqladmin -u root -p"${MYSQL_PASSWORD}" --socket=/var/run/mysqld/mysqld.sock shutdown
    wait $MYSQL_PID 2>/dev/null || true

    echo "[✓] MySQL initialization complete"
else
    echo "[✓] MySQL data directory exists, skipping initialization"
fi

# ============================================================
# Redis 数据目录
# ============================================================
chown -R redis:redis /data/redis

echo "[→] Starting all services via Supervisor..."

# 启动 supervisord (前台运行)
exec /usr/bin/supervisord -n -c /etc/supervisor/supervisord.conf
