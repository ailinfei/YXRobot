@echo off
echo ========================================
echo 启动MySQL和YXRobot应用
echo ========================================

set MYSQL_PATH=E:\YXRobot\mysql-9.3.0-winx64\bin
set PROJECT_PATH=%~dp0..

echo 1. 检查MySQL服务状态...
tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I /N "mysqld.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo MySQL服务已在运行
) else (
    echo 启动MySQL服务...
    start "MySQL Server" /MIN "%MYSQL_PATH%\mysqld.exe" --console
    echo 等待MySQL启动...
    timeout /t 10 /nobreak >nul
)

echo 2. 测试MySQL连接...
"%MYSQL_PATH%\mysql.exe" -hlocalhost -P3306 -uroot -p123456 -e "SELECT 'MySQL连接成功!' as status;" 2>nul
if %errorlevel% equ 0 (
    echo MySQL连接成功！
) else (
    echo MySQL连接失败，请检查服务状态
    echo 手动启动MySQL命令：
    echo cd /d E:\YXRobot\mysql-9.3.0-winx64\bin
    echo mysqld.exe --console
    pause
    exit /b 1
)

echo 3. 初始化数据库（如果需要）...
"%MYSQL_PATH%\mysql.exe" -hlocalhost -P3306 -uroot -p123456 -e "CREATE DATABASE IF NOT EXISTS yxrobot DEFAULT CHARSET utf8mb4;" 2>nul

echo 4. 执行数据库初始化脚本...
if exist "%PROJECT_PATH%\scripts\simple-charity-init.sql" (
    "%MYSQL_PATH%\mysql.exe" -hlocalhost -P3306 -uroot -p123456 yxrobot < "%PROJECT_PATH%\scripts\simple-charity-init.sql"
    if %errorlevel% equ 0 (
        echo 数据库初始化成功！
    ) else (
        echo 数据库初始化失败，但继续启动应用...
    )
) else (
    echo 未找到数据库初始化脚本，跳过...
)

echo 5. 启动Spring Boot应用...
cd /d "%PROJECT_PATH%"
echo 正在启动应用，请稍候...
start "YXRobot Application" cmd /k "mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo 6. 等待应用启动...
timeout /t 15 /nobreak >nul

echo 7. 打开浏览器...
start http://localhost:8081

echo ========================================
echo 启动完成！
echo 应用地址: http://localhost:8081
echo MySQL控制台: http://localhost:8081/h2-console (如果启用)
echo ========================================
pause