#!/bin/bash

#===============================================================================
#  Triflow 一键启动脚本
#  支持: macOS / Linux
#  用法: ./start.sh [services...] [options]
#
#  服务标识符:
#    server      后端服务 (端口 7100)
#    web:admin   Web 管理后台 (端口 7200)
#    app:h5      App H5 版本 (端口 7300)
#    app:mp      App 微信小程序 (构建并提示打开开发者工具)
#
#  示例:
#    ./start.sh                           # 启动所有服务 (server + web:admin + app:h5 + app:mp)
#    ./start.sh server                    # 仅启动后端
#    ./start.sh server web:admin          # 启动后端 + Web
#    ./start.sh server app:h5 app:mp      # 启动后端 + App H5 + 微信小程序
#===============================================================================

# 不使用 set -e，改为在每个关键命令后手动处理错误
# 这样可以避免因为单个服务启动失败而中断整个脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SERVER_DIR="$PROJECT_ROOT/triflow-server"
WEB_DIR="$PROJECT_ROOT/triflow-web"
APP_DIR="$PROJECT_ROOT/triflow-app"

# 运行时目录（统一存放 PID 和日志）
RUN_DIR="$PROJECT_ROOT/.run"
PID_DIR="$RUN_DIR/pids"
LOG_DIR="$RUN_DIR/logs"
mkdir -p "$PID_DIR" "$LOG_DIR"

# 端口配置
SERVER_PORT=7100
WEB_PORT=7200
APP_H5_PORT=7300

# 默认配置
SERVER_PROFILE="local"
SHOW_LOGS=false
NO_LOGS=false
FORCE_BUILD=false
FORCE_INSTALL=false

# Java 25 环境配置 (根据实际安装路径调整)
JAVA_25_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"

# 要启动的服务列表
declare -a SERVICES_TO_START=()

#===============================================================================
# 工具函数
#===============================================================================

print_banner() {
    echo -e "${CYAN}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║                    🚀 Triflow 启动脚本                     ║"
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

# 检查命令是否存在
check_command() {
    if ! command -v "$1" &> /dev/null; then
        log_error "$1 未安装，请先安装后再运行"
        return 1
    fi
    return 0
}

# 检查端口是否被占用
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0  # 端口被占用
    fi
    return 1  # 端口可用
}

# 等待服务启动
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
# 后端服务管理
#===============================================================================

start_server() {
    log_service "启动后端服务 (triflow-server)..."

    # 保存当前目录
    local current_dir=$(pwd)

    # 设置 Java 25 环境
    if [ -d "$JAVA_25_HOME" ]; then
        export JAVA_HOME="$JAVA_25_HOME"
        export PATH="$JAVA_HOME/bin:$PATH"
        log_info "使用 Java 25: $JAVA_HOME"
    else
        log_warn "未找到 Java 25 ($JAVA_25_HOME)，使用系统默认 Java"
    fi

    # 检查依赖
    if ! check_command java; then
        cd "$current_dir"
        return 1
    fi

    # 验证 Java 版本
    local java_version=$(java -version 2>&1 | head -n 1)
    log_info "Java 版本: $java_version"

    # 检查端口
    if check_port $SERVER_PORT; then
        log_warn "端口 $SERVER_PORT 已被占用，正在清理..."
        force_stop_port $SERVER_PORT
    fi

    cd "$SERVER_DIR" || {
        log_error "无法进入目录: $SERVER_DIR"
        cd "$current_dir"
        return 1
    }

    # 每次启动都自动编译后端
    local jar_file="$SERVER_DIR/base/target/base-1.0.0.jar"
    log_info "编译后端项目 (生成可执行 JAR)..."
    if ! mvn clean package -DskipTests -q; then
        log_error "编译失败，请检查 Maven 配置和代码"
        cd "$current_dir"
        return 1
    fi
    log_info "编译完成: $jar_file"

    if [ ! -f "$jar_file" ]; then
        log_error "未找到 JAR 文件: $jar_file"
        log_info "请先编译项目或使用 --build 选项"
        cd "$current_dir"
        return 1
    fi

    # 启动服务
    log_info "使用 Profile: $SERVER_PROFILE"
    nohup java -jar "$jar_file" --spring.profiles.active=$SERVER_PROFILE \
        > "$LOG_DIR/server.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/server.pid"

    # 恢复目录
    cd "$current_dir"

    # 等待启动
    if wait_for_service $SERVER_PORT "后端服务" 120; then
        log_info "后端服务地址: http://localhost:$SERVER_PORT"
        log_info "API 文档地址: http://localhost:$SERVER_PORT/doc.html"
        return 0
    fi
    return 1
}

#===============================================================================
# Web 前端管理
#===============================================================================

start_web_admin() {
    log_service "启动 Web 管理后台 (triflow-web)..."

    # 保存当前目录
    local current_dir=$(pwd)

    # 检查依赖
    if ! check_command node; then
        return 1
    fi

    if ! check_command pnpm; then
        log_error "pnpm 未安装，请运行: npm install -g pnpm"
        return 1
    fi

    # 检查端口
    if check_port $WEB_PORT; then
        log_warn "端口 $WEB_PORT 已被占用，正在清理..."
        force_stop_port $WEB_PORT
    fi

    cd "$WEB_DIR" || {
        log_error "无法进入目录: $WEB_DIR"
        cd "$current_dir"
        return 1
    }

    # 首次运行需要安装依赖
    if [ ! -d "node_modules" ] || [ "$FORCE_INSTALL" = true ]; then
        log_info "安装前端依赖..."
        if ! pnpm install; then
            log_error "依赖安装失败"
            cd "$current_dir"
            return 1
        fi
    fi

    # 启动开发服务器
    log_info "启动后台管理系统..."
    nohup pnpm dev:admin > "$LOG_DIR/web.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/web.pid"

    # 恢复目录
    cd "$current_dir"

    # 等待启动
    if wait_for_service $WEB_PORT "Web 管理后台" 60; then
        log_info "后台管理地址: http://localhost:$WEB_PORT"
        return 0
    fi
    return 1
}

#===============================================================================
# 移动端 H5 管理
#===============================================================================

start_app_h5() {
    log_service "启动移动端 H5 (triflow-app)..."

    # 保存当前目录
    local current_dir=$(pwd)

    # 检查依赖
    if ! check_command node; then
        return 1
    fi

    if ! check_command pnpm; then
        log_error "pnpm 未安装，请运行: npm install -g pnpm"
        return 1
    fi

    # 检查端口
    if check_port $APP_H5_PORT; then
        log_warn "端口 $APP_H5_PORT 已被占用，正在清理..."
        force_stop_port $APP_H5_PORT
    fi

    cd "$APP_DIR" || {
        log_error "无法进入目录: $APP_DIR"
        cd "$current_dir"
        return 1
    }

    # 首次运行需要安装依赖
    if [ ! -d "node_modules" ] || [ "$FORCE_INSTALL" = true ]; then
        log_info "安装移动端依赖..."
        if ! pnpm install; then
            log_error "依赖安装失败"
            cd "$current_dir"
            return 1
        fi
    fi

    # 启动开发服务器
    log_info "启动 H5 开发服务器..."
    nohup pnpm dev:h5 > "$LOG_DIR/app-h5.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/app-h5.pid"

    # 恢复目录
    cd "$current_dir"

    # 等待启动（增加等待时间）
    local count=0
    local max_wait=30
    log_info "等待 H5 服务启动..."
    while [ $count -lt $max_wait ]; do
        sleep 1
        if check_port $APP_H5_PORT; then
            log_info "H5 地址: http://localhost:$APP_H5_PORT"
            return 0
        fi
        count=$((count + 1))
        printf "."
    done
    echo ""

    log_warn "H5 服务可能还在启动中，请稍后检查端口 $APP_H5_PORT"
    return 0
}

#===============================================================================
# 移动端微信小程序管理
#===============================================================================

start_app_mp() {
    log_service "启动移动端微信小程序 (triflow-app)..."

    # 保存当前目录
    local current_dir=$(pwd)

    # 检查依赖
    if ! check_command node; then
        return 1
    fi

    if ! check_command pnpm; then
        log_error "pnpm 未安装，请运行: npm install -g pnpm"
        return 1
    fi

    cd "$APP_DIR" || {
        log_error "无法进入目录: $APP_DIR"
        cd "$current_dir"
        return 1
    }

    # 首次运行需要安装依赖
    if [ ! -d "node_modules" ] || [ "$FORCE_INSTALL" = true ]; then
        log_info "安装移动端依赖..."
        if ! pnpm install; then
            log_error "依赖安装失败"
            cd "$current_dir"
            return 1
        fi
    fi

    # 创建输出目录（如果不存在）
    mkdir -p "$APP_DIR/dist/dev"

    # 构建微信小程序（后台运行监听模式）
    log_info "启动微信小程序开发构建 (监听模式)..."
    nohup pnpm dev:mp > "$LOG_DIR/app-mp.log" 2>&1 &

    local pid=$!
    echo $pid > "$PID_DIR/app-mp.pid"

    # 恢复目录
    cd "$current_dir"

    # 等待构建初始化
    log_info "等待构建初始化..."
    local count=0
    local max_wait=30
    while [ $count -lt $max_wait ]; do
        sleep 1
        # 检查进程是否还在运行
        if ! kill -0 $pid 2>/dev/null; then
            log_error "微信小程序构建进程已退出，请检查日志: $LOG_DIR/app-mp.log"
            return 1
        fi
        # 检查输出目录是否已创建
        if [ -d "$APP_DIR/dist/dev/mp-weixin" ]; then
            break
        fi
        count=$((count + 1))
        printf "."
    done
    echo ""

    log_info "微信小程序构建已启动 (PID: $pid)"
    log_info "请使用微信开发者工具打开: $APP_DIR/dist/dev/mp-weixin"
    echo ""
    echo -e "${YELLOW}  提示: 如果目录不存在，请等待构建完成后再打开${NC}"
    echo -e "${YELLOW}  日志文件: $LOG_DIR/app-mp.log${NC}"
    echo ""
    return 0
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
        echo -e "  server      后端服务:      ${RED}○ 未运行${NC}"
    fi

    # Web 前端
    if check_port $WEB_PORT; then
        echo -e "  web:admin   Web 管理后台:  ${GREEN}● 运行中${NC}  http://localhost:$WEB_PORT"
    else
        echo -e "  web:admin   Web 管理后台:  ${RED}○ 未运行${NC}"
    fi

    # 移动端 H5
    if check_port $APP_H5_PORT; then
        echo -e "  app:h5      移动端 H5:     ${GREEN}● 运行中${NC}  http://localhost:$APP_H5_PORT"
    else
        echo -e "  app:h5      移动端 H5:     ${RED}○ 未运行${NC}"
    fi

    # 微信小程序
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
# 查看日志
#===============================================================================

show_logs() {
    local service=$1

    case $service in
        server)
            tail -f "$LOG_DIR/server.log"
            ;;
        web|web:admin)
            tail -f "$LOG_DIR/web.log"
            ;;
        app:h5)
            tail -f "$LOG_DIR/app-h5.log"
            ;;
        app:mp)
            tail -f "$LOG_DIR/app-mp.log"
            ;;
        all|"")
            log_info "同时显示所有服务日志 (Ctrl+C 退出)..."
            tail -f "$LOG_DIR/server.log" "$LOG_DIR/web.log" "$LOG_DIR/app-h5.log" "$LOG_DIR/app-mp.log" 2>/dev/null
            ;;
        *)
            log_error "未知服务: $service"
            log_info "可用选项: server, web:admin, app:h5, app:mp, all"
            ;;
    esac
}

#===============================================================================
# 帮助信息
#===============================================================================

show_help() {
    echo ""
    echo "Triflow 项目启动脚本"
    echo ""
    echo "用法: ./start.sh [services...] [options]"
    echo ""
    echo "服务标识符:"
    echo "  server          后端服务 (端口 7100)"
    echo "  web:admin       Web 管理后台 (端口 7200)"
    echo "  app:h5          App H5 版本 (端口 7300)"
    echo "  app:mp          App 微信小程序 (构建并提示打开开发者工具)"
    echo ""
    echo "特殊命令:"
    echo "  stop            停止所有服务"
    echo "  status          查看服务状态"
    echo "  logs [service]  查看服务日志 (service: server/web:admin/app:h5/app:mp/all)"
    echo "  help            显示帮助信息"
    echo ""
    echo "选项:"
    echo "  --build         强制重新编译后端（已默认每次启动都会编译）"
    echo "  --install       强制重新安装前端依赖"
    echo "  --profile=xxx   指定后端 Profile (默认: local)"
    echo "  --logs          启动后显示所有服务日志"
    echo "  --no-logs       启动后不自动显示日志（默认会显示后端日志）"
    echo ""
    echo "示例:"
    echo "  ./start.sh                              # 启动所有服务"
    echo "  ./start.sh server                       # 仅启动后端"
    echo "  ./start.sh server web:admin             # 启动后端 + Web 管理后台"
    echo "  ./start.sh server app:h5 app:mp         # 启动后端 + App H5 + 微信小程序"
    echo "  ./start.sh web:admin app:h5             # 启动 Web + App H5"
    echo "  ./start.sh --build server               # 重新编译并启动后端"
    echo "  ./start.sh logs server                  # 查看后端日志"
    echo ""
}

#===============================================================================
# 解析服务参数
#===============================================================================

parse_services() {
    for arg in "$@"; do
        case $arg in
            server)
                SERVICES_TO_START+=("server")
                ;;
            web|web:admin)
                SERVICES_TO_START+=("web:admin")
                ;;
            app:h5)
                SERVICES_TO_START+=("app:h5")
                ;;
            app:mp|mp)
                SERVICES_TO_START+=("app:mp")
                ;;
            # 忽略选项参数
            --*) ;;
            # 忽略特殊命令
            stop|status|logs|help) ;;
            *)
                log_warn "未知服务: $arg (已忽略)"
                ;;
        esac
    done
}

#===============================================================================
# 启动选定的服务
#===============================================================================

start_services() {
    local has_error=false

    for service in "${SERVICES_TO_START[@]}"; do
        case $service in
            server)
                start_server || has_error=true
                ;;
            web:admin)
                start_web_admin || has_error=true
                ;;
            app:h5)
                start_app_h5 || has_error=true
                ;;
            app:mp)
                start_app_mp || has_error=true
                ;;
        esac
        echo ""
    done

    if [ "$has_error" = true ]; then
        log_warn "部分服务启动失败"
    fi
}

#===============================================================================
# 停止所有服务
#===============================================================================

stop_all() {
    log_step "停止所有服务..."

    # 停止后端
    if [ -f "$PID_DIR/server.pid" ]; then
        local pid=$(cat "$PID_DIR/server.pid")
        kill $pid 2>/dev/null && log_info "后端服务已停止"
        rm -f "$PID_DIR/server.pid"
    fi
    force_stop_port $SERVER_PORT

    # 停止 Web
    if [ -f "$PID_DIR/web.pid" ]; then
        local pid=$(cat "$PID_DIR/web.pid")
        kill $pid 2>/dev/null && log_info "Web 管理后台已停止"
        rm -f "$PID_DIR/web.pid"
    fi
    force_stop_port $WEB_PORT

    # 停止 App H5
    if [ -f "$PID_DIR/app-h5.pid" ]; then
        local pid=$(cat "$PID_DIR/app-h5.pid")
        kill $pid 2>/dev/null && log_info "App H5 已停止"
        rm -f "$PID_DIR/app-h5.pid"
    fi
    force_stop_port $APP_H5_PORT

    # 停止 App MP
    if [ -f "$PID_DIR/app-mp.pid" ]; then
        local pid=$(cat "$PID_DIR/app-mp.pid")
        kill $pid 2>/dev/null && log_info "微信小程序构建已停止"
        rm -f "$PID_DIR/app-mp.pid"
    fi

    log_info "所有服务已停止"
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
            --no-logs)
                NO_LOGS=true
                ;;
        esac
    done

    # 处理特殊命令
    case $1 in
        stop)
            print_banner
            stop_all
            show_status
            return 0
            ;;
        status)
            print_banner
            show_status
            return 0
            ;;
        logs)
            show_logs "$2"
            return 0
            ;;
        help|--help|-h)
            print_banner
            show_help
            return 0
            ;;
    esac

    print_banner

    # 解析要启动的服务
    parse_services "$@"

    # 如果没有指定服务，默认启动所有
    if [ ${#SERVICES_TO_START[@]} -eq 0 ]; then
        log_step "启动所有服务 (server + web:admin + app:h5 + app:mp)..."
        SERVICES_TO_START=("server" "web:admin" "app:h5" "app:mp")
    else
        log_step "启动选定服务: ${SERVICES_TO_START[*]}"
    fi

    echo ""

    # 启动服务
    start_services

    # 显示状态
    show_status

    # 如果启动了后端服务，自动显示日志
    # 使用 --no-logs 选项可禁用自动显示日志
    if [[ " ${SERVICES_TO_START[*]} " =~ " server " ]] && [ "$NO_LOGS" != true ]; then
        echo ""
        log_info "后端服务日志 (按 Ctrl+C 退出日志查看，服务将继续运行)"
        log_info "提示：使用 --no-logs 选项可跳过自动显示日志"
        echo ""
        echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
        echo -e "${CYAN}                     后端服务日志                              ${NC}"
        echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
        echo ""
        tail -f "$LOG_DIR/server.log"
    elif [ "$SHOW_LOGS" = true ]; then
        echo ""
        log_info "按 Ctrl+C 退出日志查看 (服务将继续运行)"
        echo ""
        echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
        echo -e "${CYAN}                     服务日志                                  ${NC}"
        echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
        echo ""
        # 显示所有已启动服务的日志
        local log_files=()
        [[ " ${SERVICES_TO_START[*]} " =~ " server " ]] && log_files+=("$LOG_DIR/server.log")
        [[ " ${SERVICES_TO_START[*]} " =~ " web:admin " ]] && log_files+=("$LOG_DIR/web.log")
        [[ " ${SERVICES_TO_START[*]} " =~ " app:h5 " ]] && log_files+=("$LOG_DIR/app-h5.log")
        [[ " ${SERVICES_TO_START[*]} " =~ " app:mp " ]] && log_files+=("$LOG_DIR/app-mp.log")
        tail -f "${log_files[@]}" 2>/dev/null
    fi
}

# 运行主函数
main "$@"
