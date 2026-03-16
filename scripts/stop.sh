#!/bin/bash

#===============================================================================
#  Triflow 一键关闭脚本
#  支持: macOS / Linux
#  用法: ./stop.sh [services...] [options]
#
#  服务标识符:
#    server      后端服务 (端口 7100)
#    web:admin   Web 管理后台 (端口 7200)
#    app:h5      App H5 版本 (端口 7300)
#    app:mp      App 微信小程序
#
#  示例:
#    ./stop.sh                        # 停止所有服务
#    ./stop.sh server                 # 仅停止后端
#    ./stop.sh server web:admin       # 停止后端 + Web
#===============================================================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

# PID 文件目录
PID_DIR="$PROJECT_ROOT/.pids"

# 端口配置
SERVER_PORT=7100
WEB_PORT=7200
APP_H5_PORT=7300

# 要停止的服务列表
declare -a SERVICES_TO_STOP=()

#===============================================================================
# 工具函数
#===============================================================================

print_banner() {
    echo -e "${CYAN}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║                    🛑 Triflow 关闭脚本                     ║"
    echo "║           Server / Web / App 三端快速开发脚手架            ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

log_service() {
    echo -e "${MAGENTA}[SERVICE]${NC} $1"
}

# 检查端口是否被占用
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0  # 端口被占用
    fi
    return 1  # 端口可用
}

#===============================================================================
# 停止服务函数
#===============================================================================

stop_server() {
    log_service "停止后端服务..."

    local stopped=false

    # 通过 PID 文件停止
    if [ -f "$PID_DIR/server.pid" ]; then
        local pid=$(cat "$PID_DIR/server.pid")
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            log_info "后端服务已停止 (PID: $pid)"
            stopped=true
        fi
        rm -f "$PID_DIR/server.pid"
    fi

    # 强制杀死占用端口的进程
    local pids=$(lsof -ti:$SERVER_PORT 2>/dev/null || true)
    if [ -n "$pids" ]; then
        echo $pids | xargs kill -9 2>/dev/null || true
        log_info "已清理端口 $SERVER_PORT"
        stopped=true
    fi

    if [ "$stopped" = false ]; then
        log_warn "后端服务未在运行"
    fi
}

stop_web_admin() {
    log_service "停止 Web 管理后台..."

    local stopped=false

    # 通过 PID 文件停止
    if [ -f "$PID_DIR/web.pid" ]; then
        local pid=$(cat "$PID_DIR/web.pid")
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            log_info "Web 管理后台已停止 (PID: $pid)"
            stopped=true
        fi
        rm -f "$PID_DIR/web.pid"
    fi

    # 强制杀死占用端口的进程
    local pids=$(lsof -ti:$WEB_PORT 2>/dev/null || true)
    if [ -n "$pids" ]; then
        echo $pids | xargs kill -9 2>/dev/null || true
        log_info "已清理端口 $WEB_PORT"
        stopped=true
    fi

    if [ "$stopped" = false ]; then
        log_warn "Web 管理后台未在运行"
    fi
}

stop_app_h5() {
    log_service "停止 App H5..."

    local stopped=false

    # 通过 PID 文件停止
    if [ -f "$PID_DIR/app-h5.pid" ]; then
        local pid=$(cat "$PID_DIR/app-h5.pid")
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            log_info "App H5 已停止 (PID: $pid)"
            stopped=true
        fi
        rm -f "$PID_DIR/app-h5.pid"
    fi

    # 强制杀死占用端口的进程
    local pids=$(lsof -ti:$APP_H5_PORT 2>/dev/null || true)
    if [ -n "$pids" ]; then
        echo $pids | xargs kill -9 2>/dev/null || true
        log_info "已清理端口 $APP_H5_PORT"
        stopped=true
    fi

    if [ "$stopped" = false ]; then
        log_warn "App H5 未在运行"
    fi
}

stop_app_mp() {
    log_service "停止微信小程序构建..."

    local stopped=false

    # 通过 PID 文件停止
    if [ -f "$PID_DIR/app-mp.pid" ]; then
        local pid=$(cat "$PID_DIR/app-mp.pid")
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            log_info "微信小程序构建已停止 (PID: $pid)"
            stopped=true
        fi
        rm -f "$PID_DIR/app-mp.pid"
    fi

    if [ "$stopped" = false ]; then
        log_warn "微信小程序构建未在运行"
    fi
}

#===============================================================================
# 服务状态
#===============================================================================

show_status() {
    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}                       服务状态                                 ${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo ""

    # 后端服务
    if check_port $SERVER_PORT; then
        echo -e "  server      后端服务:      ${GREEN}● 运行中${NC}  http://localhost:$SERVER_PORT"
    else
        echo -e "  server      后端服务:      ${RED}○ 已停止${NC}"
    fi

    # Web 前端
    if check_port $WEB_PORT; then
        echo -e "  web:admin   Web 管理后台:  ${GREEN}● 运行中${NC}  http://localhost:$WEB_PORT"
    else
        echo -e "  web:admin   Web 管理后台:  ${RED}○ 已停止${NC}"
    fi

    # 移动端 H5
    if check_port $APP_H5_PORT; then
        echo -e "  app:h5      移动端 H5:     ${GREEN}● 运行中${NC}  http://localhost:$APP_H5_PORT"
    else
        echo -e "  app:h5      移动端 H5:     ${RED}○ 已停止${NC}"
    fi

    # 微信小程序
    if [ -f "$PID_DIR/app-mp.pid" ]; then
        local mp_pid=$(cat "$PID_DIR/app-mp.pid")
        if kill -0 $mp_pid 2>/dev/null; then
            echo -e "  app:mp      微信小程序:    ${GREEN}● 构建中${NC}  dist/dev/mp-weixin"
        else
            echo -e "  app:mp      微信小程序:    ${RED}○ 已停止${NC}"
        fi
    else
        echo -e "  app:mp      微信小程序:    ${RED}○ 已停止${NC}"
    fi

    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
}

#===============================================================================
# 帮助信息
#===============================================================================

show_help() {
    echo ""
    echo "Triflow 项目关闭脚本"
    echo ""
    echo "用法: ./stop.sh [services...]"
    echo ""
    echo "服务标识符:"
    echo "  server          后端服务 (端口 7100)"
    echo "  web:admin       Web 管理后台 (端口 7200)"
    echo "  app:h5          App H5 版本 (端口 7300)"
    echo "  app:mp          App 微信小程序"
    echo ""
    echo "特殊命令:"
    echo "  status          查看服务状态"
    echo "  help            显示帮助信息"
    echo ""
    echo "示例:"
    echo "  ./stop.sh                        # 停止所有服务"
    echo "  ./stop.sh server                 # 仅停止后端"
    echo "  ./stop.sh server web:admin       # 停止后端 + Web 管理后台"
    echo "  ./stop.sh app:h5 app:mp          # 停止 App H5 + 微信小程序"
    echo "  ./stop.sh status                 # 查看状态"
    echo ""
}

#===============================================================================
# 解析服务参数
#===============================================================================

parse_services() {
    for arg in "$@"; do
        case $arg in
            server)
                SERVICES_TO_STOP+=("server")
                ;;
            web|web:admin)
                SERVICES_TO_STOP+=("web:admin")
                ;;
            app:h5)
                SERVICES_TO_STOP+=("app:h5")
                ;;
            app:mp|mp)
                SERVICES_TO_STOP+=("app:mp")
                ;;
            # 忽略特殊命令
            status|help|--help|-h) ;;
            *)
                log_warn "未知服务: $arg (已忽略)"
                ;;
        esac
    done
}

#===============================================================================
# 停止选定的服务
#===============================================================================

stop_services() {
    for service in "${SERVICES_TO_STOP[@]}"; do
        case $service in
            server)
                stop_server
                ;;
            web:admin)
                stop_web_admin
                ;;
            app:h5)
                stop_app_h5
                ;;
            app:mp)
                stop_app_mp
                ;;
        esac
    done
}

#===============================================================================
# 停止所有服务
#===============================================================================

stop_all() {
    log_step "停止所有服务..."
    stop_server
    stop_web_admin
    stop_app_h5
    stop_app_mp
    log_info "所有服务已停止"
}

#===============================================================================
# 主函数
#===============================================================================

main() {
    # 处理特殊命令
    case $1 in
        status)
            print_banner
            show_status
            return 0
            ;;
        help|--help|-h)
            print_banner
            show_help
            return 0
            ;;
    esac

    print_banner

    # 解析要停止的服务
    parse_services "$@"

    # 如果没有指定服务，默认停止所有
    if [ ${#SERVICES_TO_STOP[@]} -eq 0 ]; then
        stop_all
    else
        log_step "停止选定服务: ${SERVICES_TO_STOP[*]}"
        stop_services
    fi

    echo ""
    show_status
}

# 运行主函数
main "$@"
