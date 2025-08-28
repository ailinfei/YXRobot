@echo off
setlocal enabledelayedexpansion
echo =====================================================
echo YXRobot Tomcat部署脚本
echo =====================================================

cd /d "%~dp0.."

REM 配置Tomcat路径（请根据实际情况修改）
set "TOMCAT_HOME=E:\YXRobot\tomcat9"
set "WEBAPPS_DIR=%TOMCAT_HOME%\webapps"
set "WAR_FILE=target\yxrobot.war"
set "APP_NAME=yxrobot"

echo 🚀 开始部署YXRobot到Tomcat...
echo.
echo 📋 部署配置：
echo   Tomcat目录: %TOMCAT_HOME%
echo   应用名称: %APP_NAME%
echo   WAR文件: %WAR_FILE%
echo.

REM 检查WAR文件是否存在
if not exist "%WAR_FILE%" (
    echo ❌ WAR文件不存在: %WAR_FILE%
    echo 请先运行构建脚本: scripts\build-all.bat
    pause
    exit /b 1
)

REM 检查Tomcat目录是否存在
if not exist "%TOMCAT_HOME%" (
    echo ❌ Tomcat目录不存在: %TOMCAT_HOME%
    echo 请修改脚本中的TOMCAT_HOME路径
    pause
    exit /b 1
)

REM 检查webapps目录
if not exist "%WEBAPPS_DIR%" (
    echo ❌ Tomcat webapps目录不存在: %WEBAPPS_DIR%
    pause
    exit /b 1
)

echo ✅ 环境检查通过
echo.

REM 停止Tomcat（如果正在运行）
echo 🛑 停止Tomcat服务器...
tasklist /FI "IMAGENAME eq java.exe" | find "java.exe" >nul
if !errorlevel! equ 0 (
    echo 发现Java进程，尝试停止Tomcat...
    if exist "%TOMCAT_HOME%\bin\shutdown.bat" (
        call "%TOMCAT_HOME%\bin\shutdown.bat"
        timeout /t 5 /nobreak >nul
    )
)

REM 清理旧的部署
echo 🧹 清理旧的部署文件...
if exist "%WEBAPPS_DIR%\%APP_NAME%" (
    echo 删除旧的应用目录: %WEBAPPS_DIR%\%APP_NAME%
    rmdir /s /q "%WEBAPPS_DIR%\%APP_NAME%"
)

if exist "%WEBAPPS_DIR%\%APP_NAME%.war" (
    echo 删除旧的WAR文件: %WEBAPPS_DIR%\%APP_NAME%.war
    del /q "%WEBAPPS_DIR%\%APP_NAME%.war"
)

REM 复制新的WAR文件
echo 📦 部署新的WAR文件...
copy "%WAR_FILE%" "%WEBAPPS_DIR%\%APP_NAME%.war"
if !errorlevel! equ 0 (
    echo ✅ WAR文件复制成功
) else (
    echo ❌ WAR文件复制失败
    pause
    exit /b 1
)

REM 启动Tomcat
echo 🚀 启动Tomcat服务器...
if exist "%TOMCAT_HOME%\bin\startup.bat" (
    start "Tomcat Server" "%TOMCAT_HOME%\bin\startup.bat"
    echo ✅ Tomcat启动命令已执行
) else (
    echo ❌ 找不到Tomcat启动脚本: %TOMCAT_HOME%\bin\startup.bat
    pause
    exit /b 1
)

echo.
echo 🎉 部署完成！
echo.
echo 📋 访问信息：
echo   应用地址: http://localhost:8080/%APP_NAME%
echo   管理后台: http://localhost:8080/%APP_NAME%/admin
echo   API接口: http://localhost:8080/%APP_NAME%/api/admin/products
echo.
echo 💡 提示：
echo   - 请等待约30-60秒让应用完全启动
echo   - 如果无法访问，请检查Tomcat日志
echo   - 日志位置: %TOMCAT_HOME%\logs\catalina.out
echo.

REM 等待一段时间后尝试打开浏览器
echo 等待应用启动...
timeout /t 15 /nobreak >nul

REM 尝试打开浏览器
choice /c YN /t 10 /d Y /m "是否打开浏览器访问应用"
if !errorlevel! equ 1 (
    start http://localhost:8080/%APP_NAME%
)

pause