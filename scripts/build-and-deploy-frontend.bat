@echo off
echo ========================================
echo 前端构建和部署脚本
echo ========================================

cd /d "%~dp0\..\src\frontend"

echo 1. 安装依赖...
call npm install

echo 2. 构建前端项目...
call npm run build

if %ERRORLEVEL% neq 0 (
    echo 前端构建失败！
    pause
    exit /b 1
)

echo 3. 复制构建文件到Spring Boot静态资源目录...
cd /d "%~dp0\.."
powershell -Command "Copy-Item -Path 'src\frontend\dist\*' -Destination 'src\main\resources\static\' -Recurse -Force"

echo 4. 验证文件复制...
if exist "src\main\resources\static\index.html" (
    echo ✅ 前端文件复制成功！
) else (
    echo ❌ 前端文件复制失败！
    pause
    exit /b 1
)

echo ========================================
echo 前端构建和部署完成！
echo 现在可以访问: http://localhost:8080/yxrobot/
echo ========================================
pause