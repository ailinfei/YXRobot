@echo off
chcp 65001 >nul
echo ========================================
echo 公益项目管理系统快速数据库设置
echo ========================================
echo.
echo 数据库信息:
echo - MySQL路径: E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe
echo - 主机: yun.finiot.cn:3306
echo - 数据库: YXRobot
echo - 用户: YXRobot
echo.

:: 设置MySQL路径
set MYSQL_PATH="E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe"

:: 检查MySQL是否存在
if not exist %MYSQL_PATH% (
    echo 错误: 未找到MySQL在路径 %MYSQL_PATH%
    echo 请确认MySQL安装路径是否正确
    pause
    exit /b 1
)

echo 测试数据库连接...
%MYSQL_PATH% -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SELECT 'Connection OK' as Status;"
if errorlevel 1 (
    echo 错误: 数据库连接失败
    pause
    exit /b 1
)
echo 数据库连接成功！
echo.

echo 1. 创建公益项目表结构...
%MYSQL_PATH% -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source create-charity-tables.sql"
if errorlevel 1 (
    echo 错误: 表结构创建失败
    pause
    exit /b 1
)
echo ✓ 表结构创建成功！

echo.
echo 2. 插入测试数据...
%MYSQL_PATH% -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source insert-charity-data.sql"
if errorlevel 1 (
    echo 错误: 数据插入失败
    pause
    exit /b 1
)
echo ✓ 测试数据插入成功！

echo.
echo 3. 验证数据完整性...
%MYSQL_PATH% -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SELECT COUNT(*) as '统计数据' FROM charity_stats; SELECT COUNT(*) as '合作机构' FROM charity_institutions; SELECT COUNT(*) as '公益项目' FROM charity_projects; SELECT COUNT(*) as '公益活动' FROM charity_activities;"

echo.
echo ========================================
echo ✓ 公益项目数据库设置完成！
echo ========================================
echo.
echo 已创建的表:
echo - charity_stats (公益统计数据)
echo - charity_institutions (合作机构)
echo - charity_projects (公益项目)
echo - charity_activities (公益活动)
echo - charity_stats_logs (更新日志)
echo.
echo 插入的测试数据:
echo - 统计数据: 1条记录
echo - 合作机构: 17个机构
echo - 公益项目: 12个项目
echo - 公益活动: 16个活动
echo - 更新日志: 4条记录
echo.
echo 现在可以启动Spring Boot应用程序进行测试！
echo.
pause