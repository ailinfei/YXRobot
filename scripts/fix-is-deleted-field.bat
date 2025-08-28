@echo off
echo =====================================================
echo 修复products表中is_deleted字段为NULL的记录
echo =====================================================

cd /d "%~dp0"

echo 🔧 正在修复is_deleted字段...
echo.

REM 执行SQL修复脚本
mysql -h yun.finiot.cn -P 3306 -u YXRobot -pYXRobot123 YXRobot < fix-is-deleted-field.sql

if %errorlevel% equ 0 (
    echo ✅ is_deleted字段修复成功！
    echo.
    echo 📋 修复内容：
    echo   - 将所有is_deleted为NULL的记录设置为0
    echo   - 确保产品能正常显示在列表中
    echo.
) else (
    echo ❌ is_deleted字段修复失败！
    echo 请检查数据库连接和权限设置
    echo.
)

pause