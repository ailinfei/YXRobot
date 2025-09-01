@echo off
echo ========================================
echo 客户API基础集成测试
echo ========================================
echo.

echo 📍 当前目录: %CD%
echo 📍 测试时间: %DATE% %TIME%
echo.

echo 🔍 检查Node.js环境...
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Node.js 未安装或不在PATH中
    echo 请安装Node.js并确保在PATH中
    pause
    exit /b 1
)

echo ✅ Node.js 环境正常
echo.

echo 🔍 检查前端依赖...
cd /d "%~dp0..\src\frontend"
if not exist "node_modules" (
    echo ⚠️  前端依赖未安装，正在安装...
    npm install
    if errorlevel 1 (
        echo ❌ 前端依赖安装失败
        pause
        exit /b 1
    )
)

echo ✅ 前端依赖已安装
echo.

echo 🚀 运行基础API集成测试...
echo ----------------------------------------
npx ts-node src/__tests__/integration/basicApiTest.ts

echo.
echo ========================================
echo 测试完成
echo ========================================
pause