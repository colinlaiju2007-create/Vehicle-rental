@echo off
setlocal enabledelayedexpansion

echo ====================================================================
echo   ApexDrive - Precision Vehicle Rental Management System
echo   College Java OOP Project (Spring Boot + Java 21 + SQLite)
echo ====================================================================
echo.

:: Check for bundled portable JDK 21 or system Java
if exist "%~dp0..\tools\jdk21.0.12_12\bin\java.exe" (
    echo [INFO] Using configured portable JDK 21...
    set "JAVA_HOME=%~dp0..\tools\jdk21.0.12_12"
    set "PATH=!JAVA_HOME!\bin;!PATH!"
) else if defined JAVA_HOME (
    echo [INFO] Using system JAVA_HOME: %JAVA_HOME%
    set "PATH=%JAVA_HOME%\bin;!PATH!"
) else (
    echo [INFO] Searching system PATH for Java 21...
)

:: Check for bundled Maven or system Maven
if exist "%~dp0..\tools\apache-maven-3.9.6\bin\mvn.cmd" (
    echo [INFO] Using configured portable Apache Maven...
    set "PATH=%~dp0..\tools\apache-maven-3.9.6\bin;!PATH!"
)

:: Verify Java availability
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java 21 is not found.
    echo Please install Java 21 LTS or ensure tools\jdk21 is present.
    pause
    exit /b 1
)

echo.
echo [1/2] Verifying and Compiling ApexDrive Backend...
cd /d "%~dp0"
call mvn clean compile -DskipTests
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed. Check errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/2] Starting ApexDrive Spring Boot Server...
echo Database File : %~dp0apexdrive.db
echo Web Portal    : http://localhost:8080
echo Admin Login   : admin / ApexAdmin2026!
echo.
echo Press Ctrl+C in this window anytime to stop the server.
echo ====================================================================

call mvn spring-boot:run
pause
