@echo off
echo 启动MySQL服务...

set MYSQL_PATH=E:\YXRobot\mysql-9.3.0-winx64\bin

echo 启动MySQL服务器...
"%MYSQL_PATH%\mysqld.exe" --console

pause