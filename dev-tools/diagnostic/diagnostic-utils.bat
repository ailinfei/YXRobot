@echo off
chcp 65001 > nul

REM ========================================
REM YXRobot 诊断工具 - 通用工具函数
REM ========================================

REM 设置颜色代码
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "RESET=[0m"

REM 日志函数
:log_info
echo %BLUE%[INFO]%RESET% %~1
goto :eof

:log_success
echo %GREEN%[SUCCESS]%RESET% %~1
goto :eof

:log_warning
echo %YELLOW%[WARNING]%RESET% %~1
goto :eof

:log_error
echo %RED%[ERROR]%RESET% %~1
goto :eof

REM 检查命令是否存在
:check_command
where %~1 > nul 2>&1
if %errorlevel% neq 0 (
    call :log_error "%~1 not found in PATH"
    exit /b 1
)
call :log_success "%~1 found"
exit /b 0

REM 检查端口是否被占用
:check_port
netstat -ano | findstr :%~1 > nul
if %errorlevel% == 0 (
    call :log_warning "Port %~1 is in use"
    exit /b 1
) else (
    call :log_success "Port %~1 is available"
    exit /b 0
)

REM 获取占用端口的进程ID
:get_port_pid
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%~1') do (
    echo %%a
    goto :eof
)
echo 0
goto :eof

REM 终止进程
:kill_process
taskkill /PID %~1 /F > nul 2>&1
if %errorlevel% == 0 (
    call :log_success "Process %~1 terminated"
    exit /b 0
) else (
    call :log_error "Failed to terminate process %~1"
    exit /b 1
)

REM 检查文件是否存在
:check_file
if exist "%~1" (
    call :log_success "File found: %~1"
    exit /b 0
) else (
    call :log_error "File not found: %~1"
    exit /b 1
)

REM 检查目录是否存在
:check_directory
if exist "%~1" (
    call :log_success "Directory found: %~1"
    exit /b 0
) else (
    call :log_error "Directory not found: %~1"
    exit /b 1
)

REM 创建备份
:create_backup
if exist "%~1" (
    set "backup_name=%~1.backup.%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
    set "backup_name=%backup_name: =0%"
    copy "%~1" "%backup_name%" > nul 2>&1
    if %errorlevel% == 0 (
        call :log_success "Backup created: %backup_name%"
        exit /b 0
    ) else (
        call :log_error "Failed to create backup for: %~1"
        exit /b 1
    )
) else (
    call :log_error "Source file not found: %~1"
    exit /b 1
)

REM 生成时间戳
:get_timestamp
echo %date:~0,4%-%date:~5,2%-%date:~8,2% %time:~0,2%:%time:~3,2%:%time:~6,2%
goto :eof

REM 测试URL连通性
:test_url
curl -s -o nul -w "%%{http_code}" --connect-timeout 10 "%~1" > temp_response.txt 2>&1
set /p response_code=<temp_response.txt
del temp_response.txt > nul 2>&1

if "%response_code%"=="200" (
    call :log_success "URL accessible: %~1 (HTTP %response_code%)"
    exit /b 0
) else if "%response_code%"=="000" (
    call :log_error "URL not accessible: %~1 (Connection failed)"
    exit /b 1
) else (
    call :log_warning "URL responded with HTTP %response_code%: %~1"
    exit /b 1
)