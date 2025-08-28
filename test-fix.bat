@echo off
chcp 65001 > nul
echo ========================================
echo    测试API修复效果
echo ========================================
echo.

echo 1. 检查后端服务状态...
netstat -ano | findstr :8081 > nul
if %errorlevel% neq 0 (
    echo    后端服务未运行，正在启动...
    start "YXRobot-Backend" cmd /c "cd /d %~dp0 && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
    echo    等待后端启动...
    timeout /t 15 > nul
) else (
    echo    ✓ 后端服务已运行
)

echo.
echo 2. 测试API连接...
echo    测试产品API...
curl -s -o nul -w "产品API状态码: %%{http_code}\n" "http://localhost:8081/api/admin/products?page=1&size=5"

echo    测试新闻API...
curl -s -o nul -w "新闻API状态码: %%{http_code}\n" "http://localhost:8081/api/news?page=1&pageSize=5"

echo.
echo 3. 打开浏览器测试...
echo    正在打开管理后台...
start http://localhost:8081/admin/content/products
timeout /t 2 > nul
start http://localhost:8081/admin/content/charity
timeout /t 2 > nul
start http://localhost:8081/admin/content/news

echo.
echo ========================================
echo    测试完成！
echo ========================================
echo    
echo    请在浏览器中检查以下页面：
echo    ✓ 产品管理: http://localhost:8081/admin/content/products
echo    ✓ 公益管理: http://localhost:8081/admin/content/charity  
echo    ✓ 新闻管理: http://localhost:8081/admin/content/news
echo    
echo    如果仍有问题，请检查浏览器控制台错误信息
echo    
pause