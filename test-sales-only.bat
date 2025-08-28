@echo off
echo 正在编译和运行销售模块测试...

REM 只编译测试类，不编译主代码
mvn test-compile -Dmaven.main.skip=true -Pskip-frontend

REM 运行销售相关测试
mvn surefire:test -Dtest="*Sales*Test" -Dmaven.main.skip=true

echo 测试完成！
pause