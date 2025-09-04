@echo off
chcp 65001 >nul
echo ========================================
echo    启动YXRobot开发服务器
echo ========================================
echo.

echo 🚀 正在启动应用...
echo 访问地址: http://localhost:8081
echo 管理后台: http://localhost:8081/admin/content/charity
echo.

REM 启动Maven开发服务器
mvn spring-boot:run

pause