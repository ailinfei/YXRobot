@echo off
REM 客户管理模块单元测试执行脚本
REM 用于运行所有客户相关的单元测试并生成覆盖率报告

echo ========================================
echo 客户管理模块单元测试执行
echo ========================================
echo.

REM 设置项目根目录
cd /d "%~dp0.."

REM 检查Maven是否可用
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo 错误: Maven未安装或不在PATH中
    echo 请安装Maven并确保mvn命令可用
    pause
    exit /b 1
)

echo 1. 清理之前的测试结果...
if exist "target\test-reports" rmdir /s /q "target\test-reports"
mkdir "target\test-reports" 2>nul

echo.
echo 2. 编译测试代码...
call mvn clean compile test-compile -q
if %ERRORLEVEL% NEQ 0 (
    echo 错误: 测试代码编译失败
    pause
    exit /b 1
)

echo.
echo 3. 运行客户服务层测试...
call mvn test -Dtest=CustomerServiceTest -Dspring.profiles.active=test
set SERVICE_TEST_RESULT=%ERRORLEVEL%

echo.
echo 4. 运行客户控制器测试...
call mvn test -Dtest=CustomerControllerTest -Dspring.profiles.active=test
set CONTROLLER_TEST_RESULT=%ERRORLEVEL%

echo.
echo 5. 运行客户Mapper测试...
call mvn test -Dtest=CustomerMapperTest -Dspring.profiles.active=test
set MAPPER_TEST_RESULT=%ERRORLEVEL%

echo.
echo 6. 运行搜索优化服务测试...
call mvn test -Dtest=CustomerSearchOptimizationServiceTest -Dspring.profiles.active=test
set SEARCH_TEST_RESULT=%ERRORLEVEL%

echo.
echo 7. 运行客户验证测试...
call mvn test -Dtest=CustomerValidationTest -Dspring.profiles.active=test
set VALIDATION_TEST_RESULT=%ERRORLEVEL%

echo.
echo 8. 运行完整测试套件...
call mvn test -Dtest=CustomerModuleTestSuite -Dspring.profiles.active=test
set SUITE_TEST_RESULT=%ERRORLEVEL%

echo.
echo 9. 生成测试覆盖率报告...
call mvn jacoco:report -q
if %ERRORLEVEL% EQU 0 (
    echo 覆盖率报告已生成: target\site\jacoco\index.html
) else (
    echo 警告: 覆盖率报告生成失败
)

echo.
echo 10. 生成Surefire测试报告...
if exist "target\surefire-reports" (
    echo Surefire测试报告: target\surefire-reports\
) else (
    echo 警告: Surefire报告未生成
)

echo.
echo ========================================
echo 测试执行结果汇总
echo ========================================
echo 服务层测试: %SERVICE_TEST_RESULT%
echo 控制器测试: %CONTROLLER_TEST_RESULT%
echo Mapper测试: %MAPPER_TEST_RESULT%
echo 搜索优化测试: %SEARCH_TEST_RESULT%
echo 验证测试: %VALIDATION_TEST_RESULT%
echo 完整套件测试: %SUITE_TEST_RESULT%
echo.

REM 计算总体结果
set /a TOTAL_ERRORS=%SERVICE_TEST_RESULT%+%CONTROLLER_TEST_RESULT%+%MAPPER_TEST_RESULT%+%SEARCH_TEST_RESULT%+%VALIDATION_TEST_RESULT%

if %TOTAL_ERRORS% EQU 0 (
    echo ✅ 所有测试通过！
    echo.
    echo 测试报告位置:
    echo - 覆盖率报告: target\site\jacoco\index.html
    echo - Surefire报告: target\surefire-reports\
    echo - 自定义报告: target\test-reports\
) else (
    echo ❌ 部分测试失败，请检查测试结果
    echo.
    echo 失败的测试模块:
    if %SERVICE_TEST_RESULT% NEQ 0 echo - 服务层测试
    if %CONTROLLER_TEST_RESULT% NEQ 0 echo - 控制器测试
    if %MAPPER_TEST_RESULT% NEQ 0 echo - Mapper测试
    if %SEARCH_TEST_RESULT% NEQ 0 echo - 搜索优化测试
    if %VALIDATION_TEST_RESULT% NEQ 0 echo - 验证测试
)

echo.
echo 测试执行完成！
pause
exit /b %TOTAL_ERRORS%