@echo off
echo ========================================
echo 重启YXRobot应用程序
echo ========================================

echo 1. 停止现有的Java进程...
taskkill /f /im java.exe 2>nul
timeout /t 3 >nul

echo 2. 启动应用程序...
cd /d "%~dp0\.."

echo 选择启动方式:
echo [1] 使用Maven启动 (推荐)
echo [2] 使用JAR文件启动
set /p choice="请选择 (1 或 2): "

if "%choice%"=="1" (
    echo 使用Maven启动...
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
) else if "%choice%"=="2" (
    echo 检查JAR文件...
    if exist "target\yxrobot.war" (
        echo 使用JAR文件启动...
        java -jar target\yxrobot.war --spring.profiles.active=dev
    ) else (
        echo JAR文件不存在，先构建项目...
        mvn clean package -DskipTests
        if exist "target\yxrobot.war" (
            java -jar target\yxrobot.war --spring.profiles.active=dev
        ) else (
            echo 构建失败，请检查项目配置
        )
    )
) else (
    echo 无效选择，使用默认Maven启动...
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
)

pause