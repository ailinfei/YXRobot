@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot 完整重新构建和启动
echo ========================================
echo.

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=dev
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

echo 1. 停止现有进程...
netstat -ano | findstr :8081 > nul
if %errorlevel% == 0 (
    echo    停止端口8081上的进程...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
        taskkill /PID %%a /F > nul 2>&1
    )
    timeout /t 3 > nul
)

echo 2. 清理构建目录...
if exist "target" (
    echo    清理Maven构建目录...
    rmdir /s /q "target" > nul 2>&1
)
if exist "src\main\resources\static" (
    echo    清理静态资源目录...
    rmdir /s /q "src\main\resources\static" > nul 2>&1
)
if exist "src\frontend\dist" (
    echo    清理前端构建目录...
    rmdir /s /q "src\frontend\dist" > nul 2>&1
)
if exist "src\frontend\node_modules\.cache" (
    echo    清理前端缓存...
    rmdir /s /q "src\frontend\node_modules\.cache" > nul 2>&1
)

echo 3. 重新编译Java代码...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo    错误: Java编译失败
    pause
    exit /b 1
)

echo 4. 构建前端并集成到Spring Boot...
call mvn process-resources -q
if %errorlevel% neq 0 (
    echo    错误: 前端构建失败
    pause
    exit /b 1
)

echo 5. 验证构建结果...
if not exist "src\main\resources\static\index.html" (
    echo    错误: 前端构建文件未找到
    pause
    exit /b 1
)

echo 6. 启动应用...
echo    应用将在端口8081启动
echo    前端页面: http://localhost:8081/
echo    后端API: http://localhost:8081/api/
echo.

start "YXRobot-Rebuilt" cmd /c "mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments=\"-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true -Xmx1024m -Dfile.encoding=UTF-8\""

echo    等待应用启动...
timeout /t 15 > nul

echo    尝试打开浏览器...
start http://localhost:8081

echo.
echo ========================================
echo    重新构建完成！
echo ========================================
echo    如果仍有问题，请检查控制台日志
echo    按任意键关闭此窗口...
echo ========================================

pause > nul