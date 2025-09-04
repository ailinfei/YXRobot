@echo off
chcp 65001 >nul
echo ========================================
echo    YXRobot应用诊断工具
echo ========================================
echo.

echo 🔍 检查Java进程...
tasklist | findstr java
echo.

echo 🔍 检查8081端口...
netstat -an | findstr :8081
echo.

echo 🔍 检查应用文件...
if exist "target\yxrobot.war" (
    echo ✅ WAR文件存在
) else (
    echo ❌ WAR文件不存在，需要运行: mvn package
)
echo.

echo 🔍 检查前端构建...
if exist "src\main\resources\static\index.html" (
    echo ✅ 前端文件存在
) else (
    echo ❌ 前端文件不存在，需要构建前端
)
echo.

echo 🔍 测试数据库连接...
echo 数据库地址: yun.finiot.cn:3306
ping -n 1 yun.finiot.cn >nul
if %errorlevel%==0 (
    echo ✅ 数据库服务器可达
) else (
    echo ❌ 数据库服务器不可达
)
echo.

echo 💡 建议操作:
echo 1. 运行 mvn spring-boot:run 启动应用
echo 2. 等待应用完全启动（看到Tomcat started消息）
echo 3. 访问 http://localhost:8081/admin/content/charity
echo.

pause