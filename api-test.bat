@echo off
echo ========================================
echo YXRobot API连接测试
echo ========================================
echo.

echo 1. 检查后端服务状态...
netstat -ano | findstr :8081
if %errorlevel% equ 0 (
    echo ✅ 后端服务正在运行 (端口8081)
) else (
    echo ❌ 后端服务未运行
    goto :end
)
echo.

echo 2. 测试产品API连接...
curl -s -o nul -w "HTTP状态码: %%{http_code}\n" "http://localhost:8081/api/admin/products?page=1&size=5"
echo.

echo 3. 测试新闻API连接...
curl -s -o nul -w "HTTP状态码: %%{http_code}\n" "http://localhost:8081/api/news?page=1&pageSize=5"
echo.

echo 4. 测试健康检查...
curl -s -o nul -w "HTTP状态码: %%{http_code}\n" "http://localhost:8081/actuator/health" 2>nul
if %errorlevel% neq 0 (
    echo 健康检查端点可能未启用
)
echo.

echo ========================================
echo 测试完成
echo ========================================
echo.
echo 如果看到HTTP状态码200，说明API连接正常
echo 如果看到其他状态码或连接失败，请检查：
echo 1. 后端服务是否正常启动
echo 2. 端口8081是否被占用
echo 3. 防火墙设置是否阻止连接
echo.

:end
pause