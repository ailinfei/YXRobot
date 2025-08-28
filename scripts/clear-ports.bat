@echo off
echo ========================================
echo YXRobot 端口清理工具
echo ========================================
echo.

echo [1/4] 检查当前端口占用情况...
netstat -ano | findstr :8081
if %errorlevel% == 0 (
    echo 发现端口8081被占用
) else (
    echo 端口8081当前未被占用
)
echo.

echo [2/4] 终止所有Java进程...
taskkill /f /im java.exe 2>nul
if %errorlevel% == 0 (
    echo Java进程已终止
) else (
    echo 未发现运行中的Java进程
)
echo.

echo [3/4] 等待进程完全释放...
timeout /t 3 /nobreak >nul

echo [4/4] 验证端口释放状态...
netstat -ano | findstr :8081
if %errorlevel% == 0 (
    echo ❌ 警告：端口8081仍被占用！
    echo 占用详情：
    netstat -ano | findstr :8081
    echo.
    echo 请手动检查并终止占用进程，或重启计算机
    pause
) else (
    echo ✅ 端口8081已完全释放
    echo 现在可以安全启动YXRobot项目
)
echo.

echo ========================================
echo 端口清理完成
echo ========================================
pause