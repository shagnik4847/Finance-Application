@echo off
cd /d "%~dp0"

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin src\*.java
if errorlevel 1 (
    echo.
    echo Failed to compile the project.
    pause
    exit /b 1
)

java -cp "bin;lib\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar" FinanceManagerGUI
if errorlevel 1 (
    echo.
    echo Failed to start Finance Manager GUI.
    echo Make sure MySQL is running and the finance_app database exists.
    pause
)
