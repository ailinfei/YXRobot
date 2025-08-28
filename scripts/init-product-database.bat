@echo off
chcp 65001 >nul
echo ========================================
echo YXRobot 产品管理模块数据库初始化
echo ========================================
echo.

:: 设置数据库连接参数
set DB_HOST=yun.finiot.cn
set DB_PORT=3306
set DB_NAME=YXRobot
set DB_USER=YXRobot
set DB_PASSWORD=2200548qq

echo 正在连接数据库...
echo 主机: %DB_HOST%:%DB_PORT%
echo 数据库: %DB_NAME%
echo 用户: %DB_USER%
echo.

:: 检查MySQL客户端是否可用
mysql --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到MySQL客户端
    echo 请确保MySQL客户端已安装并添加到PATH环境变量中
    echo.
    pause
    exit /b 1
)

:: 测试数据库连接
echo 测试数据库连接...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "SELECT 1;" >nul 2>&1
if errorlevel 1 (
    echo ❌ 数据库连接失败
    echo 请检查数据库连接参数是否正确
    echo.
    pause
    exit /b 1
)

echo ✅ 数据库连接成功
echo.

:: 执行数据库初始化脚本
echo 正在执行产品管理模块数据库初始化...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% < "%~dp0init-product-tables.sql"

if errorlevel 1 (
    echo ❌ 数据库初始化失败
    echo 请检查SQL脚本是否有语法错误
    echo.
    pause
    exit /b 1
)

echo ✅ 产品管理模块数据库初始化完成！
echo.
echo 已创建的表:
echo   - products (产品表)
echo   - product_media (产品媒体表)
echo.
echo 已插入测试数据:
echo   - 5个产品记录
echo   - 16个媒体文件记录
echo.

:: 验证数据
echo 验证数据插入结果...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "USE YXRobot; SELECT COUNT(*) as product_count FROM products WHERE is_deleted = 0; SELECT COUNT(*) as media_count FROM product_media WHERE is_deleted = 0;"

echo.
echo ========================================
echo 初始化完成！现在可以启动应用程序了。
echo ========================================
echo.
pause