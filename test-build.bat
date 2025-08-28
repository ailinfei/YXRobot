@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot 构建测试脚本
echo ========================================
echo.

REM 设置环境变量
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

echo 1. 清理构建目录...
if exist "src\main\resources\static" (
    rmdir /s /q "src\main\resources\static" > nul 2>&1
)
if exist "src\frontend\dist" (
    rmdir /s /q "src\frontend\dist" > nul 2>&1
)
if exist "target" (
    rmdir /s /q "target" > nul 2>&1
)

echo 2. 执行Maven构建...
echo    这将包括：Java编译 + 前端构建 + 资源复制
call mvn clean process-resources
if %errorlevel% neq 0 (
    echo    构建失败！
    pause
    exit /b 1
)

echo.
echo 3. 验证构建结果...
if exist "src\main\resources\static\index.html" (
    echo    ✅ 前端构建成功：找到 index.html
) else (
    echo    ❌ 前端构建失败：未找到 index.html
)

if exist "src\main\resources\static\js" (
    echo    ✅ JavaScript文件已生成
) else (
    echo    ❌ JavaScript文件未找到
)

if exist "src\main\resources\static\css" (
    echo    ✅ CSS文件已生成
) else (
    echo    ❌ CSS文件未找到
)

echo.
echo ========================================
echo    构建测试完成！
echo ========================================

pause