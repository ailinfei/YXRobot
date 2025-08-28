@echo off
echo 使用指定MySQL初始化公益统计数据...

set MYSQL_PATH=E:\YXRobot\mysql-9.3.0-winx64\bin
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=yxrobot
set DB_USER=root

echo 连接到MySQL数据库...
echo 请输入MySQL root密码：

"%MYSQL_PATH%\mysql.exe" -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p %DB_NAME% < simple-charity-init.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 公益统计数据初始化成功！
    echo ========================================
    echo.
) else (
    echo.
    echo ========================================
    echo 公益统计数据初始化失败！
    echo 请检查MySQL服务是否启动
    echo 请检查数据库连接参数是否正确
    echo ========================================
    echo.
)

pause