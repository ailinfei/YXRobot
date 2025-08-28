@echo off
echo 启动YXRobot应用...

echo 正在启动Spring Boot应用，使用H2内存数据库...
cd /d E:\YXRobot\workspace\projects\YXRobot

REM 使用H2数据库配置启动
java -jar target\yxrobot-1.0.0.war --spring.profiles.active=h2 --server.port=8081

pause