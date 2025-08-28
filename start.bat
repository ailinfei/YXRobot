@echo off
echo Starting YXRobot Application...
cd /d "%~dp0"
mvn spring-boot:run -Dspring.profiles.active=simple
pause