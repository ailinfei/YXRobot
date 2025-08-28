@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot Maven集成开发环境
echo    (纯Maven构建，前后端集成模式)
echo ========================================
echo.

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=dev
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

REM 检查Maven
echo 1. 检查Maven环境...
where mvn > nul 2>&1
if %errorlevel% neq 0 (
    echo    错误: 未找到Maven，请安装Maven并添加到PATH
    pause
    exit /b 1
)
echo    Maven已找到

REM 停止可能运行的Vite进程
echo 2. 停止Vite开发服务器...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173') do (
    echo    停止Vite进程 PID: %%a
    taskkill /PID %%a /F > nul 2>&1
)

REM 停止占用8081端口的进程
echo 3. 检查并清理端口8081...
netstat -ano | findstr :8081 > nul
if %errorlevel% == 0 (
    echo    端口8081被占用，停止相关进程...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
        echo    停止进程 PID: %%a
        taskkill /PID %%a /F > nul 2>&1
    )
    timeout /t 3 > nul
)

REM 清理之前的构建文件
echo 4. 清理构建文件...
if exist "src\main\resources\static" (
    echo    清理静态资源目录...
    rmdir /s /q "src\main\resources\static" > nul 2>&1
)
if exist "src\frontend\dist" (
    echo    清理前端构建目录...
    rmdir /s /q "src\frontend\dist" > nul 2>&1
)
if exist "target" (
    echo    清理Maven构建目录...
    rmdir /s /q "target" > nul 2>&1
)

REM 执行Maven完整构建
echo 5. 执行Maven完整构建...
echo    阶段1: 清理项目
call mvn clean -q
if %errorlevel% neq 0 (
    echo    错误: Maven清理失败
    pause
    exit /b 1
)

echo    阶段2: 安装Node.js和npm (通过Maven插件)
call mvn frontend:install-node-and-npm -q
if %errorlevel% neq 0 (
    echo    警告: Node.js安装可能失败，继续尝试...
)

echo    阶段3: 安装前端依赖
call mvn frontend:npm@"npm install" -q
if %errorlevel% neq 0 (
    echo    警告: 前端依赖安装可能失败，继续尝试...
)

echo    阶段4: 构建前端项目
call mvn frontend:npm@"npm run build" -q
if %errorlevel% neq 0 (
    echo    错误: 前端构建失败
    echo    尝试手动构建...
    cd src\frontend
    if exist "node_modules" (
        call npm run build
    ) else (
        call npm install
        call npm run build
    )
    cd ..\..
)

echo    阶段5: 复制前端资源到Spring Boot
call mvn process-resources -q
if %errorlevel% neq 0 (
    echo    错误: 资源处理失败
    pause
    exit /b 1
)

REM 验证构建结果
echo 6. 验证构建结果...
if not exist "src\main\resources\static\index.html" (
    echo    错误: 前端构建文件未找到
    echo    检查构建过程...
    if exist "src\frontend\dist\index.html" (
        echo    前端构建成功，但复制失败，手动复制...
        xcopy "src\frontend\dist\*" "src\main\resources\static\" /E /I /Y > nul
        if exist "src\main\resources\static\index.html" (
            echo    手动复制成功
        ) else (
            echo    手动复制失败
            pause
            exit /b 1
        )
    ) else (
        echo    前端构建失败，请检查前端代码
        pause
        exit /b 1
    )
)
echo    构建验证成功！

REM 编译Java代码
echo 7. 编译Java代码...
call mvn compile -q
if %errorlevel% neq 0 (
    echo    错误: Java编译失败
    pause
    exit /b 1
)

REM 启动Spring Boot应用
echo 8. 启动Spring Boot集成应用...
echo    应用将在端口8081提供前端页面和后端API
echo    启动中，请稍候...

start "YXRobot-Maven-Integrated" cmd /c "mvn spring-boot:run -Dspring.profiles.active=dev -Dspring-boot.run.jvmArguments=\"-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=false -Xmx1024m -Dfile.encoding=UTF-8\""

REM 等待应用启动
echo    等待应用启动...
timeout /t 25 > nul

REM 测试应用是否启动成功
echo 9. 测试应用启动状态...
curl -s -o nul -w "%%{http_code}" --connect-timeout 5 "http://localhost:8081" > temp_status.txt 2>&1
set /p status_code=<temp_status.txt
del temp_status.txt > nul 2>&1

if "%status_code%"=="200" (
    echo    应用启动成功！
    start http://localhost:8081
) else (
    echo    应用可能还在启动中，请稍后访问 http://localhost:8081
)

echo.
echo ========================================
echo    Maven集成开发环境启动完成！
echo ========================================
echo    访问地址: http://localhost:8081
echo    
echo    前端页面: http://localhost:8081/
echo    管理后台: http://localhost:8081/admin/
echo    后端API:  http://localhost:8081/api/
echo    
echo    重要说明:
echo    ✓ 前端通过Maven构建并集成到Spring Boot
echo    ✓ 前后端运行在同一端口 (8081)
echo    ✓ 无需启动Vite开发服务器
echo    ✓ 前端代码修改需重新运行此脚本
echo    ✓ 后端代码修改会自动重启 (DevTools)
echo    
echo    如需修改前端代码，请:
echo    1. 修改 src/frontend/ 下的文件
echo    2. 重新运行此脚本进行构建
echo    
echo    按任意键关闭此窗口...
echo ========================================

pause > nul