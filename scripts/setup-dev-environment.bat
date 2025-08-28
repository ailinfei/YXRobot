@echo off
REM 公益项目管理开发环境配置脚本
REM 创建时间: 2024-12-19
REM 维护人员: YXRobot开发团队
REM 说明: 该脚本用于配置和验证公益项目管理系统的开发环境

echo ========================================
echo 公益项目管理开发环境配置脚本
echo ========================================
echo.

REM 创建必要的目录结构
echo 步骤1: 创建开发环境目录结构...
if not exist "logs" mkdir logs
if not exist "uploads" mkdir uploads
if not exist "uploads\charity" mkdir uploads\charity
if not exist "uploads\charity\images" mkdir uploads\charity\images
if not exist "uploads\charity\documents" mkdir uploads\charity\documents
if not exist "temp" mkdir temp
echo 目录结构创建完成!
echo.

REM 检查Java环境
echo 步骤2: 检查Java开发环境...
java -version >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到Java运行环境
    echo 请确保已安装JDK 11或更高版本
    pause
    exit /b 1
)
echo Java环境检查通过!
echo.

REM 检查Maven环境
echo 步骤3: 检查Maven构建环境...
mvn -version >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven构建工具
    echo 请确保已安装Maven并添加到PATH环境变量
    pause
    exit /b 1
)
echo Maven环境检查通过!
echo.

REM 检查MySQL连接
echo 步骤4: 检查MySQL数据库连接...
mysql -hyun.finiot.cn -P3306 -uYXRobot -p2200548qq -e "SELECT 1;" YXRobot >nul 2>nul
if %errorlevel% neq 0 (
    echo 警告: MySQL数据库连接失败
    echo 请检查数据库服务器是否可访问
    echo 数据库地址: yun.finiot.cn:3306
    echo 数据库名称: YXRobot
    echo.
) else (
    echo MySQL数据库连接正常!
)
echo.

REM 检查Redis服务
echo 步骤5: 检查Redis缓存服务...
redis-cli ping >nul 2>nul
if %errorlevel% neq 0 (
    echo 警告: Redis服务未运行
    echo 可以使用 start-redis-dev.bat 启动Redis服务
    echo 或在application-dev.yml中禁用缓存: spring.cache.type: none
    echo.
) else (
    echo Redis缓存服务正常!
)
echo.

REM 检查Node.js环境（前端开发）
echo 步骤6: 检查Node.js前端环境...
node -v >nul 2>nul
if %errorlevel% neq 0 (
    echo 警告: 未找到Node.js环境
    echo 前端开发需要Node.js 18或更高版本
    echo.
) else (
    echo Node.js环境检查通过!
)
echo.

REM 编译项目
echo 步骤7: 编译项目...
echo 正在执行Maven编译 (跳过前端构建)...
mvn clean compile -Pskip-frontend -q
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    echo 请检查代码是否有语法错误
    pause
    exit /b 1
)
echo 项目编译成功!
echo.

REM 运行测试
echo 步骤8: 运行单元测试...
echo 正在执行公益项目管理相关测试...
mvn test -Dtest=*Charity* -q
if %errorlevel% neq 0 (
    echo 警告: 部分测试失败，但不影响开发环境配置
    echo 可以稍后查看测试报告进行修复
)
echo 测试执行完成!
echo.

echo ========================================
echo 开发环境配置完成!
echo ========================================
echo.
echo 环境配置摘要:
echo ✓ 目录结构已创建
echo ✓ Java环境正常
echo ✓ Maven构建工具正常
echo ✓ 项目编译成功
echo.
echo 下一步操作:
echo 1. 初始化数据库: init-charity-database.bat
echo 2. 启动Redis服务: start-redis-dev.bat (可选)
echo 3. 启动应用程序: dev-start-safe.bat
echo 4. 访问管理后台: http://localhost:8081/admin
echo.
echo 开发文档位置:
echo - API文档: docs/charity-api-documentation.md
echo - 开发指南: DEVELOPMENT-GUIDE.md
echo - 错误处理: docs/ERROR-HANDLING-GUIDE.md
echo.
pause