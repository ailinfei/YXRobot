@echo off
chcp 65001 >nul
echo ========================================
echo    测试YXRobot应用状态
echo ========================================
echo.

echo 🔍 检查Java进程...
tasklist | findstr java
echo.

echo 🔍 检查8081端口监听...
netstat -an | findstr ":8081.*LISTENING"
if %errorlevel%==0 (
    echo ✅ 8081端口正在监听
) else (
    echo ❌ 8081端口未监听
)
echo.

echo 🔍 测试HTTP连接...
curl -s -m 5 http://localhost:8081 >nul 2>&1
if %errorlevel%==0 (
    echo ✅ HTTP连接成功
) else (
    echo ❌ HTTP连接失败
)
echo.

echo 🔍 测试API端点...
curl -s -m 5 http://localhost:8081/api/admin/charity/stats >nul 2>&1
if %errorlevel%==0 (
    echo ✅ API端点响应
) else (
    echo ❌ API端点无响应
)
echo.

echo 💡 建议:
echo 1. 如果Java进程存在但端口未监听，可能是启动失败
echo 2. 检查应用日志: logs/yxrobot-backend.log
echo 3. 重新启动: mvn spring-boot:run
echo.

pause