@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot Maven集成模式启动
echo    (纯Spring Boot，前端已集成)
echo ========================================
echo.

REM 停止所有可能的Vite进程
echo 1. 确保Vite开发服务器已停止...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr :5173') do (
    echo    停止Vite进程 PID: %%a
    taskkill /PID %%a /F > nul 2>&1
)

REM 停止占用8081端口的进程
echo 2. 清理端口8081...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr :8081') do (
    echo    停止端口8081进程 PID: %%a
    taskkill /PID %%a /F > nul 2>&1
)
timeout /t 2 > nul

REM 检查前端资源是否已构建
echo 3. 检查前端集成状态...
if not exist "src\main\resources\static\index.html" (
    echo    前端资源未找到，需要先构建...
    echo    运行构建脚本...
    call build-integrated.bat
    if %errorlevel% neq 0 (
        echo    构建失败，无法启动
        pause
        exit /b 1
    )
) else (
    echo    ✓ 前端资源已集成
)

REM 设置Spring Boot环境
echo 4. 配置Spring Boot环境...
set SPRING_PROFILES_ACTIVE=dev
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

REM 启动Spring Boot应用
echo 5. 启动Spring Boot集成应用...
echo    模式: 前后端集成 (端口8081)
echo    前端: 静态资源服务
echo    后端: REST API服务
echo    启动中...
echo.

start "YXRobot-Integrated" cmd /c "mvn spring-boot:run -Dspring.profiles.active=dev -Dspring-boot.run.jvmArguments=\"-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=false -Xmx1024m -Dfile.encoding=UTF-8\" && pause"

REM 等待启动
echo    等待应用启动...
timeout /t 20 > nul

REM 测试应用状态
echo 6. 测试应用状态...
curl -s -o nul -w "%%{http_code}" --connect-timeout 5 "http://localhost:8081" > temp_status.txt 2>&1
set /p status_code=<temp_status.txt
del temp_status.txt > nul 2>&1

if "%status_code%"=="200" (
    echo    ✓ 应用启动成功！
    echo    正在打开浏览器...
    timeout /t 2 > nul
    start http://localhost:8081
) else (
    echo    应用可能还在启动中...
    echo    请稍后手动访问: http://localhost:8081
)

echo.
echo ========================================
echo    Maven集成模式启动完成
echo ========================================
echo    
echo    🌐 访问地址: http://localhost:8081
echo    📱 前端页面: http://localhost:8081/
echo    🔧 管理后台: http://localhost:8081/admin/
echo    🔌 后端API:  http://localhost:8081/api/
echo    
echo    📋 当前配置:
echo    ✓ 前端: 通过Maven构建并集成
echo    ✓ 后端: Spring Boot (端口8081)
echo    ✓ 模式: 生产模式 (无Vite开发服务器)
echo    ✓ 热重载: 仅后端代码 (DevTools)
echo    
echo    📝 开发说明:
echo    • 前端代码修改后需重新构建 (运行 build-integrated.bat)
echo    • 后端代码修改会自动重启
echo    • 所有请求通过同一端口 (8081)
echo    • 无需启动额外的开发服务器
echo    
echo    按任意键关闭此窗口...
echo ========================================

pause > nul