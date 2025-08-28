@echo off
echo 启动前端开发服务器...
echo.

cd src\frontend

REM 检查node_modules是否存在
if not exist node_modules (
    echo 安装前端依赖...
    npm install
)

REM 启动前端开发服务器
echo 启动Vite开发服务器...
echo 前端地址: http://localhost:5173
echo 后端代理: http://localhost:8081
echo 按 Ctrl+C 停止服务器
echo.

npm run dev

cd ..\..
pause