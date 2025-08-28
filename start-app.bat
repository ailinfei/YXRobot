@echo off
echo Starting YXRobot Application...
set SPRING_PROFILES_ACTIVE=dev
set SERVER_PORT=8081
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081 -Dspring.profiles.active=dev"
pause