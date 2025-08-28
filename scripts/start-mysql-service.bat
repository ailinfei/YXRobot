@echo off
echo 启动MySQL服务...

set MYSQL_PATH=E:\YXRobot\mysql-9.3.0-winx64

echo 正在启动MySQL服务器...
echo 请在新的命令行窗口中运行以下命令：
echo.
echo cd /d E:\YXRobot\mysql-9.3.0-winx64\bin
echo mysqld.exe --console
echo.
echo 或者使用Windows服务方式启动：
echo net start mysql
echo.
echo 启动后请按任意键继续...
pause

echo 测试MySQL连接...
"%MYSQL_PATH%\bin\mysql.exe" -hlocalhost -P3306 -uroot -p123456 -e "SELECT 'MySQL连接成功!' as status;"

if %errorlevel% equ 0 (
    echo MySQL服务启动成功！
) else (
    echo MySQL服务启动失败或连接失败！
    echo 请检查：
    echo 1. MySQL服务是否正在运行
    echo 2. 用户名密码是否正确
    echo 3. 端口3306是否被占用
)

pause