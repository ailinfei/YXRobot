@echo off
echo ========================================
echo 订单管理系统集成测试执行脚本
echo ========================================
echo.

echo 🚀 开始执行订单管理系统集成测试...
echo.

echo 📋 测试环境信息:
echo - Java版本: %JAVA_VERSION%
echo - Maven版本: 
call mvn --version | findstr "Apache Maven"
echo - 数据库连接: yun.finiot.cn:3306/YXRobot
echo.

echo 🔧 准备测试环境...
echo 1. 检查数据库连接...
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SELECT 'Database connection OK' as status;"

if %ERRORLEVEL% NEQ 0 (
    echo ❌ 数据库连接失败，请检查数据库服务状态
    pause
    exit /b 1
)

echo ✅ 数据库连接正常
echo.

echo 2. 编译项目...
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ 项目编译失败
    pause
    exit /b 1
)
echo ✅ 项目编译成功
echo.

echo 🧪 执行集成测试...
echo.

echo ==========================================
echo 阶段1: 基础API集成测试
echo ==========================================
echo 执行订单CRUD API测试...
call mvn test -Dtest=OrderCrudApiIntegrationTest -q
echo.

echo 执行订单列表API测试...
call mvn test -Dtest=OrderListApiIntegrationTest -q
echo.

echo 执行订单状态API测试...
call mvn test -Dtest=OrderStatusApiIntegrationTest -q
echo.

echo 执行订单统计API测试...
call mvn test -Dtest=OrderStatsApiIntegrationTest -q
echo.

echo ==========================================
echo 阶段2: 系统功能集成测试
echo ==========================================
echo 执行系统基础集成测试...
call mvn test -Dtest=OrderSystemIntegrationTest -q
echo.

echo 执行系统全面集成测试...
call mvn test -Dtest=OrderSystemComprehensiveIntegrationTest -q
echo.

echo ==========================================
echo 阶段3: 部署验证测试
echo ==========================================
echo 执行部署验证测试...
call mvn test -Dtest=OrderSystemDeploymentValidationTest -q
echo.

echo ==========================================
echo 阶段4: 数据验证测试
echo ==========================================
echo 执行数据验证集成测试...
call mvn test -Dtest=OrderValidationIntegrationTest -q
echo.

echo ==========================================
echo 阶段5: 完整测试套件执行
echo ==========================================
echo 执行完整测试套件...
call mvn test -Dtest=OrderSystemIntegrationTestSuite
echo.

echo 📊 生成测试报告...
if exist target\surefire-reports (
    echo ✅ 测试报告已生成: target\surefire-reports\
    echo 📁 查看详细报告: target\surefire-reports\index.html
) else (
    echo ⚠️  测试报告目录不存在
)
echo.

echo 🎯 验证前端页面访问...
echo 📝 前端访问地址: http://localhost:8081/admin/business/orders
echo 💡 请手动验证前端页面功能是否正常
echo.

echo ========================================
echo 🎉 订单管理系统集成测试完成！
echo ========================================
echo.

echo 📋 测试结果摘要:
echo - 基础API测试: 已执行
echo - 系统功能测试: 已执行  
echo - 部署验证测试: 已执行
echo - 数据验证测试: 已执行
echo - 完整测试套件: 已执行
echo.

echo 🔍 如需查看详细测试结果，请检查:
echo - 控制台输出日志
echo - target\surefire-reports\ 目录下的测试报告
echo - 应用日志文件
echo.

echo 📝 下一步操作建议:
echo 1. 检查所有测试是否通过
echo 2. 验证前端页面功能: http://localhost:8081/admin/business/orders
echo 3. 检查数据库中的测试数据
echo 4. 验证API响应时间和性能指标
echo 5. 确认错误处理机制正常工作
echo.

pause