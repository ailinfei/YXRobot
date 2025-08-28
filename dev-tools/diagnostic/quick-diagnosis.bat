@echo off
chcp 65001 > nul

REM 导入工具函数
call "%~dp0diagnostic-utils.bat"

echo ========================================
echo    YXRobot API错误快速诊断工具
echo ========================================
echo.

call :log_info "开始API错误诊断..."
call :get_timestamp
echo 诊断时间: %timestamp%
echo.

REM 1. 检查基础环境
call :log_info "1. 检查基础开发环境..."
call :check_command java
if %errorlevel% neq 0 goto :environment_error

call :check_command mvn
if %errorlevel% neq 0 goto :environment_error

call :check_command node
if %errorlevel% neq 0 (
    call :log_warning "Node.js not found, but Maven plugin can handle frontend build"
)

REM 2. 检查端口占用情况
call :log_info "2. 检查端口占用情况..."
call :check_port 8081
if %errorlevel% neq 0 (
    call :log_warning "端口8081被占用，获取占用进程..."
    call :get_port_pid 8081
    set "pid=%errorlevel%"
    if not "%pid%"=="0" (
        call :log_info "占用进程PID: %pid%"
        tasklist /FI "PID eq %pid%" /FO TABLE
        echo.
        set /p "kill_choice=是否终止占用进程? (y/n): "
        if /i "%kill_choice%"=="y" (
            call :kill_process %pid%
        )
    )
)

REM 3. 检查Spring Boot应用状态
call :log_info "3. 检查Spring Boot应用状态..."
call :test_url "http://localhost:8081/actuator/health"
if %errorlevel% neq 0 (
    call :log_error "Spring Boot应用未运行或健康检查失败"
    call :log_info "尝试测试基础端点..."
    call :test_url "http://localhost:8081"
    if %errorlevel% neq 0 (
        call :log_error "应用完全无法访问，需要启动应用"
        goto :application_not_running
    )
) else (
    call :log_success "Spring Boot应用运行正常"
)

REM 4. 检查API端点
call :log_info "4. 检查API端点可访问性..."
call :test_url "http://localhost:8081/api/v1/products"
if %errorlevel% neq 0 (
    call :log_error "API端点无法访问"
    goto :api_error
) else (
    call :log_success "API端点可访问"
)

REM 5. 检查数据库连接
call :log_info "5. 检查数据库连接..."
call :test_url "http://localhost:8081/actuator/health"
REM 这里可以添加更详细的数据库连接检查

REM 6. 检查配置文件
call :log_info "6. 检查关键配置文件..."
call :check_file "src\main\resources\application.yml"
call :check_file "pom.xml"
call :check_file "src\frontend\vite.config.ts"

echo.
call :log_success "诊断完成！"
echo.
echo 如果仍有问题，请运行详细诊断: dev-tools\diagnostic\full-diagnosis.bat
pause
exit /b 0

:environment_error
call :log_error "开发环境配置有问题，请检查Java和Maven安装"
echo.
echo 解决方案:
echo 1. 确保Java 11或更高版本已安装
echo 2. 确保Maven已安装并添加到PATH
echo 3. 运行 java -version 和 mvn -version 验证安装
pause
exit /b 1

:application_not_running
call :log_error "Spring Boot应用未运行"
echo.
echo 解决方案:
echo 1. 运行 dev-full.bat 启动完整开发环境
echo 2. 或运行 mvn spring-boot:run 启动后端
echo 3. 检查应用日志: logs\yxrobot-backend.log
echo.
set /p "start_choice=是否现在启动应用? (y/n): "
if /i "%start_choice%"=="y" (
    call :log_info "启动Spring Boot应用..."
    start "YXRobot-Backend" cmd /c "mvn spring-boot:run -Dspring.profiles.active=dev"
    call :log_info "应用启动中，请等待30秒后重新测试..."
    timeout /t 30 > nul
    call :test_url "http://localhost:8081"
    if %errorlevel% == 0 (
        call :log_success "应用启动成功！"
    ) else (
        call :log_error "应用启动可能失败，请检查日志"
    )
)
pause
exit /b 1

:api_error
call :log_error "API端点访问失败"
echo.
echo 可能的原因:
echo 1. 应用启动了但API路由配置有问题
echo 2. 数据库连接失败导致API无法正常工作
echo 3. 应用内部错误
echo.
echo 解决方案:
echo 1. 检查应用日志: logs\yxrobot-backend.log
echo 2. 检查数据库连接配置
echo 3. 运行 dev-tools\diagnostic\full-diagnosis.bat 进行详细诊断
pause
exit /b 1