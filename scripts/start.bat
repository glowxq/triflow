@echo off
chcp 65001 >nul 2>&1
title Triflow 启动脚本

:: ===============================================================================
::  Triflow 一键启动脚本 (Windows Batch)
::  这是 PowerShell 脚本的入口文件，方便双击运行
::  完整功能请使用: powershell -ExecutionPolicy Bypass -File start.ps1 [command]
:: ===============================================================================

:: 检查是否有参数传入
if "%~1"=="" (
    echo.
    echo ===============================================================
    echo                    Triflow 启动脚本
    echo           Server / Web / App 三端快速开发脚手架
    echo ===============================================================
    echo.
    echo 请选择要执行的操作:
    echo.
    echo   [1] 启动所有服务 (后端 + Web)
    echo   [2] 启动所有服务 (包括移动端 H5)
    echo   [3] 仅启动后端服务
    echo   [4] 仅启动 Web 前端
    echo   [5] 启动移动端 H5
    echo   [6] 查看服务状态
    echo   [7] 停止所有服务
    echo   [8] 重启所有服务
    echo   [9] 查看帮助
    echo   [0] 退出
    echo.
    set /p choice="请输入选项 [0-9]: "

    if "%choice%"=="1" goto run_default
    if "%choice%"=="2" goto run_all
    if "%choice%"=="3" goto run_server
    if "%choice%"=="4" goto run_web
    if "%choice%"=="5" goto run_app
    if "%choice%"=="6" goto run_status
    if "%choice%"=="7" goto run_stop
    if "%choice%"=="8" goto run_restart
    if "%choice%"=="9" goto run_help
    if "%choice%"=="0" goto end

    echo 无效的选项，请重新运行
    pause
    goto end
) else (
    :: 直接传递参数给 PowerShell 脚本
    powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" %*
    goto end
)

:run_default
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1"
goto after_run

:run_all
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" all
goto after_run

:run_server
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" server
goto after_run

:run_web
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" web
goto after_run

:run_app
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" app h5
goto after_run

:run_status
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" status
goto after_run

:run_stop
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" stop
goto after_run

:run_restart
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" restart
goto after_run

:run_help
powershell -ExecutionPolicy Bypass -File "%~dp0start.ps1" help
goto after_run

:after_run
echo.
pause

:end
