@echo off
setlocal enabledelayedexpansion
echo =====================================================
echo YXRobot 全栈开发环境启动
echo =====================================================

cd /d "%~dp0.."

echo 🚀 启动YXRobot全栈开发环境...
echo.
echo 📋 开发环境特性：
echo   - 前端热重载：修改Vue代码自动刷新
echo   - 后端热重载：修改Java代码自动重启
echo   - API代理：前端请求自动转发到后端
echo   - 实时调试：支持断点调试
echo.

REM 检查环境
echo 🔍 检查开发环境...
mvn --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Maven未安装
    pause
    exit /b 1
)

node --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Node.js未安装
    pause
    exit /b 1
)

echo ✅ 开发环境检查通过

echo.
echo 🏗️ 启动后端开发服务器（端口8080）...
start "YXRobot Backend" cmd /k "cd /d %~dp0.. && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo 等待后端服务器启动...
timeout /t 10 /nobreak >nul

echo.
echo 🎨 启动前端开发服务器（端口5173）...
start "YXRobot Frontend" cmd /k "cd /d %~dp0..\src\frontend && npm run dev"

echo.
echo 🎉 开发环境启动完成！
echo.
echo 📋 访问地址：
echo   前端开发服务器: http://localhost:5173
echo   后端API服务器:  http://localhost:8080
echo   管理后台页面:   http://localhost:5173/admin/content/products
echo   API接口测试:    http://localhost:8080/api/admin/products
echo.
echo 💡 开发提示：
echo   - 修改Vue文件会自动刷新浏览器
echo   - 修改Java文件会自动重启后端
echo   - 前端API请求会自动代理到后端
echo   - 关闭此窗口会停止所有服务
echo.

pause