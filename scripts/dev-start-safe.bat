@echo off
setlocal enabledelayedexpansion
echo =====================================================
echo YXRobot 安全开发环境启动脚本
echo =====================================================

cd /d "%~dp0.."

echo 🚀 启动YXRobot开发服务器...
echo.

echo 📋 启动流程：
echo   1. 检查并清理端口占用
echo   2. 验证开发环境配置
echo   3. 启动Spring Boot开发服务器
echo   4. 验证服务器状态
echo.

REM 步骤1: 端口管理和清理（强制执行）
echo 🔍 步骤1: 端口管理和清理...
echo.
echo 📋 端口管理规范：
echo   - Spring Boot后端: 8081端口（固定不变）
echo   - 前端开发服务器: 5173端口（仅开发时）
echo   - 数据库服务: 3306端口（远程MySQL）
echo.

echo 🔍 检查端口8081占用情况...
netstat -ano | findstr :8081 >nul
if !errorlevel! equ 0 (
    echo ⚠️  发现端口8081被占用，执行清理流程...
    echo.
    
    echo 📋 占用详情：
    netstat -ano | findstr :8081
    echo.
    
    REM 获取占用端口的进程ID并显示进程信息
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
        echo 🔍 检查进程 PID: %%a
        tasklist | findstr %%a
        echo 🧹 终止进程 PID: %%a
        taskkill /f /pid %%a >nul 2>&1
    )
    
    REM 强制终止所有Java进程
    echo.
    echo 🧹 强制清理所有Java进程...
    taskkill /f /im java.exe >nul 2>&1
    if !errorlevel! equ 0 (
        echo ✅ Java进程已终止
    ) else (
        echo ℹ️  未发现运行中的Java进程
    )
    
    REM 等待端口完全释放
    echo 🕐 等待端口完全释放（3秒）...
    timeout /t 3 /nobreak >nul
    
    REM 验证端口释放状态
    echo 🔍 验证端口释放状态...
    netstat -ano | findstr :8081 >nul
    if !errorlevel! equ 0 (
        echo ❌ 端口清理失败！端口8081仍被占用
        echo.
        echo 📋 当前占用详情：
        netstat -ano | findstr :8081
        echo.
        echo 🛠️  解决方案：
        echo   1. 手动终止占用进程
        echo   2. 重启计算机
        echo   3. 检查防火墙设置
        echo.
        pause
        exit /b 1
    )
    echo.
)
echo ✅ 端口8081已完全释放，可以安全启动项目

REM 步骤2: 验证开发环境
echo.
echo 🔍 步骤2: 验证开发环境...

REM 检查Maven
mvn --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Maven未安装或未配置到PATH中
    echo 请安装Maven并配置环境变量
    pause
    exit /b 1
)
echo ✅ Maven环境正常

REM 检查Java
java -version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Java未安装或未配置到PATH中
    echo 请安装JDK 21并配置环境变量
    pause
    exit /b 1
)
echo ✅ Java环境正常

REM 检查开发配置文件
if not exist "src\main\resources\application-dev.yml" (
    echo ❌ 开发配置文件不存在: application-dev.yml
    pause
    exit /b 1
)
echo ✅ 开发配置文件存在

REM 步骤3: 启动Spring Boot开发服务器
echo.
echo 🚀 步骤3: 启动Spring Boot开发服务器...
echo.
echo 📋 服务器信息：
echo   - 后端服务器: http://localhost:8081
echo   - API基础路径: http://localhost:8081/api
echo   - 管理后台: http://localhost:8081/admin/content/products
echo   - 配置文件: application-dev.yml
echo   - 数据库: MySQL (yun.finiot.cn:3306/YXRobot)
echo.
echo 💡 开发特性：
echo   - Java代码热重载：修改后自动重启
echo   - 配置文件热重载：修改后自动重启
echo   - CORS跨域支持：前端开发友好
echo   - 数据库连接池：开发环境优化
echo.
echo 🔄 正在启动服务器，请稍候...
echo ⚠️  首次启动可能需要1-2分钟下载依赖
echo.

REM 启动Spring Boot开发服务器
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"

REM 如果到达这里，说明服务器已停止
echo.
echo 🛑 开发服务器已停止
echo.
pause