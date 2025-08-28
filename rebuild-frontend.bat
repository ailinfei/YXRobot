@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot 前端快速重建脚本
echo ========================================
echo.

REM 设置环境变量
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

echo 1. 清理前端构建文件...
if exist "src\main\resources\static" (
    rmdir /s /q "src\main\resources\static" > nul 2>&1
)
if exist "src\frontend\dist" (
    rmdir /s /q "src\frontend\dist" > nul 2>&1
)

echo 2. 重新构建前端...
cd src\frontend

REM 检查是否有node_modules
if not exist "node_modules" (
    echo    安装前端依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo    错误：前端依赖安装失败
        cd ..\..
        pause
        exit /b 1
    )
)

echo    构建前端资源...
call npm run build
if %errorlevel% neq 0 (
    echo    错误：前端构建失败
    cd ..\..
    pause
    exit /b 1
)

cd ..\..

echo 3. 复制前端资源到静态目录...
call mvn process-resources -q
if %errorlevel% neq 0 (
    echo    错误：资源复制失败
    pause
    exit /b 1
)

REM 验证构建结果
if not exist "src\main\resources\static\index.html" (
    echo    错误：前端构建文件未找到
    pause
    exit /b 1
)

echo.
echo ========================================
echo    前端重建完成！
echo ========================================
echo    前端资源已更新到静态目录
echo    如果Spring Boot正在运行，页面将自动刷新
echo    
echo    访问地址: http://localhost:8081
echo ========================================

timeout /t 3 > nul