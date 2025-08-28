@echo off
echo ========================================
echo YXRobot 全栈项目构建和运行脚本
echo ========================================

echo.
echo [1/4] 清理之前的构建...
call mvn clean

echo.
echo [2/4] 安装前端依赖...
cd src\frontend
call npm install
if %errorlevel% neq 0 (
    echo 前端依赖安装失败！
    pause
    exit /b 1
)

echo.
echo [3/4] 构建前端项目...
call npm run build
if %errorlevel% neq 0 (
    echo 前端构建失败！
    pause
    exit /b 1
)

cd ..\..

echo.
echo [4/4] 构建并启动后端项目...
call mvn spring-boot:run -Dspring-boot.run.profiles=dev

echo.
echo 构建完成！应用已启动。
echo 访问地址: http://localhost:8080
echo 按任意键退出...
pause