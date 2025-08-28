@echo off
title YXRobot 快速开发启动
color 0A

echo.
echo  ╔══════════════════════════════════════╗
echo  ║        YXRobot 快速开发启动          ║
echo  ╚══════════════════════════════════════╝
echo.

REM 检查是否需要重新构建
set NEED_BUILD=0
if not exist "target\classes\com\yxrobot\YXRobotApplication.class" (
    set NEED_BUILD=1
    echo  → 检测到首次启动，需要完整构建...
) else (
    echo  → 检测到已有构建文件，使用快速启动...
)

REM 停止现有进程
echo  → 停止现有服务...
taskkill /F /IM java.exe > nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr :8081') do (
    taskkill /PID %%a /F > nul 2>&1
)

timeout /t 1 > nul

REM 根据情况选择启动方式
if %NEED_BUILD%==1 (
    echo  → 执行完整构建启动...
    mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true -Xmx1024m -XX:TieredStopAtLevel=1 -XX:+UseParallelGC" -Dspring-boot.run.fork=false
) else (
    echo  → 执行快速启动（跳过前端构建）...
    mvn spring-boot:run -Pskip-frontend -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true -Xmx1024m -XX:TieredStopAtLevel=1 -XX:+UseParallelGC" -Dspring-boot.run.fork=false
)

echo.
echo  服务已停止，按任意键退出...
pause > nul