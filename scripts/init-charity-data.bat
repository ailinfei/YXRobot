@echo off
chcp 65001 >nul
echo ========================================
echo 公益项目管理系统数据初始化脚本
echo ========================================
echo.

:: 设置数据库连接参数
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=yxrobot
set DB_USER=root

:: 提示用户输入数据库密码
set /p DB_PASSWORD=请输入数据库密码: 

echo.
echo 开始执行数据库初始化...
echo.

:: 检查MySQL是否可用
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到MySQL命令行工具，请确保MySQL已正确安装并添加到PATH环境变量中
    pause
    exit /b 1
)

:: 测试数据库连接
echo 测试数据库连接...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "SELECT 1;" >nul 2>&1
if errorlevel 1 (
    echo 错误: 数据库连接失败，请检查连接参数和密码
    pause
    exit /b 1
)
echo 数据库连接成功！

:: 检查数据库是否存在
echo 检查数据库 %DB_NAME% 是否存在...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "USE %DB_NAME%;" >nul 2>&1
if errorlevel 1 (
    echo 数据库 %DB_NAME% 不存在，正在创建...
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    if errorlevel 1 (
        echo 错误: 创建数据库失败
        pause
        exit /b 1
    )
    echo 数据库创建成功！
) else (
    echo 数据库 %DB_NAME% 已存在
)

:: 执行表结构创建脚本
echo.
echo 1. 创建数据库表结构...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < create-charity-tables.sql
if errorlevel 1 (
    echo 错误: 表结构创建失败
    pause
    exit /b 1
)
echo 表结构创建成功！

:: 执行数据插入脚本
echo.
echo 2. 插入测试数据...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < insert-charity-data.sql
if errorlevel 1 (
    echo 错误: 数据插入失败
    pause
    exit /b 1
)
echo 测试数据插入成功！

:: 验证数据
echo.
echo 3. 验证数据插入结果...
echo.
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% -e "
SELECT 
  '数据验证结果' as '验证项目',
  '' as '数量';
SELECT 
  '统计数据' as '验证项目',
  COUNT(*) as '数量'
FROM charity_stats
UNION ALL
SELECT 
  '合作机构' as '验证项目',
  COUNT(*) as '数量'
FROM charity_institutions
UNION ALL
SELECT 
  '公益项目' as '验证项目',
  COUNT(*) as '数量'
FROM charity_projects
UNION ALL
SELECT 
  '公益活动' as '验证项目',
  COUNT(*) as '数量'
FROM charity_activities
UNION ALL
SELECT 
  '更新日志' as '验证项目',
  COUNT(*) as '数量'
FROM charity_stats_logs;
"

echo.
echo ========================================
echo 数据初始化完成！
echo ========================================
echo.
echo 初始化内容包括:
echo - 公益统计数据: 1条记录
echo - 合作机构: 17个机构 (学校、孤儿院、社区、医院、图书馆)
echo - 公益项目: 12个项目 (教育、捐赠、志愿服务、培训类)
echo - 公益活动: 16个活动 (探访、培训、捐赠、活动类)
echo - 更新日志: 4条日志记录
echo.
echo 您现在可以启动应用程序进行测试了！
echo.

pause