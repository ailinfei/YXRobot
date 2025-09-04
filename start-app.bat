@echo off
chcp 65001 >nul
echo ========================================
echo    启动YXRobot应用
echo ========================================
echo.

echo 🚀 正在启动Spring Boot应用...
echo 端口: 8081
echo 数据库: 云数据库
echo.

REM 启动应用
java -jar target/yxrobot.war --server.port=8081 --spring.profiles.active=dev

echo.
echo 应用已停止
pause