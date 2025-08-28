@echo off
echo 正在执行公益项目数据库脚本...

:: 设置MySQL路径（项目专用）
set MYSQL_PATH="E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe"

:: 检查MySQL是否存在
if not exist %MYSQL_PATH% (
    echo 错误: 未找到MySQL在路径 %MYSQL_PATH%
    echo 请确认MySQL安装路径是否正确
    pause
    exit /b 1
)

echo 使用MySQL路径: %MYSQL_PATH%

echo 1. 创建公益项目表结构...
%MYSQL_PATH% -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source create-charity-tables.sql"
if errorlevel 1 (
    echo 错误: 表结构创建失败
    pause
    exit /b 1
)
echo 表结构创建成功！

echo 2. 插入测试数据...
%MYSQL_PATH% -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source insert-charity-data.sql"
if errorlevel 1 (
    echo 错误: 数据插入失败
    pause
    exit /b 1
)
echo 数据插入成功！

echo 3. 验证数据...
%MYSQL_PATH% -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source verify-charity-data.sql"

echo 公益项目数据库初始化完成！
pause