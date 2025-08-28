@echo off
echo 快速重启后端服务...

REM 停止后端进程
echo 停止现有后端进程...
taskkill /F /IM java.exe > nul 2>&1
netstat -ano | findstr :8081 > nul
if %errorlevel% == 0 (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
        taskkill /PID %%a /F > nul 2>&1
    )
)

timeout /t 2 > nul

REM 快速启动（跳过前端构建）
echo 快速启动后端...
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true -Xmx1024m" -Dspring-boot.run.fork=false -Dfrontend.skip=true

pause