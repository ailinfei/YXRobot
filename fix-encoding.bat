@echo off
chcp 65001 >nul
echo ========================================
echo    XML编码自动修复工具 v2.0
echo ========================================
echo.

REM 设置项目路径
set PROJECT_PATH=%~dp0
set MAPPER_PATH=%PROJECT_PATH%src\main\resources\mapper
set BACKUP_PATH=%PROJECT_PATH%xml-backup

echo 🔍 步骤1: 检测XML编码问题...
echo 映射文件路径: %MAPPER_PATH%

REM 运行编码扫描器
echo 正在扫描XML文件...
call mvn exec:java -Dexec.mainClass="com.yxrobot.util.XmlEncodingScanner" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 扫描失败，请检查项目配置
    pause
    exit /b 1
)

echo.
echo 📦 步骤2: 备份XML文件...
if not exist "%BACKUP_PATH%" (
    mkdir "%BACKUP_PATH%"
    echo 创建备份目录: %BACKUP_PATH%
)

REM 运行备份服务
echo 正在备份XML文件...
call mvn exec:java -Dexec.mainClass="com.yxrobot.util.XmlBackupRunner" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 备份失败
    pause
    exit /b 1
)

echo.
echo 🔧 步骤3: 修复编码问题...
echo 正在修复XML文件编码...
call mvn exec:java -Dexec.mainClass="com.yxrobot.util.XmlFinalFixRunner" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 修复失败
    pause
    exit /b 1
)

echo.
echo 🧪 步骤4: 验证修复结果...
echo 正在验证XML文件...
call mvn exec:java -Dexec.mainClass="com.yxrobot.util.ComprehensiveXmlChecker" -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 验证失败
    pause
    exit /b 1
)

echo.
echo 🏗️ 步骤5: 测试编译...
echo 清理项目...
call mvn clean -q

echo 重新编译...
call mvn compile -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 编译失败，可能需要手动检查
    pause
    exit /b 1
)

echo.
echo ✅ 编码修复完成！
echo ========================================
echo 修复摘要:
echo - 备份位置: %BACKUP_PATH%
echo - 修复工具: XmlFinalFixRunner
echo - 验证工具: ComprehensiveXmlChecker
echo - 编译状态: 成功
echo ========================================
echo.
echo 💡 提示: 可以运行 'mvn spring-boot:run' 测试应用启动
echo.
pause