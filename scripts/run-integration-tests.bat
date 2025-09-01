@echo off
echo ========================================
echo 客户API集成测试执行脚本
echo ========================================

:: 设置环境变量
set API_BASE_URL=http://localhost:8081
set NODE_ENV=test

:: 检查Node.js是否安装
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到Node.js，请先安装Node.js
    pause
    exit /b 1
)

:: 检查npm是否可用
npm --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到npm，请检查Node.js安装
    pause
    exit /b 1
)

echo 📋 环境信息:
echo Node.js版本: 
node --version
echo npm版本: 
npm --version
echo API地址: %API_BASE_URL%
echo.

:: 进入前端目录
cd /d "%~dp0\..\workspace\projects\YXRobot\src\frontend"

:: 检查是否存在package.json
if not exist "package.json" (
    echo ❌ 错误: 未找到package.json文件，请检查路径
    pause
    exit /b 1
)

:: 安装依赖（如果需要）
echo 🔍 检查依赖...
if not exist "node_modules" (
    echo 📦 安装依赖...
    npm install
    if errorlevel 1 (
        echo ❌ 依赖安装失败
        pause
        exit /b 1
    )
)

:: 检查后端服务状态
echo 🔍 检查后端服务状态...
curl -s -o nul -w "%%{http_code}" %API_BASE_URL%/api/admin/customers/stats > temp_status.txt
set /p HTTP_STATUS=<temp_status.txt
del temp_status.txt

if "%HTTP_STATUS%" neq "200" (
    echo ⚠️  警告: 后端服务可能未启动 (HTTP状态: %HTTP_STATUS%)
    echo 请确保后端服务正在运行: mvn spring-boot:run
    echo.
    echo 是否继续运行测试? (y/n)
    set /p CONTINUE=
    if /i "%CONTINUE%" neq "y" (
        echo 测试已取消
        pause
        exit /b 0
    )
) else (
    echo ✅ 后端服务运行正常
)

echo.
echo 🚀 开始执行集成测试...
echo ========================================

:: 运行集成测试
echo 📋 1. API接口完整性测试...
npx vitest run src/__tests__/integration/customerApiIntegration.test.ts --reporter=verbose
set TEST1_RESULT=%errorlevel%

echo.
echo 📋 2. 字段映射验证测试...
npx vitest run src/__tests__/validation/fieldMappingValidation.test.ts --reporter=verbose
set TEST2_RESULT=%errorlevel%

echo.
echo 📋 3. API性能验证测试...
npx vitest run src/__tests__/performance/apiPerformanceValidation.test.ts --reporter=verbose
set TEST3_RESULT=%errorlevel%

echo.
echo ========================================
echo 📊 测试结果总结:
echo ========================================

:: 检查测试结果
set TOTAL_TESTS=3
set PASSED_TESTS=0

if %TEST1_RESULT% equ 0 (
    echo ✅ API接口完整性测试: 通过
    set /a PASSED_TESTS+=1
) else (
    echo ❌ API接口完整性测试: 失败
)

if %TEST2_RESULT% equ 0 (
    echo ✅ 字段映射验证测试: 通过
    set /a PASSED_TESTS+=1
) else (
    echo ❌ 字段映射验证测试: 失败
)

if %TEST3_RESULT% equ 0 (
    echo ✅ API性能验证测试: 通过
    set /a PASSED_TESTS+=1
) else (
    echo ❌ API性能验证测试: 失败
)

echo.
echo 通过率: %PASSED_TESTS%/%TOTAL_TESTS%

if %PASSED_TESTS% equ %TOTAL_TESTS% (
    echo.
    echo 🎉 所有集成测试通过！
    echo ✅ 前后端API对接验证成功
    echo ✅ 数据格式匹配正确
    echo ✅ 字段映射一致
    echo ✅ 性能要求满足
    echo.
    echo 📋 验证完成的功能:
    echo   • 客户列表查询和分页
    echo   • 客户统计数据获取
    echo   • 客户CRUD操作
    echo   • 客户关联数据查询
    echo   • 搜索和筛选功能
    echo   • 错误处理机制
    echo   • API响应性能
    echo.
) else (
    echo.
    echo ⚠️  部分测试失败，需要检查和修复
    echo.
    echo 🔧 可能的解决方案:
    echo   1. 检查后端服务是否正常运行
    echo   2. 验证数据库连接和数据
    echo   3. 检查API接口实现
    echo   4. 验证字段映射配置
    echo   5. 优化API响应性能
    echo.
)

echo 测试完成时间: %date% %time%
echo.
pause