@echo off
echo 启动应用（跳过测试）...

REM 设置Maven参数跳过测试
set MAVEN_OPTS=-Dmaven.test.skip=true

REM 启动Spring Boot应用
mvn compile exec:java -Dexec.mainClass="com.yxrobot.YXRobotApplication" -Dexec.args="--spring.profiles.active=dev"

pause