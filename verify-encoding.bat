@echo off
chcp 65001 >nul
echo ========================================
echo    XML编码验证工具
echo ========================================
echo.

echo 🔍 正在检查XML文件编码状态...

REM 运行综合检查器
call mvn exec:java -Dexec.mainClass="com.yxrobot.util.ComprehensiveXmlChecker" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 验证过程出错
    pause
    exit /b 1
)

echo.
echo 🧪 测试编译...
call mvn compile -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 编译失败，可能存在编码问题
    echo 💡 建议运行 fix-encoding.bat 进行修复
    pause
    exit /b 1
) else (
    echo ✅ 编译成功
)

echo.
echo ✅ 验证完成！
echo 💡 如发现问题，请运行 fix-encoding.bat 进行修复
echo.
pause