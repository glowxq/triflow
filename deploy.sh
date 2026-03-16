#!/bin/bash

#===============================================================================
#  Triflow Docker 一键管理脚本
#  用法: ./deploy.sh <command> [options]
#
#  命令:
#    start     启动所有服务
#    stop      停止所有服务
#    restart   重启所有服务
#    status    查看服务状态
#    logs      查看日志 (可指定服务名)
#    clean     停止并清理所有数据 (危险!)
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

# Docker Compose 命令（兼容新旧版本）
if docker compose version &>/dev/null; then
    COMPOSE="docker compose"
elif command -v docker-compose &>/dev/null; then
    COMPOSE="docker-compose"
else
    COMPOSE=""
fi

#===============================================================================
# 工具函数
#===============================================================================

print_banner() {
    echo -e "${CYAN}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║               Triflow Docker 管理工具                     ║"
    echo "║        三端企业级快速开发脚手架 - 一键启动                 ║"
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

    # 检查 Docker
    if ! command -v docker &>/dev/null; then
        log_error "Docker 未安装"
        echo "  请访问 https://docs.docker.com/get-docker/ 安装 Docker"
        has_error=true
    else
        log_info "Docker 已安装 ($(docker --version | awk '{print $3}' | tr -d ','))"
    fi

    # 检查 Docker Compose
    if [ -z "$COMPOSE" ]; then
        log_error "Docker Compose 未安装"
        echo "  请访问 https://docs.docker.com/compose/install/ 安装"
        has_error=true
    else
        log_info "Docker Compose 已可用"
    fi

    # 检查 Docker 是否正在运行
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
# 配置初始化
#===============================================================================

init_config() {
    if [ ! -f ".env" ]; then
        if [ -f ".env.example" ]; then
            cp .env.example .env
            log_info "已从 .env.example 创建 .env 配置文件"
        else
            log_error "找不到 .env.example 文件"
            exit 1
        fi
    else
        log_info "配置文件 .env 已存在"
    fi
}

#===============================================================================
# 服务管理
#===============================================================================

do_start() {
    print_banner
    echo "正在启动 Triflow 服务..."
    echo ""

    # 环境检测
    check_environment

    # 配置初始化
    init_config

    echo ""
    log_step "正在构建并启动所有服务..."
    echo ""

    $COMPOSE up -d --build

    echo ""
    log_step "等待服务就绪..."

    # 等待各服务健康
    wait_for_service "triflow-mysql" "MySQL" 120
    wait_for_service "triflow-redis" "Redis" 30
    wait_for_service "triflow-minio" "MinIO" 30
    wait_for_service "triflow-server" "Server" 300
    wait_for_service "triflow-web" "Web" 60

    echo ""
    print_success
}

wait_for_service() {
    local container=$1
    local name=$2
    local timeout=$3
    local elapsed=0

    while [ $elapsed -lt $timeout ]; do
        # 检查容器是否存在
        if ! docker inspect "$container" &>/dev/null; then
            sleep 3
            elapsed=$((elapsed + 3))
            continue
        fi

        # 检查是否有 healthcheck
        local has_health
        has_health=$(docker inspect --format='{{if .State.Health}}true{{else}}false{{end}}' "$container" 2>/dev/null || echo "false")

        if [ "$has_health" = "true" ]; then
            local status
            status=$(docker inspect --format='{{.State.Health.Status}}' "$container" 2>/dev/null || echo "starting")

            case "$status" in
                "healthy")
                    printf "\r  ${GREEN}[✓]${NC} %-10s 已就绪                    \n" "$name"
                    return 0
                    ;;
                "unhealthy")
                    printf "\r  ${RED}[✗]${NC} %-10s 启动失败                    \n" "$name"
                    echo "     查看日志: ./deploy.sh logs $(echo "$container" | sed 's/triflow-//')"
                    return 1
                    ;;
            esac
        else
            # 没有 healthcheck 的容器，运行中即视为就绪
            local running
            running=$(docker inspect --format='{{.State.Running}}' "$container" 2>/dev/null || echo "false")
            if [ "$running" = "true" ]; then
                printf "\r  ${GREEN}[✓]${NC} %-10s 已就绪                    \n" "$name"
                return 0
            fi
        fi

        sleep 3
        elapsed=$((elapsed + 3))
        printf "\r  ${BLUE}[→]${NC} 等待 %-10s %3ds / %ds" "$name" "$elapsed" "$timeout"
    done

    printf "\r  ${YELLOW}[!]${NC} %-10s 启动超时 (%ds)，可能仍在初始化中    \n" "$name" "$timeout"
    echo "     查看日志: ./deploy.sh logs $(echo "$container" | sed 's/triflow-//')"
}

print_success() {
    # 读取端口配置
    source .env 2>/dev/null || true
    local web_port=${WEB_PORT:-7200}
    local server_port=${SERVER_PORT:-7100}
    local minio_console_port=${MINIO_CONSOLE_PORT:-9001}

    echo -e "${GREEN}"
    echo "══════════════════════════════════════════════════════════════"
    echo "  所有服务已启动！"
    echo ""
    echo "  Web 管理后台:   http://localhost:${web_port}"
    echo "  后端 API:       http://localhost:${server_port}"
    echo "  API 文档:       http://localhost:${server_port}/doc.html"
    echo "  MinIO 控制台:   http://localhost:${minio_console_port}"
    echo ""
    echo "  默认管理员账号: root / root"
    echo "  MinIO 账号:     triflow / triflow123"
    echo "══════════════════════════════════════════════════════════════"
    echo -e "${NC}"
}

do_stop() {
    log_step "正在停止所有服务..."
    $COMPOSE down
    log_info "所有服务已停止"
}

do_restart() {
    log_step "正在重启所有服务..."
    $COMPOSE restart
    log_info "所有服务已重启"
}

do_status() {
    echo "服务状态:"
    echo ""
    $COMPOSE ps
}

do_logs() {
    local service=${1:-}
    if [ -n "$service" ]; then
        $COMPOSE logs -f "$service"
    else
        $COMPOSE logs -f
    fi
}

do_clean() {
    echo -e "${RED}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║  ⚠️  警告: 此操作将删除所有数据！                         ║"
    echo "║  包括: 数据库、Redis 缓存、MinIO 文件                     ║"
    echo "║  此操作不可逆！                                           ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"

    read -rp "确认要清理所有数据吗？请输入 'yes' 确认: " confirm
    if [ "$confirm" = "yes" ]; then
        log_step "正在停止服务并清理数据卷..."
        $COMPOSE down -v
        log_info "所有服务和数据已清理"
    else
        log_info "操作已取消"
    fi
}

#===============================================================================
# 帮助信息
#===============================================================================

print_help() {
    echo "Triflow Docker 管理工具"
    echo ""
    echo "用法: ./deploy.sh <command> [options]"
    echo ""
    echo "命令:"
    echo "  start     启动所有服务 (首次运行自动初始化)"
    echo "  stop      停止所有服务"
    echo "  restart   重启所有服务"
    echo "  status    查看服务状态"
    echo "  logs      查看日志 (./deploy.sh logs [mysql|redis|minio|server|web])"
    echo "  clean     停止并清理所有数据 (危险操作)"
    echo "  help      显示此帮助信息"
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
