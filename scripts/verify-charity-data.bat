@echo off
chcp 65001 >nul
echo ========================================
echo 公益项目管理系统数据验证脚本
echo ========================================
echo.

:: 设置数据库连接参数
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=yxrobot
set DB_USER=root

:: 提示用户输入数据库密码
set /p DB_PASSWORD=请输入数据库密码: 

echo.
echo 开始验证数据...
echo.

:: 检查MySQL是否可用
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到MySQL命令行工具
    pause
    exit /b 1
)

:: 执行验证脚本
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < verify-charity-data.sql

echo.
echo 验证完成！
pause