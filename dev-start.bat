@echo off
echo 启动YXRobot开发环境...
echo.

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=dev
set MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=256m

REM 检查端口是否被占用
echo 检查端口占用情况...
netstat -ano | findstr :8081 > nul
if %errorlevel% == 0 (
    echo 端口8081已被占用，正在停止相关进程...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
        taskkill /PID %%a /F > nul 2>&1
    )
    timeout /t 2 > nul
)

REM 清理之前的构建
echo 清理构建缓存...
if exist target\classes rmdir /s /q target\classes > nul 2>&1
if exist target\test-classes rmdir /s /q target\test-classes > nul 2>&1

REM 启动应用（跳过测试，启用热重载）
echo 启动Spring Boot应用...
echo 访问地址: http://localhost:8081
echo 按 Ctrl+C 停止应用
echo.

mvn spring-boot:run ^
    -Dspring-boot.run.profiles=dev ^
    -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true -Xmx1024m" ^
    -Dspring-boot.run.fork=false

pause