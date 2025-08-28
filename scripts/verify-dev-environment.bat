@echo off
REM 公益项目管理开发环境验证脚本
REM 创建时间: 2024-12-19
REM 维护人员: YXRobot开发团队
REM 说明: 该脚本用于验证公益项目管理系统开发环境的完整性

echo ========================================
echo 公益项目管理开发环境验证脚本
echo ========================================
echo.

set VERIFICATION_PASSED=true

REM 验证Java环境
echo 步骤1: 验证Java环境...
java -version >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Java环境验证失败
    set VERIFICATION_PASSED=false
) else (
    echo ✅ Java环境验证通过
    java -version 2>&1 | findstr "version"
)
echo.

REM 验证Maven环境
echo 步骤2: 验证Maven环境...
mvn -version >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Maven环境验证失败
    set VERIFICATION_PASSED=false
) else (
    echo ✅ Maven环境验证通过
    mvn -version 2>&1 | findstr "Apache Maven"
)
echo.

REM 验证MySQL连接
echo 步骤3: 验证MySQL数据库连接...
mysql -hyun.finiot.cn -P3306 -uYXRobot -p2200548qq -e "SELECT 'MySQL连接正常' as status;" YXRobot >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ MySQL数据库连接失败
    echo    请检查数据库服务器是否可访问
    echo    数据库地址: yun.finiot.cn:3306
    echo    数据库名称: YXRobot
    set VERIFICATION_PASSED=false
) else (
    echo ✅ MySQL数据库连接正常
)
echo.

REM 验证数据库表结构
echo 步骤4: 验证公益项目数据库表...
mysql -hyun.finiot.cn -P3306 -uYXRobot -p2200548qq -e "SHOW TABLES LIKE 'charity_%';" YXRobot > temp_tables.txt 2>nul
if %errorlevel% neq 0 (
    echo ❌ 数据库表验证失败
    set VERIFICATION_PASSED=false
) else (
    set table_count=0
    for /f %%i in (temp_tables.txt) do set /a table_count+=1
    if %table_count% geq 5 (
        echo ✅ 公益项目数据库表验证通过 ^(%table_count%个表^)
    ) else (
        echo ❌ 公益项目数据库表不完整 ^(发现%table_count%个表，期望5个^)
        echo    请运行 init-charity-database.bat 初始化数据库
        set VERIFICATION_PASSED=false
    )
)
if exist temp_tables.txt del temp_tables.txt
echo.

REM 验证Redis服务
echo 步骤5: 验证Redis缓存服务...
redis-cli ping >nul 2>nul
if %errorlevel% neq 0 (
    echo ⚠️  Redis服务未运行 ^(可选^)
    echo    可以使用 start-redis-dev.bat 启动Redis服务
    echo    或在application-dev.yml中禁用缓存
) else (
    echo ✅ Redis缓存服务正常
)
echo.

REM 验证项目编译
echo 步骤6: 验证项目编译...
echo 正在编译项目 ^(跳过前端构建^)...
mvn clean compile -Pskip-frontend -q >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 项目编译失败
    echo    请检查代码是否有语法错误
    set VERIFICATION_PASSED=false
) else (
    echo ✅ 项目编译成功
)
echo.

REM 验证核心类文件
echo 步骤7: 验证核心类文件...
set core_classes=0
if exist "src\main\java\com\yxrobot\controller\CharityController.java" set /a core_classes+=1
if exist "src\main\java\com\yxrobot\service\CharityService.java" set /a core_classes+=1
if exist "src\main\java\com\yxrobot\mapper\CharityStatsMapper.java" set /a core_classes+=1
if exist "src\main\java\com\yxrobot\entity\CharityStats.java" set /a core_classes+=1

if %core_classes% geq 4 (
    echo ✅ 核心类文件验证通过 ^(%core_classes%/4^)
) else (
    echo ❌ 核心类文件不完整 ^(%core_classes%/4^)
    set VERIFICATION_PASSED=false
)
echo.

REM 验证配置文件
echo 步骤8: 验证配置文件...
set config_files=0
if exist "src\main\resources\application-dev.yml" set /a config_files+=1
if exist "src\main\resources\logback-spring.xml" set /a config_files+=1
if exist "src\main\resources\mapper\CharityStatsMapper.xml" set /a config_files+=1

if %config_files% geq 3 (
    echo ✅ 配置文件验证通过 ^(%config_files%/3^)
) else (
    echo ❌ 配置文件不完整 ^(%config_files%/3^)
    set VERIFICATION_PASSED=false
)
echo.

REM 验证测试文件
echo 步骤9: 验证测试文件...
set test_files=0
if exist "src\test\java\com\yxrobot\service\CharityServiceTest.java" set /a test_files+=1
if exist "src\test\java\com\yxrobot\controller\CharityControllerTest.java" set /a test_files+=1
if exist "src\test\java\com\yxrobot\integration\CharityIntegrationTest.java" set /a test_files+=1

if %test_files% geq 3 (
    echo ✅ 测试文件验证通过 ^(%test_files%/3^)
) else (
    echo ⚠️  测试文件不完整 ^(%test_files%/3^)
    echo    建议补充完整的测试用例
)
echo.

REM 验证文档文件
echo 步骤10: 验证文档文件...
set doc_files=0
if exist "docs\charity-api-documentation.md" set /a doc_files+=1
if exist "docs\charity-development-guide.md" set /a doc_files+=1

if %doc_files% geq 2 (
    echo ✅ 文档文件验证通过 ^(%doc_files%/2^)
) else (
    echo ⚠️  文档文件不完整 ^(%doc_files%/2^)
)
echo.

REM 验证日志目录
echo 步骤11: 验证日志目录...
if not exist "logs" mkdir logs
if exist "logs" (
    echo ✅ 日志目录验证通过
) else (
    echo ❌ 日志目录创建失败
    set VERIFICATION_PASSED=false
)
echo.

REM 运行快速测试
echo 步骤12: 运行快速测试...
echo 正在运行公益项目相关测试...
mvn test -Dtest=*Charity*Test -q >nul 2>nul
if %errorlevel% neq 0 (
    echo ⚠️  部分测试失败，但不影响开发环境
    echo    可以稍后查看测试报告进行修复
) else (
    echo ✅ 快速测试通过
)
echo.

REM 输出验证结果
echo ========================================
echo 开发环境验证结果
echo ========================================
echo.

if "%VERIFICATION_PASSED%"=="true" (
    echo 🎉 开发环境验证通过！
    echo.
    echo 环境状态摘要:
    echo ✅ Java开发环境正常
    echo ✅ Maven构建工具正常
    echo ✅ MySQL数据库连接正常
    echo ✅ 公益项目数据库表完整
    echo ✅ 项目编译成功
    echo ✅ 核心代码文件完整
    echo ✅ 配置文件完整
    echo.
    echo 下一步操作:
    echo 1. 启动应用程序: dev-start-safe.bat
    echo 2. 访问管理后台: http://localhost:8081/admin
    echo 3. 查看API文档: docs\charity-api-documentation.md
    echo 4. 查看开发指南: docs\charity-development-guide.md
) else (
    echo ❌ 开发环境验证失败！
    echo.
    echo 请解决以上标记为❌的问题后重新运行验证脚本
    echo.
    echo 常见解决方案:
    echo - 安装缺失的软件组件
    echo - 检查网络连接和防火墙设置
    echo - 运行 setup-dev-environment.bat 重新配置环境
    echo - 运行 init-charity-database.bat 初始化数据库
)

echo.
echo 验证完成时间: %date% %time%
echo.
pause