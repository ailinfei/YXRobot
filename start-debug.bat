@echo off
echo 正在启动YXRobot应用...
echo 数据库连接: yun.finiot.cn:3306/YXRobot
echo 用户名: YXRobot
echo.

cd /d "%~dp0"
mvn spring-boot:run -Dspring-boot.run.fork=false

pause