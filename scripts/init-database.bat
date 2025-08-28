@echo off
echo =====================================================
echo YXRobot 数据库初始化脚本
echo =====================================================

set MYSQL_HOST=yun.finiot.cn
set MYSQL_PORT=3306
set MYSQL_DATABASE=yxsql
set MYSQL_USER=yxsql
set MYSQL_PASSWORD=2200548qq

echo 正在连接云数据库...
echo 服务器: %MYSQL_HOST%:%MYSQL_PORT%
echo 数据库: %MYSQL_DATABASE%
echo 用户: %MYSQL_USER%
echo.

echo 正在执行数据库初始化脚本...

mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < init-database.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ 数据库初始化成功！
    echo ✓ 产品表已创建
    echo ✓ 测试数据已插入
) else (
    echo.
    echo ✗ 数据库初始化失败！
    echo 请检查网络连接和数据库配置
)

echo.
pause