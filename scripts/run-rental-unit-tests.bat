@echo off
echo ========================================
echo 运行租赁模块单元测试
echo ========================================

cd /d "%~dp0\.."

echo.
echo 正在运行租赁统计服务测试...
call mvn test -Dtest=RentalStatsServiceTest

echo.
echo 正在运行设备利用率服务测试...
call mvn test -Dtest=DeviceUtilizationServiceTest

echo.
echo 正在运行租赁控制器测试...
call mvn test -Dtest=RentalControllerTest

echo.
echo 正在运行租赁记录Mapper测试...
call mvn test -Dtest=RentalRecordMapperTest

echo.
echo 正在运行租赁验证功能测试...
call mvn test -Dtest=RentalValidationTest

echo.
echo 正在运行所有租赁模块测试...
call mvn test -Dtest="*Rental*Test"

echo.
echo 生成测试覆盖率报告...
call mvn jacoco:report

echo.
echo ========================================
echo 租赁模块单元测试完成
echo ========================================
echo.
echo 测试报告位置：
echo - 单元测试报告: target/surefire-reports/
echo - 覆盖率报告: target/site/jacoco/
echo.

pause