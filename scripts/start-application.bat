@echo off
echo =====================================================
echo YXRobot 后端应用启动脚本
echo =====================================================

:: 设置Java环境变量
set JAVA_HOME=%~dp0..\..\..\jdk21
set PATH=%JAVA_HOME%\bin;%PATH%

:: 切换到项目根目录
cd /d "%~dp0.."

echo.
echo 正在检查Java版本...
java -version

echo.
echo 正在编译项目...
call mvn clean compile -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo ❌ 项目编译失败！
    pause
    exit /b 1
)

echo.
echo ✅ 项目编译成功！

echo.
echo 正在启动Spring Boot应用...
echo 应用将在 http://localhost:8080/api 启动
echo 按 Ctrl+C 停止应用

call mvn spring-boot:run -Dspring-boot.run.profiles=dev

pause