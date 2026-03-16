#!/bin/bash

#===============================================================================
#  Triflow 重启脚本
#  支持: macOS / Linux
#  用法: ./restart.sh [services...] [options]
#  特点: 自动停止现有服务，无需交互确认
#
#  服务标识符:
#    server      后端服务 (端口 7100)
#    web:admin   Web 管理后台 (端口 7200)
#    app:h5      App H5 版本 (端口 7300)
#    app:mp      App 微信小程序 (构建并提示打开开发者工具)
#
#  示例:
#    ./restart.sh                          # 重启所有服务
#    ./restart.sh server                   # 仅重启后端
#    ./restart.sh server web:admin         # 重启后端 + Web
#    ./restart.sh server app:h5 app:mp     # 重启后端 + App H5 + 微信小程序
#===============================================================================

set -e

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
SERVER_DIR="$PROJECT_ROOT/triflow-server"
WEB_DIR="$PROJECT_ROOT/triflow-web"
APP_DIR="$PROJECT_ROOT/triflow-app"

# PID 文件目录
PID_DIR="$PROJECT_ROOT/.pids"
mkdir -p "$PID_DIR"

# 日志目录
LOG_DIR="$PROJECT_ROOT/.logs"
mkdir -p "$LOG_DIR"

# 端口配置
SERVER_PORT=7100
WEB_PORT=7200
APP_H5_PORT=7300

# 默认配置
SERVER_PROFILE="local"
SHOW_LOGS=false
FORCE_BUILD=false
FORCE_INSTALL=false

# Java 25 环境配置
JAVA_25_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"

# 要重启的服务列表
declare -a SERVICES_TO_RESTART=()

#===============================================================================
# 工具函数
#===============================================================================

print_banner() {
    echo -e "${CYAN}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║                    🔄 Triflow 重启脚本                     ║"
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

check_command() {
    if ! command -v "$1" &> /dev/null; then
        log_error "$1 未安装，请先安装后再运行"
        return 1
    fi
    return 0
}

check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0  # 端口被占用
    fi
    return 1  # 端口可用
}

should_rebuild_server() {
    local jar_file="$1"

    if [ ! -f "$jar_file" ]; then
        return 0
    fi

    if find "$SERVER_DIR" -type f -newer "$jar_file" \
        -not -path "*/target/*" \
        -not -path "*/.git/*" \
        -not -path "*/.idea/*" \
        -not -path "*/.logs/*" \
        -not -path "*/.pids/*" \
        -print -quit | grep -q .; then
        return 0
    fi

    return 1
}

wait_for_service() {
    local port=$1
    local name=$2
    local timeout=${3:-60}
    local count=0

    log_info "等待 $name 启动 (端口: $port)..."
    while ! check_port $port; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge $timeout ]; then
            log_error "$name 启动超时"
            return 1
        fi
        printf "."
    done
    echo ""
    log_info "$name 启动成功!"
    return 0
}

# 强制停止占用端口的进程
force_stop_port() {
    local port=$1
    local pids=$(lsof -ti:$port 2>/dev/null || true)
    if [ -n "$pids" ]; then
        echo $pids | xargs kill -9 2>/dev/null || true
        log_info "已清理端口 $port"
        sleep 1
    fi
}

#===============================================================================
# 停止服务函数 (无交互)
#===============================================================================

stop_server() {
    log_service "停止后端服务..."

    if [ -f "$PID_DIR/server.pid" ]; then
        local pid=$(cat "$PID_DIR/server.pid")
        kill $pid 2>/dev/null && log_info "后端服务已停止 (PID: $pid)"
        rm -f "$PID_DIR/server.pid"
    fi

    force_stop_port $SERVER_PORT
}

stop_web_admin() {
    log_service "停止 Web 管理后台..."

    if [ -f "$PID_DIR/web.pid" ]; then
        local pid=$(cat "$PID_DIR/web.pid")
        kill $pid 2>/dev/null && log_info "Web 管理后台已停止 (PID: $pid)"
        rm -f "$PID_DIR/web.pid"
    fi

    force_stop_port $WEB_PORT
}

stop_app_h5() {
    log_service "停止 App H5..."

    if [ -f "$PID_DIR/app-h5.pid" ]; then
        local pid=$(cat "$PID_DIR/app-h5.pid")
        kill $pid 2>/dev/null && log_info "App H5 已停止 (PID: $pid)"
        rm -f "$PID_DIR/app-h5.pid"
    fi

    force_stop_port $APP_H5_PORT
}

stop_app_mp() {
    log_service "停止微信小程序构建..."

    if [ -f "$PID_DIR/app-mp.pid" ]; then
        local pid=$(cat "$PID_DIR/app-mp.pid")
        kill $pid 2>/dev/null && log_info "微信小程序构建已停止 (PID: $pid)"
        rm -f "$PID_DIR/app-mp.pid"
    fi
}

#===============================================================================
# 启动服务函数
#===============================================================================

start_server() {
    log_service "启动后端服务 (triflow-server)..."

    # 设置 Java 25 环境
    if [ -d "$JAVA_25_HOME" ]; then
        export JAVA_HOME="$JAVA_25_HOME"
        export PATH="$JAVA_HOME/bin:$PATH"
        log_info "使用 Java 25: $JAVA_HOME"
    else
        log_warn "未找到 Java 25 ($JAVA_25_HOME)，使用系统默认 Java"
    fi

    if ! check_command java; then
        return 1
    fi

    local java_version=$(java -version 2>&1 | head -n 1)
    log_info "Java 版本: $java_version"

    cd "$SERVER_DIR"

    local jar_file="$SERVER_DIR/base/target/base-1.0.0.jar"

    # 检查是否需要编译
    if [ "$FORCE_BUILD" = true ] || should_rebuild_server "$jar_file"; then
        log_info "编译后端项目..."
        mvn clean package -DskipTests -q || {
            log_error "编译失败"
            return 1
        }
        log_info "编译完成"
    fi

    if [ ! -f "$jar_file" ]; then
        log_error "未找到 JAR 文件: $jar_file"
        return 1
    fi

    log_info "使用 Profile: $SERVER_PROFILE"

    nohup java -jar "$jar_file" --spring.profiles.active=$SERVER_PROFILE \
        > "$LOG_DIR/server.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/server.pid"

    if wait_for_service $SERVER_PORT "后端服务" 120; then
        log_info "后端服务地址: http://localhost:$SERVER_PORT"
        log_info "API 文档地址: http://localhost:$SERVER_PORT/doc.html"
        return 0
    fi
    return 1
}

start_web_admin() {
    log_service "启动 Web 管理后台 (triflow-web)..."

    if ! check_command node; then
        return 1
    fi

    if ! check_command pnpm; then
        log_error "pnpm 未安装，请运行: npm install -g pnpm"
        return 1
    fi

    cd "$WEB_DIR"

    # 检查依赖
    if [ ! -d "node_modules" ] || [ "$FORCE_INSTALL" = true ]; then
        log_info "安装前端依赖..."
        pnpm install || {
            log_error "依赖安装失败"
            return 1
        }
    fi

    log_info "启动后台管理系统..."
    nohup pnpm dev:admin > "$LOG_DIR/web.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/web.pid"

    if wait_for_service $WEB_PORT "Web 管理后台" 60; then
        log_info "后台管理地址: http://localhost:$WEB_PORT"
        return 0
    fi
    return 1
}

start_app_h5() {
    log_service "启动 App H5 (triflow-app)..."

    if ! check_command node; then
        return 1
    fi

    if ! check_command pnpm; then
        log_error "pnpm 未安装"
        return 1
    fi

    cd "$APP_DIR"

    if [ ! -d "node_modules" ] || [ "$FORCE_INSTALL" = true ]; then
        log_info "安装移动端依赖..."
        pnpm install || {
            log_error "依赖安装失败"
            return 1
        }
    fi

    log_info "启动 H5 开发服务器..."
    nohup pnpm dev:h5 > "$LOG_DIR/app-h5.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/app-h5.pid"

    sleep 5
    log_info "H5 地址: http://localhost:$APP_H5_PORT"
    return 0
}

start_app_mp() {
    log_service "启动微信小程序构建 (triflow-app)..."

    if ! check_command node; then
        return 1
    fi

    if ! check_command pnpm; then
        log_error "pnpm 未安装"
        return 1
    fi

    cd "$APP_DIR"

    if [ ! -d "node_modules" ] || [ "$FORCE_INSTALL" = true ]; then
        log_info "安装移动端依赖..."
        pnpm install || {
            log_error "依赖安装失败"
            return 1
        }
    fi

    log_info "启动微信小程序开发构建 (监听模式)..."
    nohup pnpm dev:mp > "$LOG_DIR/app-mp.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/app-mp.pid"

    sleep 3
    log_info "微信小程序构建已启动 (PID: $pid)"
    log_info "请使用微信开发者工具打开: $APP_DIR/dist/dev/mp-weixin"
    return 0
}

#===============================================================================
# 状态显示
#===============================================================================

show_status() {
    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}                       服务状态                                 ${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo ""

    if check_port $SERVER_PORT; then
        echo -e "  server      后端服务:      ${GREEN}● 运行中${NC}  http://localhost:$SERVER_PORT"
    else
        echo -e "  server      后端服务:      ${RED}○ 未运行${NC}"
    fi

    if check_port $WEB_PORT; then
        echo -e "  web:admin   Web 管理后台:  ${GREEN}● 运行中${NC}  http://localhost:$WEB_PORT"
    else
        echo -e "  web:admin   Web 管理后台:  ${RED}○ 未运行${NC}"
    fi

    if check_port $APP_H5_PORT; then
        echo -e "  app:h5      移动端 H5:     ${GREEN}● 运行中${NC}  http://localhost:$APP_H5_PORT"
    else
        echo -e "  app:h5      移动端 H5:     ${RED}○ 未运行${NC}"
    fi

    if [ -f "$PID_DIR/app-mp.pid" ]; then
        local mp_pid=$(cat "$PID_DIR/app-mp.pid")
        if kill -0 $mp_pid 2>/dev/null; then
            echo -e "  app:mp      微信小程序:    ${GREEN}● 构建中${NC}  dist/dev/mp-weixin"
        else
            echo -e "  app:mp      微信小程序:    ${RED}○ 未运行${NC}"
        fi
    else
        echo -e "  app:mp      微信小程序:    ${RED}○ 未运行${NC}"
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
    echo "Triflow 重启脚本 (自动停止现有服务，无需交互确认)"
    echo ""
    echo "用法: ./restart.sh [services...] [options]"
    echo ""
    echo "服务标识符:"
    echo "  server          后端服务 (端口 7100)"
    echo "  web:admin       Web 管理后台 (端口 7200)"
    echo "  app:h5          App H5 版本 (端口 7300)"
    echo "  app:mp          App 微信小程序"
    echo ""
    echo "选项:"
    echo "  --build         强制重新编译后端"
    echo "  --install       强制重新安装前端依赖"
    echo "  --profile=xxx   指定后端 Profile (默认: local)"
    echo "  --logs          重启后显示后端日志"
    echo ""
    echo "说明:"
    echo "  默认会在检测到后端源码变更时自动重编译"
    echo ""
    echo "示例:"
    echo "  ./restart.sh                          # 重启所有服务"
    echo "  ./restart.sh server                   # 仅重启后端"
    echo "  ./restart.sh server web:admin         # 重启后端 + Web"
    echo "  ./restart.sh server app:h5 app:mp     # 重启后端 + App H5 + 微信小程序"
    echo "  ./restart.sh --build server           # 重新编译并重启后端"
    echo "  ./restart.sh --logs                   # 重启后查看日志"
    echo ""
}

#===============================================================================
# 解析服务参数
#===============================================================================

parse_services() {
    for arg in "$@"; do
        case $arg in
            server)
                SERVICES_TO_RESTART+=("server")
                ;;
            web|web:admin)
                SERVICES_TO_RESTART+=("web:admin")
                ;;
            app:h5)
                SERVICES_TO_RESTART+=("app:h5")
                ;;
            app:mp|mp)
                SERVICES_TO_RESTART+=("app:mp")
                ;;
            # 忽略选项参数
            --*) ;;
            # 忽略特殊命令
            help) ;;
            *)
                log_warn "未知服务: $arg (已忽略)"
                ;;
        esac
    done
}

#===============================================================================
# 重启选定的服务
#===============================================================================

restart_services() {
    local has_error=false

    for service in "${SERVICES_TO_RESTART[@]}"; do
        case $service in
            server)
                stop_server
                start_server || has_error=true
                ;;
            web:admin)
                stop_web_admin
                start_web_admin || has_error=true
                ;;
            app:h5)
                stop_app_h5
                start_app_h5 || has_error=true
                ;;
            app:mp)
                stop_app_mp
                start_app_mp || has_error=true
                ;;
        esac
        echo ""
    done

    if [ "$has_error" = true ]; then
        log_warn "部分服务重启失败"
    fi
}

#===============================================================================
# 重启所有服务
#===============================================================================

restart_all() {
    log_step "重启所有服务 (server + web:admin + app:h5 + app:mp)..."
    echo ""

    stop_server
    stop_web_admin
    stop_app_h5
    stop_app_mp

    echo ""

    start_server
    echo ""
    start_web_admin
    echo ""
    start_app_h5
    echo ""
    start_app_mp
}

#===============================================================================
# 主函数
#===============================================================================

main() {
    # 解析选项
    for arg in "$@"; do
        case $arg in
            --profile=*)
                SERVER_PROFILE="${arg#*=}"
                ;;
            --build)
                FORCE_BUILD=true
                ;;
            --install)
                FORCE_INSTALL=true
                ;;
            --logs)
                SHOW_LOGS=true
                ;;
        esac
    done

    # 处理帮助命令
    case $1 in
        help|--help|-h)
            print_banner
            show_help
            return 0
            ;;
    esac

    print_banner

    # 解析要重启的服务
    parse_services "$@"

    # 如果没有指定服务，默认重启所有
    if [ ${#SERVICES_TO_RESTART[@]} -eq 0 ]; then
        restart_all
    else
        log_step "重启选定服务: ${SERVICES_TO_RESTART[*]}"
        echo ""
        restart_services
    fi

    show_status

    # 启动后显示日志
    if [ "$SHOW_LOGS" = true ]; then
        echo ""
        log_info "按 Ctrl+C 退出日志查看 (服务将继续运行)"
        echo ""
        echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
        echo -e "${CYAN}                     后端服务日志                              ${NC}"
        echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
        echo ""
        tail -f "$LOG_DIR/server.log"
    fi
}

main "$@"
