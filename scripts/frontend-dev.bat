@echo off
setlocal enabledelayedexpansion
echo =====================================================
echo YXRobot 前端开发服务器
echo =====================================================

cd /d "%~dp0..\src\frontend"

echo 🚀 启动前端开发服务器...
echo.
echo 📋 前端开发特性：
echo   - 热重载：修改代码自动刷新浏览器
echo   - 快速编译：Vite极速构建
echo   - 实时预览：所见即所得
echo   - API代理：自动转发到后端服务器
echo.

REM 检查Node.js是否可用
echo 🔍 检查前端环境...
node --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Node.js未安装或未配置到PATH中
    echo 请安装Node.js并配置环境变量
    pause
    exit /b 1
)
echo ✅ Node.js环境检查通过

REM 检查依赖是否安装
if not exist "node_modules" (
    echo 📦 安装前端依赖...
    npm install
)

echo.
echo 🏗️ 启动Vite开发服务器...
echo 前端地址：http://localhost:5173
echo 管理后台：http://localhost:5173/admin/content/products
echo.
echo 💡 提示：
echo   - 前端运行在 5173 端口
echo   - 后端运行在 8080 端口
echo   - API请求会自动代理到后端
echo   - 按 Ctrl+C 停止服务器
echo.

REM 启动前端开发服务器
npm run dev

pause