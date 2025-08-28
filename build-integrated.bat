@echo off
chcp 65001 > nul
echo ========================================
echo    YXRobot 集成构建脚本
echo    (Maven + 前端构建 + Spring Boot集成)
echo ========================================
echo.

REM 设置环境变量
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

echo 1. 清理之前的构建...
if exist "src\main\resources\static" rmdir /s /q "src\main\resources\static" > nul 2>&1
if exist "src\frontend\dist" rmdir /s /q "src\frontend\dist" > nul 2>&1
if exist "target" rmdir /s /q "target" > nul 2>&1
echo    清理完成

echo 2. Maven清理项目...
call mvn clean -q
if %errorlevel% neq 0 (
    echo    错误: Maven清理失败
    pause
    exit /b 1
)

echo 3. 构建前端 (通过Maven)...
echo    安装Node.js和npm...
call mvn frontend:install-node-and-npm -q
echo    安装前端依赖...
call mvn frontend:npm@"npm install" -q
echo    构建前端项目...
call mvn frontend:npm@"npm run build" -q

REM 检查前端构建结果
if not exist "src\frontend\dist\index.html" (
    echo    前端构建可能失败，尝试手动构建...
    cd src\frontend
    if exist "package.json" (
        echo    手动安装依赖...
        call npm install
        echo    手动构建...
        call npm run build
        cd ..\..
        if not exist "src\frontend\dist\index.html" (
            echo    错误: 前端构建失败
            pause
            exit /b 1
        )
    ) else (
        echo    错误: 未找到package.json
        cd ..\..
        pause
        exit /b 1
    )
) else (
    echo    前端构建成功
)

echo 4. 复制前端资源到Spring Boot...
call mvn process-resources -q
if %errorlevel% neq 0 (
    echo    Maven资源处理失败，手动复制...
    if not exist "src\main\resources\static" mkdir "src\main\resources\static"
    xcopy "src\frontend\dist\*" "src\main\resources\static\" /E /I /Y > nul
)

REM 验证最终结果
if exist "src\main\resources\static\index.html" (
    echo    ✓ 前端资源集成成功
) else (
    echo    ✗ 前端资源集成失败
    pause
    exit /b 1
)

echo 5. 编译Java代码...
call mvn compile -q
if %errorlevel% neq 0 (
    echo    错误: Java编译失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo    集成构建完成！
echo ========================================
echo    前端资源已集成到: src\main\resources\static\
echo    现在可以启动Spring Boot应用
echo    
echo    启动命令: mvn spring-boot:run -Dspring.profiles.active=dev
echo    访问地址: http://localhost:8081
echo ========================================
echo.

set /p "start_now=是否现在启动应用? (y/n): "
if /i "%start_now%"=="y" (
    echo 启动Spring Boot应用...
    mvn spring-boot:run -Dspring.profiles.active=dev
) else (
    echo 构建完成，可以手动启动应用
    pause
)