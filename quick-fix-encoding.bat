@echo off
chcp 65001 >nul
echo ========================================
echo    XML编码快速修复工具
echo ========================================
echo.

REM 设置项目路径
set PROJECT_PATH=%~dp0

echo 🔧 正在快速修复XML编码问题...

REM 直接运行最终修复工具
call mvn exec:java -Dexec.mainClass="com.yxrobot.util.XmlFinalFixRunner" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 修复失败
    pause
    exit /b 1
)

echo 🏗️ 重新编译项目...
call mvn clean compile -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 编译失败
    pause
    exit /b 1
)

echo.
echo ✅ 快速修复完成！
echo 💡 如需详细检查，请运行 fix-encoding.bat
echo.
pause