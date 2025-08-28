@echo off
echo 初始化MySQL数据库...

set MYSQL_HOME=E:\YXRobot\mysql-9.3.0-winx64
set MYSQL_DATA=%MYSQL_HOME%\data

echo MySQL安装目录: %MYSQL_HOME%
echo MySQL数据目录: %MYSQL_DATA%

REM 停止可能运行的MySQL进程
echo 停止现有MySQL进程...
taskkill /f /im mysqld.exe 2>nul

REM 等待进程完全停止
timeout /t 3 /nobreak >nul

REM 检查数据目录是否存在，如果不存在则创建
if not exist "%MYSQL_DATA%" (
    echo 创建数据目录: %MYSQL_DATA%
    mkdir "%MYSQL_DATA%"
)

REM 初始化MySQL数据目录
echo 初始化MySQL数据目录...
"%MYSQL_HOME%\bin\mysqld.exe" --initialize-insecure --basedir="%MYSQL_HOME%" --datadir="%MYSQL_DATA%"

if %errorlevel% neq 0 (
    echo MySQL初始化失败！
    pause
    exit /b 1
)

echo MySQL初始化成功！

REM 启动MySQL服务
echo 启动MySQL服务...
start "MySQL Server" "%MYSQL_HOME%\bin\mysqld.exe" --console

REM 等待MySQL启动
echo 等待MySQL启动...
timeout /t 10 /nobreak >nul

REM 测试连接
echo 测试MySQL连接...
"%MYSQL_HOME%\bin\mysql.exe" -hlocalhost -P3306 -uroot -e "SELECT 'MySQL连接成功!' as status;"

if %errorlevel% equ 0 (
    echo MySQL启动成功！
    echo 创建yxrobot数据库...
    "%MYSQL_HOME%\bin\mysql.exe" -hlocalhost -P3306 -uroot -e "CREATE DATABASE IF NOT EXISTS yxrobot DEFAULT CHARSET utf8mb4;"
    
    echo 设置root密码...
    "%MYSQL_HOME%\bin\mysql.exe" -hlocalhost -P3306 -uroot -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';"
    
    echo MySQL配置完成！
) else (
    echo MySQL启动失败！
)

pause