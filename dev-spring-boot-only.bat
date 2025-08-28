@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot Spring Boot Only Mode
echo    (Backend API + Static Frontend)
echo ========================================
echo.

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=dev
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

REM 检查Maven
echo 1. 检查开发环境...
where mvn > nul 2>&1
if %errorlevel% neq 0 (
    echo    错误: 未找到Maven，请安装Maven并添加到PATH
    pause
    exit /b 1
)

REM 检查并停止占用端口8081的进程
echo 2. 检查端口占用情况...
netstat -ano | findstr :8081 > nul
if %errorlevel% == 0 (
    echo    端口8081被占用，正在停止相关进程...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
        taskkill /PID %%a /F > nul 2>&1
    )
    timeout /t 2 > nul
)

REM 启动Spring Boot应用
echo 3. 启动Spring Boot应用...
echo    应用将在端口8081启动
echo    前端页面: http://localhost:8081/
echo    后端API: http://localhost:8081/api/
echo.

mvn spring-boot:run -Dspring.profiles.active=dev -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true -Xmx1024m -Dfile.encoding=UTF-8"

echo.
echo 应用已停止
pause