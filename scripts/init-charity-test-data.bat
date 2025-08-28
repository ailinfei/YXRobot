@echo off
echo 初始化公益统计测试数据...

REM 设置数据库连接参数
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=yxrobot
set DB_USER=root
set DB_PASS=123456
set MYSQL_PATH=E:\YXRobot\mysql-9.3.0-winx64\bin

echo 连接数据库并执行初始化脚本...

REM 执行简化的初始化脚本
"%MYSQL_PATH%\mysql.exe" -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% < simple-charity-init.sql

if %errorlevel% equ 0 (
    echo 公益统计数据表初始化成功！
) else (
    echo 公益统计数据表初始化失败！
    pause
    exit /b 1
)

echo 初始化完成！
pause