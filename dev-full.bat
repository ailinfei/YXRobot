@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot Full Development Environment
echo    (Frontend + Backend Integrated Mode)
echo ========================================
echo.

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=dev
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

REM 检查必要工具
echo 1. Check development environment...
where mvn > nul 2>&1
if %errorlevel% neq 0 (
    echo    Error: Maven not found, please install Maven and add to PATH
    pause
    exit /b 1
)

where node > nul 2>&1
if %errorlevel% neq 0 (
    echo    Warning: Node.js not found, will use Maven plugin to install
)

REM Check and stop processes using ports
echo 2. Check port usage...
netstat -ano | findstr :8081 > nul
if %errorlevel% == 0 (
    echo    Port 8081 is in use, stopping related processes...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
        taskkill /PID %%a /F > nul 2>&1
    )
    timeout /t 2 > nul
)

REM Clean previous builds
echo 3. Clean previous build files...
if exist "src\main\resources\static" (
    echo    Cleaning static resources directory...
    rmdir /s /q "src\main\resources\static" > nul 2>&1
)
if exist "src\frontend\dist" (
    echo    Cleaning frontend build directory...
    rmdir /s /q "src\frontend\dist" > nul 2>&1
)
if exist "target" (
    echo    Cleaning Maven build directory...
    rmdir /s /q "target" > nul 2>&1
)

REM Build frontend and package to Spring Boot
echo 4. Build frontend and integrate to Spring Boot...
echo    Executing full build process (Java compile + Frontend build + Resource copy)...
call mvn clean process-resources -q
if %errorlevel% neq 0 (
    echo    Error: Build process failed
    echo    Please check Maven configuration and frontend code
    pause
    exit /b 1
)

REM Verify build results
if not exist "src\main\resources\static\index.html" (
    echo    Error: Frontend build files not found, please check build process
    pause
    exit /b 1
)

echo    Build completed! Frontend resources integrated to Spring Boot

REM Start integrated Spring Boot application
echo 5. Start integrated application...
echo    Application will serve frontend pages and backend API on port 8081
echo    Starting, please wait...

start "YXRobot-Integrated" cmd /c "mvn spring-boot:run \"-Dspring.profiles.active=dev\" \"-Dspring-boot.run.jvmArguments=-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true -Xmx1024m -Dfile.encoding=UTF-8\""

REM Wait for application startup
echo    Waiting for application startup...
timeout /t 20 > nul

REM Try to open browser
echo    Trying to open browser...
start http://localhost:8081

echo.
echo ========================================
echo    Integrated development environment started!
echo ========================================
echo    Access URL: http://localhost:8081
echo    
echo    Frontend Pages: http://localhost:8081/
echo    Admin Backend:  http://localhost:8081/admin/
echo    Backend API:    http://localhost:8081/api/
echo    
echo    Notes:
echo    - Frontend and backend integrated on same port (8081)
echo    - Frontend resources packaged to Spring Boot static directory
echo    - Frontend code changes require re-running this script
echo    - Backend code changes will auto-restart (DevTools)
echo    
echo    Press any key to close this window...
echo ========================================

pause > nul