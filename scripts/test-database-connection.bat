@echo off
echo =====================================================
echo YXRobot 数据库连接测试
echo 服务器: yun.finiot.cn:3306
echo 数据库: YXRobot
echo 用户名: YXRobot
echo =====================================================

echo.
echo 正在测试数据库连接...

:: 使用MySQL客户端测试连接
mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq -D YXRobot -e "SELECT 'Database connection successful!' as status, NOW() as current_time;"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ 数据库连接成功！
    echo.
    echo 正在检查表结构...
    mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq -D YXRobot -e "SHOW TABLES;"
    
    echo.
    echo 正在检查产品数据...
    mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq -D YXRobot -e "SELECT COUNT(*) as product_count FROM products;"
) else (
    echo.
    echo ❌ 数据库连接失败！
    echo 请检查网络连接和数据库配置。
)

echo.
pause