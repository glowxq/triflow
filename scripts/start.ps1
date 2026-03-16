#===============================================================================
#  Triflow 一键启动脚本 (Windows PowerShell)
#  支持: Windows 10/11 + PowerShell 5.1+
#  用法: .\start.ps1 [command] [options]
#===============================================================================

param(
    [Parameter(Position = 0)]
    [string]$Command = "",

    [Parameter(Position = 1)]
    [string]$SubCommand = "",

    [Parameter(Position = 2)]
    [string]$Option = "",

    [switch]$Build,
    [switch]$Install,
    [string]$Profile = "local"
)

# 设置编码为 UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$PSDefaultParameterValues['Out-File:Encoding'] = 'utf8'

#===============================================================================
# 配置变量
#===============================================================================

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$ServerDir = Join-Path $ProjectRoot "triflow-server"
$WebDir = Join-Path $ProjectRoot "triflow-web"
$AppDir = Join-Path $ProjectRoot "triflow-app"

$PidDir = Join-Path $ProjectRoot ".pids"
$LogDir = Join-Path $ProjectRoot ".logs"

# 创建目录
if (-not (Test-Path $PidDir)) { New-Item -ItemType Directory -Path $PidDir -Force | Out-Null }
if (-not (Test-Path $LogDir)) { New-Item -ItemType Directory -Path $LogDir -Force | Out-Null }

# 默认配置
$ServerProfile = $Profile
$ServerPort = 7100
$WebPort = 5777
$AppPort = 9000

# Java 25 路径 (根据实际安装路径调整)
$Java25Paths = @(
    "C:\Program Files\Java\jdk-25",
    "C:\Program Files\Eclipse Adoptium\jdk-25*",
    "C:\Program Files\Microsoft\jdk-25*",
    "C:\Program Files\Zulu\zulu-25*",
    "$env:USERPROFILE\.jdks\jdk-25*",
    "$env:USERPROFILE\scoop\apps\openjdk25\current"
)

#===============================================================================
# 工具函数
#===============================================================================

function Write-Banner {
    Write-Host ""
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host "                   Triflow 启动脚本                            " -ForegroundColor Cyan
    Write-Host "          Server / Web / App 三端快速开发脚手架                " -ForegroundColor Cyan
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host ""
}

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] " -ForegroundColor Green -NoNewline
    Write-Host $Message
}

function Write-Warn {
    param([string]$Message)
    Write-Host "[WARN] " -ForegroundColor Yellow -NoNewline
    Write-Host $Message
}

function Write-Error2 {
    param([string]$Message)
    Write-Host "[ERROR] " -ForegroundColor Red -NoNewline
    Write-Host $Message
}

function Write-Step {
    param([string]$Message)
    Write-Host "[STEP] " -ForegroundColor Blue -NoNewline
    Write-Host $Message
}

function Test-Command {
    param([string]$CommandName)
    return $null -ne (Get-Command $CommandName -ErrorAction SilentlyContinue)
}

function Test-Port {
    param([int]$Port)
    $connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $null -ne $connection
}

function Find-Java25 {
    foreach ($path in $Java25Paths) {
        $resolved = Resolve-Path -Path $path -ErrorAction SilentlyContinue
        if ($resolved) {
            $javaExe = Join-Path $resolved.Path "bin\java.exe"
            if (Test-Path $javaExe) {
                return $resolved.Path
            }
        }
    }
    return $null
}

function Wait-ForService {
    param(
        [int]$Port,
        [string]$Name,
        [int]$Timeout = 60
    )

    Write-Info "等待 $Name 启动 (端口: $Port)..."
    $count = 0
    while (-not (Test-Port $Port)) {
        Start-Sleep -Seconds 1
        $count++
        if ($count -ge $Timeout) {
            Write-Error2 "$Name 启动超时"
            return $false
        }
        Write-Host "." -NoNewline
    }
    Write-Host ""
    Write-Info "$Name 启动成功!"
    return $true
}

function Stop-ProcessByPort {
    param([int]$Port)
    $connections = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    if ($connections) {
        $pids = $connections | Select-Object -ExpandProperty OwningProcess -Unique
        foreach ($pid in $pids) {
            try {
                Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
                Write-Info "已停止进程 (PID: $pid)"
            } catch {
                # 忽略错误
            }
        }
    }
}

#===============================================================================
# 后端服务管理
#===============================================================================

function Start-Server {
    param([switch]$ForceBuild)

    Write-Step "启动后端服务 (triflow-server)..."

    # 查找并设置 Java 25
    $java25Home = Find-Java25
    if ($java25Home) {
        $env:JAVA_HOME = $java25Home
        $env:PATH = "$java25Home\bin;$env:PATH"
        Write-Info "使用 Java 25: $java25Home"
    } else {
        Write-Warn "未找到 Java 25，使用系统默认 Java"
        Write-Warn "建议安装 Java 25 到以下路径之一:"
        Write-Warn "  - C:\Program Files\Java\jdk-25"
        Write-Warn "  - C:\Program Files\Eclipse Adoptium\jdk-25.x.x"
    }

    # 检查 Java
    if (-not (Test-Command "java")) {
        Write-Error2 "Java 未安装，请先安装 JDK 25"
        return $false
    }

    # 显示 Java 版本
    $javaVersion = & java -version 2>&1 | Select-Object -First 1
    Write-Info "Java 版本: $javaVersion"

    # 检查 Maven
    if (-not (Test-Command "mvn")) {
        Write-Error2 "Maven 未安装，请先安装 Maven 3.9+"
        return $false
    }

    # 检查端口
    if (Test-Port $ServerPort) {
        Write-Warn "端口 $ServerPort 已被占用"
        $answer = Read-Host "是否尝试停止现有服务? [y/N]"
        if ($answer -eq "y" -or $answer -eq "Y") {
            Stop-Server
        } else {
            return $false
        }
    }

    Push-Location $ServerDir

    # 首次运行需要编译
    $targetDir = Join-Path $ServerDir "base\target"
    if (-not (Test-Path $targetDir) -or $ForceBuild -or $Build) {
        Write-Info "编译后端项目..."
        & mvn clean install -DskipTests -q
        if ($LASTEXITCODE -ne 0) {
            Write-Error2 "编译失败"
            Pop-Location
            return $false
        }
    }

    # 启动服务
    Write-Info "使用 Profile: $ServerProfile"
    $logFile = Join-Path $LogDir "server.log"
    $pidFile = Join-Path $PidDir "server.pid"

    $process = Start-Process -FilePath "mvn" `
        -ArgumentList "-pl", "base", "spring-boot:run", "-Dspring-boot.run.profiles=$ServerProfile" `
        -WorkingDirectory $ServerDir `
        -RedirectStandardOutput $logFile `
        -RedirectStandardError "$logFile.err" `
        -PassThru `
        -WindowStyle Hidden

    $process.Id | Out-File -FilePath $pidFile -Encoding ascii

    Pop-Location

    # 等待启动
    if (Wait-ForService -Port $ServerPort -Name "后端服务" -Timeout 120) {
        Write-Info "后端服务地址: http://localhost:$ServerPort"
        Write-Info "API 文档地址: http://localhost:$ServerPort/doc.html"
        return $true
    }
    return $false
}

function Stop-Server {
    Write-Step "停止后端服务..."

    $pidFile = Join-Path $PidDir "server.pid"
    if (Test-Path $pidFile) {
        $pid = Get-Content $pidFile
        try {
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Write-Info "后端服务已停止 (PID: $pid)"
        } catch {
            # 忽略错误
        }
        Remove-Item $pidFile -Force -ErrorAction SilentlyContinue
    }

    # 强制清理端口
    Stop-ProcessByPort -Port $ServerPort
}

#===============================================================================
# Web 前端管理
#===============================================================================

function Start-Web {
    param([switch]$ForceInstall)

    Write-Step "启动 Web 前端 (triflow-web)..."

    # 检查 Node.js
    if (-not (Test-Command "node")) {
        Write-Error2 "Node.js 未安装，请先安装 Node.js 20+"
        return $false
    }

    # 检查 pnpm
    if (-not (Test-Command "pnpm")) {
        Write-Error2 "pnpm 未安装，请运行: npm install -g pnpm"
        return $false
    }

    # 检查端口
    if (Test-Port $WebPort) {
        Write-Warn "端口 $WebPort 已被占用"
        $answer = Read-Host "是否尝试停止现有服务? [y/N]"
        if ($answer -eq "y" -or $answer -eq "Y") {
            Stop-Web
        } else {
            return $false
        }
    }

    Push-Location $WebDir

    # 首次运行需要安装依赖
    $nodeModules = Join-Path $WebDir "node_modules"
    if (-not (Test-Path $nodeModules) -or $ForceInstall -or $Install) {
        Write-Info "安装前端依赖..."
        & pnpm install
        if ($LASTEXITCODE -ne 0) {
            Write-Error2 "依赖安装失败"
            Pop-Location
            return $false
        }
    }

    # 启动开发服务器
    Write-Info "启动后台管理系统..."
    $logFile = Join-Path $LogDir "web.log"
    $pidFile = Join-Path $PidDir "web.pid"

    $process = Start-Process -FilePath "pnpm" `
        -ArgumentList "dev:admin" `
        -WorkingDirectory $WebDir `
        -RedirectStandardOutput $logFile `
        -RedirectStandardError "$logFile.err" `
        -PassThru `
        -WindowStyle Hidden

    $process.Id | Out-File -FilePath $pidFile -Encoding ascii

    Pop-Location

    # 等待启动
    if (Wait-ForService -Port $WebPort -Name "Web 前端" -Timeout 60) {
        Write-Info "后台管理地址: http://localhost:$WebPort"
        return $true
    }
    return $false
}

function Stop-Web {
    Write-Step "停止 Web 前端..."

    $pidFile = Join-Path $PidDir "web.pid"
    if (Test-Path $pidFile) {
        $pid = Get-Content $pidFile
        try {
            # 停止进程树
            Get-Process | Where-Object { $_.Id -eq $pid -or $_.Parent.Id -eq $pid } | Stop-Process -Force -ErrorAction SilentlyContinue
            Write-Info "Web 前端已停止 (PID: $pid)"
        } catch {
            # 忽略错误
        }
        Remove-Item $pidFile -Force -ErrorAction SilentlyContinue
    }

    # 强制清理端口
    Stop-ProcessByPort -Port $WebPort
}

#===============================================================================
# 移动端管理
#===============================================================================

function Start-App {
    param(
        [string]$Platform = "h5",
        [switch]$ForceInstall
    )

    Write-Step "启动移动端 (triflow-app - $Platform)..."

    # 检查 Node.js
    if (-not (Test-Command "node")) {
        Write-Error2 "Node.js 未安装，请先安装 Node.js 20+"
        return $false
    }

    # 检查 pnpm
    if (-not (Test-Command "pnpm")) {
        Write-Error2 "pnpm 未安装，请运行: npm install -g pnpm"
        return $false
    }

    Push-Location $AppDir

    # 首次运行需要安装依赖
    $nodeModules = Join-Path $AppDir "node_modules"
    if (-not (Test-Path $nodeModules) -or $ForceInstall -or $Install) {
        Write-Info "安装移动端依赖..."
        & pnpm install
        if ($LASTEXITCODE -ne 0) {
            Write-Error2 "依赖安装失败"
            Pop-Location
            return $false
        }
    }

    # 根据平台启动
    switch ($Platform) {
        "h5" {
            Write-Info "启动 H5 开发服务器..."
            $logFile = Join-Path $LogDir "app-h5.log"
            $pidFile = Join-Path $PidDir "app.pid"

            $process = Start-Process -FilePath "pnpm" `
                -ArgumentList "dev:h5" `
                -WorkingDirectory $AppDir `
                -RedirectStandardOutput $logFile `
                -RedirectStandardError "$logFile.err" `
                -PassThru `
                -WindowStyle Hidden

            $process.Id | Out-File -FilePath $pidFile -Encoding ascii

            Start-Sleep -Seconds 5
            Write-Info "H5 地址: http://localhost:$AppPort"
        }
        { $_ -in "mp", "weixin" } {
            Write-Info "启动微信小程序开发..."
            & pnpm dev:mp
            Write-Info "请使用微信开发者工具打开: dist\dev\mp-weixin"
        }
        "app" {
            Write-Info "启动 App 开发..."
            & pnpm dev:app
        }
        default {
            Write-Error2 "未知平台: $Platform"
            Write-Info "支持的平台: h5, mp, app"
            Pop-Location
            return $false
        }
    }

    Pop-Location
    return $true
}

function Stop-App {
    Write-Step "停止移动端..."

    $pidFile = Join-Path $PidDir "app.pid"
    if (Test-Path $pidFile) {
        $pid = Get-Content $pidFile
        try {
            Get-Process | Where-Object { $_.Id -eq $pid -or $_.Parent.Id -eq $pid } | Stop-Process -Force -ErrorAction SilentlyContinue
            Write-Info "移动端已停止 (PID: $pid)"
        } catch {
            # 忽略错误
        }
        Remove-Item $pidFile -Force -ErrorAction SilentlyContinue
    }

    # 强制清理端口
    Stop-ProcessByPort -Port $AppPort
}

#===============================================================================
# 服务状态
#===============================================================================

function Show-Status {
    Write-Host ""
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host "                       服务状态                                " -ForegroundColor Cyan
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host ""

    # 后端服务
    if (Test-Port $ServerPort) {
        Write-Host "  后端服务 (triflow-server):  " -NoNewline
        Write-Host "● 运行中" -ForegroundColor Green -NoNewline
        Write-Host "  http://localhost:$ServerPort"
    } else {
        Write-Host "  后端服务 (triflow-server):  " -NoNewline
        Write-Host "○ 未运行" -ForegroundColor Red
    }

    # Web 前端
    if (Test-Port $WebPort) {
        Write-Host "  Web 前端 (web-admin):       " -NoNewline
        Write-Host "● 运行中" -ForegroundColor Green -NoNewline
        Write-Host "  http://localhost:$WebPort"
    } else {
        Write-Host "  Web 前端 (web-admin):       " -NoNewline
        Write-Host "○ 未运行" -ForegroundColor Red
    }

    # 移动端 H5
    if (Test-Port $AppPort) {
        Write-Host "  移动端 H5 (triflow-app):    " -NoNewline
        Write-Host "● 运行中" -ForegroundColor Green -NoNewline
        Write-Host "  http://localhost:$AppPort"
    } else {
        Write-Host "  移动端 H5 (triflow-app):    " -NoNewline
        Write-Host "○ 未运行" -ForegroundColor Red
    }

    Write-Host ""
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host ""
}

#===============================================================================
# 查看日志
#===============================================================================

function Show-Logs {
    param([string]$Service)

    switch ($Service) {
        "server" {
            $logFile = Join-Path $LogDir "server.log"
            if (Test-Path $logFile) {
                Get-Content $logFile -Wait -Tail 50
            } else {
                Write-Error2 "日志文件不存在: $logFile"
            }
        }
        "web" {
            $logFile = Join-Path $LogDir "web.log"
            if (Test-Path $logFile) {
                Get-Content $logFile -Wait -Tail 50
            } else {
                Write-Error2 "日志文件不存在: $logFile"
            }
        }
        "app" {
            $logFile = Join-Path $LogDir "app-h5.log"
            if (Test-Path $logFile) {
                Get-Content $logFile -Wait -Tail 50
            } else {
                Write-Error2 "日志文件不存在: $logFile"
            }
        }
        { $_ -in "all", "" } {
            Write-Info "显示最近日志 (按 Ctrl+C 退出)..."
            $serverLog = Join-Path $LogDir "server.log"
            $webLog = Join-Path $LogDir "web.log"
            $appLog = Join-Path $LogDir "app-h5.log"

            # 显示各服务最近的日志
            if (Test-Path $serverLog) {
                Write-Host "`n=== Server 日志 ===" -ForegroundColor Cyan
                Get-Content $serverLog -Tail 20
            }
            if (Test-Path $webLog) {
                Write-Host "`n=== Web 日志 ===" -ForegroundColor Cyan
                Get-Content $webLog -Tail 20
            }
            if (Test-Path $appLog) {
                Write-Host "`n=== App 日志 ===" -ForegroundColor Cyan
                Get-Content $appLog -Tail 20
            }
        }
        default {
            Write-Error2 "未知服务: $Service"
            Write-Info "可用选项: server, web, app, all"
        }
    }
}

#===============================================================================
# 帮助信息
#===============================================================================

function Show-Help {
    Write-Host ""
    Write-Host "Triflow 项目启动脚本 (Windows PowerShell)"
    Write-Host ""
    Write-Host "用法: .\start.ps1 [command] [options]"
    Write-Host ""
    Write-Host "命令:"
    Write-Host "  (无参数)        启动所有服务 (后端 + Web)"
    Write-Host "  server          仅启动后端服务"
    Write-Host "  web             仅启动 Web 前端"
    Write-Host "  app [platform]  启动移动端 (platform: h5/mp/app, 默认 h5)"
    Write-Host "  all             启动所有服务 (包括移动端 H5)"
    Write-Host "  stop            停止所有服务"
    Write-Host "  restart         重启所有服务"
    Write-Host "  status          查看服务状态"
    Write-Host "  logs [service]  查看服务日志 (service: server/web/app/all)"
    Write-Host "  help            显示帮助信息"
    Write-Host ""
    Write-Host "选项:"
    Write-Host "  -Build          强制重新编译后端"
    Write-Host "  -Install        强制重新安装前端依赖"
    Write-Host "  -Profile xxx    指定后端 Profile (默认: local)"
    Write-Host ""
    Write-Host "示例:"
    Write-Host "  .\start.ps1                    # 启动后端 + Web"
    Write-Host "  .\start.ps1 server             # 仅启动后端"
    Write-Host "  .\start.ps1 web                # 仅启动 Web"
    Write-Host "  .\start.ps1 app h5             # 启动移动端 H5"
    Write-Host "  .\start.ps1 app mp             # 启动微信小程序"
    Write-Host "  .\start.ps1 stop               # 停止所有服务"
    Write-Host "  .\start.ps1 logs server        # 查看后端日志"
    Write-Host "  .\start.ps1 logs all           # 查看所有日志"
    Write-Host "  .\start.ps1 server -Build      # 强制重新编译并启动"
    Write-Host ""
}

#===============================================================================
# 主函数
#===============================================================================

function Main {
    switch ($Command) {
        "server" {
            Write-Banner
            Start-Server -ForceBuild:$Build
        }
        "web" {
            Write-Banner
            Start-Web -ForceInstall:$Install
        }
        "app" {
            Write-Banner
            $platform = if ($SubCommand) { $SubCommand } else { "h5" }
            Start-App -Platform $platform -ForceInstall:$Install
        }
        "all" {
            Write-Banner
            Write-Step "启动所有服务..."
            $serverOk = Start-Server -ForceBuild:$Build
            if ($serverOk) {
                $webOk = Start-Web -ForceInstall:$Install
                if ($webOk) {
                    Start-App -Platform "h5" -ForceInstall:$Install
                }
            }
            Show-Status
        }
        "stop" {
            Write-Banner
            Stop-Server
            Stop-Web
            Stop-App
            Write-Info "所有服务已停止"
        }
        "restart" {
            Write-Banner
            Stop-Server
            Stop-Web
            Stop-App
            Start-Sleep -Seconds 2
            $serverOk = Start-Server -ForceBuild:$Build
            if ($serverOk) {
                Start-Web -ForceInstall:$Install
            }
            Show-Status
        }
        "status" {
            Write-Banner
            Show-Status
        }
        "logs" {
            Show-Logs -Service $SubCommand
        }
        { $_ -in "help", "--help", "-h", "-?" } {
            Write-Banner
            Show-Help
        }
        default {
            Write-Banner
            Write-Step "启动核心服务 (后端 + Web)..."
            $serverOk = Start-Server -ForceBuild:$Build
            if ($serverOk) {
                Start-Web -ForceInstall:$Install
            }
            Show-Status
        }
    }
}

# 运行主函数
Main
