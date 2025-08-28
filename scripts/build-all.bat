@echo off
setlocal enabledelayedexpansion
echo =====================================================
echo YXRobot 全栈项目构建脚本
echo =====================================================

cd /d "%~dp0.."

echo 🏗️  开始构建YXRobot全栈项目...
echo.

REM 检查Maven是否可用
echo 🔍 检查构建环境...
mvn --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Maven未安装或未配置到PATH中
    echo 请安装Maven并配置环境变量
    pause
    exit /b 1
)
echo ✅ Maven环境检查通过

REM 检查Node.js是否可用（如果需要本地构建前端）
node --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ⚠️  Node.js未安装，将使用Maven插件自动下载
) else (
    echo ✅ Node.js环境检查通过
)

echo.
echo 📦 开始Maven构建...
echo 构建过程包括：
echo   1. 下载Node.js和npm（如果需要）
echo   2. 安装前端依赖
echo   3. 构建前端项目
echo   4. 复制前端静态文件到后端resources
echo   5. 编译后端Java代码
echo   6. 打包为WAR文件
echo.

REM 执行Maven构建
mvn clean package -DskipTests

if !errorlevel! equ 0 (
    echo.
    echo ✅ 构建成功！
    echo.
    echo 📋 构建产物：
    if exist "target\yxrobot.war" (
        echo   WAR文件: target\yxrobot.war
        for %%F in ("target\yxrobot.war") do echo   文件大小: %%~zF 字节
    )
    
    echo.
    echo 🚀 部署说明：
    echo   1. 将WAR文件复制到Tomcat的webapps目录
    echo   2. 启动Tomcat服务器
    echo   3. 访问 http://localhost:8080/yxrobot
    echo.
    echo 💡 快速部署：运行 scripts\deploy-tomcat.bat
    
) else (
    echo.
    echo ❌ 构建失败！
    echo 请检查上面的错误信息并修复问题
)

echo.
pause