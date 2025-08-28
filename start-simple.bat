@echo off
echo 启动YXRobot应用...

set JAVA_HOME=E:\YXRobot\jdk21
set PATH=%JAVA_HOME%\bin;%PATH%

cd /d E:\YXRobot\workspace\projects\YXRobot

echo 正在编译项目...
java -version

echo 设置classpath...
set CLASSPATH=target\classes

echo 添加Maven依赖到classpath...
for %%f in (target\dependency\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

echo 启动Spring Boot应用...
java -Dspring.profiles.active=dev -Dserver.port=8081 -cp "%CLASSPATH%" com.yxrobot.YXRobotApplication

pause
