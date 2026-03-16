#!/bin/bash

#===============================================================================
#  Triflow All-in-One 部署脚本
#  用法: ./deploy-all-in-one.sh <command>
#
#  将 MySQL + Redis + MinIO + 后端 + Web + H5 全部打包到单个容器中
#  适用于快速体验、演示、个人部署
#
#  命令:
#    start     构建并启动容器
#    stop      停止容器
#    restart   重启容器
#    status    查看容器状态
#    logs      查看日志 (可指定: mysql|redis|minio|server|nginx)
#    clean     停止并清理所有数据 (危险!)
#    build     仅构建镜像不启动
#===============================================================================

set -euo pipefail

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_ROOT"

# 常量
IMAGE_NAME="triflow-aio"
CONTAINER_NAME="triflow-aio"
VOLUME_NAME="triflow-aio-data"
DEFAULT_PORT=7200

#===============================================================================
# 工具函数
#===============================================================================

print_banner() {
    echo -e "${CYAN}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║          Triflow All-in-One 部署工具                      ║"
    echo "║   MySQL + Redis + MinIO + Backend + Web + H5 单容器部署   ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

log_info()  { echo -e "${GREEN}[✓]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
log_error() { echo -e "${RED}[✗]${NC} $1"; }
log_step()  { echo -e "${BLUE}[→]${NC} $1"; }

#===============================================================================
# 环境检测
#===============================================================================

check_environment() {
    local has_error=false

    if ! command -v docker &>/dev/null; then
        log_error "Docker 未安装"
        echo "  请访问 https://docs.docker.com/get-docker/ 安装 Docker"
        has_error=true
    else
        log_info "Docker 已安装 ($(docker --version | awk '{print $3}' | tr -d ','))"
    fi

    if ! docker info &>/dev/null; then
        log_error "Docker 引擎未运行"
        echo "  请启动 Docker Desktop 或 Docker 服务"
        has_error=true
    else
        log_info "Docker 引擎正在运行"
    fi

    if [ "$has_error" = true ]; then
        echo ""
        log_error "环境检查未通过，请解决上述问题后重试"
        exit 1
    fi
}

#===============================================================================
# 镜像构建
#===============================================================================

do_build() {
    log_step "正在构建 All-in-One 镜像 (首次约 5-10 分钟)..."
    echo ""

    docker build \
        -f docker/all-in-one/Dockerfile \
        -t "${IMAGE_NAME}:latest" \
        .

    echo ""
    log_info "镜像构建完成: ${IMAGE_NAME}:latest"
}

#===============================================================================
# 服务管理
#===============================================================================

do_start() {
    print_banner
    echo "正在启动 Triflow All-in-One 容器..."
    echo ""

    check_environment

    # 读取端口配置
    local port=${TRIFLOW_PORT:-$DEFAULT_PORT}

    # 检查容器是否已存在
    if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        local state
        state=$(docker inspect --format='{{.State.Running}}' "$CONTAINER_NAME" 2>/dev/null || echo "false")
        if [ "$state" = "true" ]; then
            log_info "容器已在运行中"
            print_success "$port"
            return
        else
            log_step "启动已停止的容器..."
            docker start "$CONTAINER_NAME"
            wait_for_ready "$port"
            print_success "$port"
            return
        fi
    fi

    # 检查镜像是否存在，不存在则构建
    if ! docker image inspect "${IMAGE_NAME}:latest" &>/dev/null; then
        do_build
        echo ""
    fi

    # 创建数据卷
    docker volume create "$VOLUME_NAME" &>/dev/null || true

    # 启动容器
    log_step "正在启动容器..."
    docker run -d \
        --name "$CONTAINER_NAME" \
        -p "${port}:7200" \
        -v "${VOLUME_NAME}:/data" \
        -e MYSQL_PASSWORD="${MYSQL_PASSWORD:-triflow123}" \
        -e MYSQL_DATABASE="${MYSQL_DATABASE:-triflow}" \
        -e REDIS_PASSWORD="${REDIS_PASSWORD:-triflow123}" \
        -e MINIO_ACCESS_KEY="${MINIO_ACCESS_KEY:-triflow}" \
        -e MINIO_SECRET_KEY="${MINIO_SECRET_KEY:-triflow123}" \
        -e MINIO_BUCKET="${MINIO_BUCKET:-triflow}" \
        -e JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m}" \
        --restart unless-stopped \
        "${IMAGE_NAME}:latest"

    wait_for_ready "$port"
    print_success "$port"
}

wait_for_ready() {
    local port=$1
    local timeout=180
    local elapsed=0

    echo ""
    while [ $elapsed -lt $timeout ]; do
        # 检查 Nginx 是否响应
        if curl -sf "http://localhost:${port}" >/dev/null 2>&1; then
            # 检查后端 API 是否可用
            if curl -sf "http://localhost:${port}/api/base/client/auth/login" -X POST \
                -H "Content-Type: application/json" -d '{}' >/dev/null 2>&1; then
                echo ""
                return 0
            fi
        fi

        sleep 3
        elapsed=$((elapsed + 3))
        printf "\r  ${BLUE}[→]${NC} 等待服务就绪 %3ds / %ds (首次启动需要初始化数据库)" "$elapsed" "$timeout"
    done

    echo ""
    log_warn "等待超时，服务可能仍在初始化中"
    echo "     查看日志: ./deploy-all-in-one.sh logs"
}

print_success() {
    local port=$1

    echo -e "${GREEN}"
    echo "══════════════════════════════════════════════════════════════"
    echo "  Triflow All-in-One 已启动！"
    echo ""
    echo "  Web 管理后台:   http://localhost:${port}"
    echo "  H5 移动端:      http://localhost:${port}/h5/"
    echo "  后端 API:       http://localhost:${port}/api/"
    echo "  API 文档:       http://localhost:${port}/api/doc.html"
    echo ""
    echo "  默认管理员账号: root / root"
    echo "══════════════════════════════════════════════════════════════"
    echo -e "${NC}"
}

do_stop() {
    log_step "正在停止容器..."
    docker stop "$CONTAINER_NAME" 2>/dev/null || log_warn "容器未在运行"
    log_info "容器已停止"
}

do_restart() {
    log_step "正在重启容器..."
    docker restart "$CONTAINER_NAME" 2>/dev/null || {
        log_warn "容器不存在，执行完整启动"
        do_start
        return
    }
    log_info "容器已重启"
}

do_status() {
    echo "容器状态:"
    echo ""
    if docker ps -a --filter "name=${CONTAINER_NAME}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -q "$CONTAINER_NAME"; then
        docker ps -a --filter "name=${CONTAINER_NAME}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    else
        log_warn "容器不存在，请先运行 ./deploy-all-in-one.sh start"
    fi
}

do_logs() {
    local service=${1:-}

    if [ -n "$service" ]; then
        # 查看容器内特定服务日志
        case "$service" in
            mysql|redis|minio|nginx)
                docker exec "$CONTAINER_NAME" tail -f "/data/logs/${service}.log" 2>/dev/null || \
                    docker exec "$CONTAINER_NAME" tail -f "/data/logs/${service}-error.log" 2>/dev/null
                ;;
            server)
                docker exec "$CONTAINER_NAME" tail -f /data/logs/server.log 2>/dev/null
                ;;
            *)
                log_error "未知服务: $service (可选: mysql|redis|minio|server|nginx)"
                ;;
        esac
    else
        docker logs -f "$CONTAINER_NAME" 2>/dev/null || log_warn "容器不存在"
    fi
}

do_clean() {
    echo -e "${RED}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║  ⚠️  警告: 此操作将删除容器和所有数据！                    ║"
    echo "║  包括: 数据库、Redis 缓存、MinIO 文件                     ║"
    echo "║  此操作不可逆！                                           ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"

    read -rp "确认要清理所有数据吗？请输入 'yes' 确认: " confirm
    if [ "$confirm" = "yes" ]; then
        log_step "正在停止容器并清理数据..."
        docker stop "$CONTAINER_NAME" 2>/dev/null || true
        docker rm "$CONTAINER_NAME" 2>/dev/null || true
        docker volume rm "$VOLUME_NAME" 2>/dev/null || true
        log_info "容器和数据已清理"
    else
        log_info "操作已取消"
    fi
}

#===============================================================================
# 帮助信息
#===============================================================================

print_help() {
    echo "Triflow All-in-One 部署工具"
    echo ""
    echo "用法: ./deploy-all-in-one.sh <command> [options]"
    echo ""
    echo "命令:"
    echo "  start     构建并启动容器 (首次运行自动初始化)"
    echo "  stop      停止容器"
    echo "  restart   重启容器"
    echo "  status    查看容器状态"
    echo "  logs      查看日志 (./deploy-all-in-one.sh logs [mysql|redis|minio|server|nginx])"
    echo "  clean     停止并清理所有数据 (危险操作)"
    echo "  build     仅构建镜像不启动"
    echo "  help      显示此帮助信息"
    echo ""
    echo "环境变量:"
    echo "  TRIFLOW_PORT       Web 访问端口 (默认: 7200)"
    echo "  MYSQL_PASSWORD     MySQL 密码 (默认: triflow123)"
    echo "  REDIS_PASSWORD     Redis 密码 (默认: triflow123)"
    echo "  MINIO_ACCESS_KEY   MinIO 用户 (默认: triflow)"
    echo "  MINIO_SECRET_KEY   MinIO 密码 (默认: triflow123)"
    echo "  JAVA_OPTS          JVM 参数 (默认: -Xms256m -Xmx512m)"
}

#===============================================================================
# 主入口
#===============================================================================

main() {
    local command=${1:-start}
    shift 2>/dev/null || true

    case "$command" in
        start)   do_start ;;
        stop)    do_stop ;;
        restart) do_restart ;;
        status)  do_status ;;
        logs)    do_logs "$@" ;;
        clean)   do_clean ;;
        build)   check_environment; do_build ;;
        help|-h|--help) print_help ;;
        *)
            log_error "未知命令: $command"
            echo ""
            print_help
            exit 1
            ;;
    esac
}

main "$@"
