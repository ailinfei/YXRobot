@echo off
REM 公益项目管理数据库初始化脚本
REM 创建时间: 2024-12-19
REM 维护人员: YXRobot开发团队
REM 说明: 该脚本用于初始化公益项目管理系统的数据库表和测试数据

echo ========================================
echo 公益项目管理数据库初始化脚本
echo ========================================
echo.

REM 设置数据库连接参数
set DB_HOST=yun.finiot.cn
set DB_PORT=3306
set DB_NAME=YXRobot
set DB_USER=YXRobot
set DB_PASSWORD=2200548qq

echo 正在连接数据库: %DB_HOST%:%DB_PORT%/%DB_NAME%
echo 用户名: %DB_USER%
echo.

REM 检查MySQL客户端是否可用
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到MySQL客户端，请确保MySQL已安装并添加到PATH环境变量中
    echo 请检查以下路径是否存在MySQL客户端:
    echo - mysql-9.3.0-winx64\bin\mysql.exe
    echo - 或其他MySQL安装路径
    pause
    exit /b 1
)

echo 步骤1: 创建公益项目管理数据库表...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < create-charity-tables.sql
if %errorlevel% neq 0 (
    echo 错误: 数据库表创建失败
    pause
    exit /b 1
)
echo 数据库表创建成功!
echo.

echo 步骤2: 插入初始化测试数据...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < insert-charity-data.sql
if %errorlevel% neq 0 (
    echo 错误: 测试数据插入失败
    pause
    exit /b 1
)
echo 测试数据插入成功!
echo.

echo 步骤3: 验证数据库初始化结果...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < verify-charity-data.sql
if %errorlevel% neq 0 (
    echo 警告: 数据验证脚本执行失败，但数据库初始化可能已完成
)
echo.

echo ========================================
echo 数据库初始化完成!
echo ========================================
echo.
echo 已创建的数据库表:
echo - charity_stats (公益统计数据表)
echo - charity_institutions (合作机构表)
echo - charity_activities (公益活动表)
echo - charity_projects (公益项目表)
echo - charity_stats_logs (统计数据更新日志表)
echo.
echo 已插入的测试数据:
echo - 1条统计数据记录
echo - 16条合作机构记录
echo - 12条公益项目记录
echo - 15条公益活动记录
echo - 4条更新日志记录
echo.
echo 现在可以启动应用程序进行测试!
echo.
pause