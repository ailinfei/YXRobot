@echo off
REM =====================================================
REM 租赁数据分析模块 - 快速设置脚本
REM 自动创建数据库表结构并插入初始化数据
REM 创建时间: 2025-01-28
REM =====================================================

echo ========================================
echo 租赁数据分析模块 - 快速数据库设置
echo ========================================
echo.

echo [1/3] 创建租赁数据库表结构...
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source workspace/projects/YXRobot/scripts/create-rental-tables.sql"

if %ERRORLEVEL% NEQ 0 (
    echo 错误：数据库表创建失败！
    pause
    exit /b 1
)

echo [2/3] 插入初始化数据...
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source workspace/projects/YXRobot/scripts/insert-rental-initial-data.sql"

if %ERRORLEVEL% NEQ 0 (
    echo 错误：初始化数据插入失败！
    pause
    exit /b 1
)

echo [3/3] 验证数据库设置...
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source workspace/projects/YXRobot/scripts/verify-rental-database.sql"

if %ERRORLEVEL% NEQ 0 (
    echo 错误：数据库验证失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo 租赁数据分析模块数据库设置完成！
echo ========================================
echo.
echo 已创建的表：
echo - rental_records (租赁记录表)
echo - rental_devices (租赁设备表) 
echo - device_utilization (设备利用率表)
echo - rental_stats (租赁统计表)
echo - rental_customers (租赁客户表)
echo.
echo 已插入的数据：
echo - 12台租赁设备
echo - 8个租赁客户
echo - 13条租赁记录
echo - 3条统计数据
echo - 12条设备利用率数据
echo.
echo 现在可以启动项目测试租赁分析功能：
echo mvn spring-boot:run
echo.
pause