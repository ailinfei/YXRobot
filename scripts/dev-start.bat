@echo off
setlocal enabledelayedexpansion
echo =====================================================
echo YXRobot 开发模式启动脚本
echo =====================================================

cd /d "%~dp0.."

echo 🚀 启动YXRobot开发服务器...
echo.
echo 📋 开发模式特性：
echo   - 后端热重载：修改Java代码自动重启
echo   - 前端热重载：修改Vue代码自动刷新
echo   - 快速启动：无需打包WAR文件
echo   - 实时调试：支持IDE断点调试
echo.

REM 检查Maven是否可用
echo 🔍 检查开发环境...
mvn --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Maven未安装或未配置到PATH中
    echo 请安装Maven并配置环境变量
    pause
    exit /b 1
)
echo ✅ Maven环境检查通过

echo.
echo 🏗️ 启动Spring Boot开发服务器...
echo 访问地址：http://localhost:8080
echo 管理后台：http://localhost:8080/admin/content/products
echo API接口：http://localhost:8080/api/admin/products
echo.
echo 💡 提示：
echo   - 按 Ctrl+C 停止服务器
echo   - 修改Java代码会自动重启
echo   - 修改前端代码会自动刷新浏览器
echo.

REM 启动Spring Boot开发服务器
mvn spring-boot:run -Dspring-boot.run.profiles=dev

pause