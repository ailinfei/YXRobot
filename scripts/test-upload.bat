@echo off
echo 测试文件上传功能
echo.

echo 1. 启动后端服务器...
echo 请确保后端服务器已启动在 http://localhost:8080

echo.
echo 2. 启动前端开发服务器...
echo 请确保前端服务器已启动在 http://localhost:5173

echo.
echo 3. 测试步骤:
echo    - 打开浏览器访问: http://localhost:5173/admin/content/products
echo    - 点击"新增产品"按钮
echo    - 在产品封面部分点击上传按钮
echo    - 选择一个图片文件进行上传
echo    - 检查是否上传成功

echo.
echo 4. 检查上传文件:
echo    - 查看 uploads/products/covers/ 目录
echo    - 确认文件已保存到对应的日期目录中

echo.
echo 5. 测试文件访问:
echo    - 复制上传成功后返回的文件URL
echo    - 在浏览器中直接访问该URL
echo    - 确认能正常显示图片

echo.
pause