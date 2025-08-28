@echo off
echo 启动YXRobot Spring Boot应用...
cd /d "%~dp0"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
pause