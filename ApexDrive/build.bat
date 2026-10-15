@echo off
setlocal enabledelayedexpansion

echo ====================================================================
echo   ApexDrive - Standalone JAR Packaging Tool
echo ====================================================================

if exist "%~dp0..\tools\jdk21.0.12_12\bin\java.exe" (
    set "JAVA_HOME=%~dp0..\tools\jdk21.0.12_12"
    set "PATH=!JAVA_HOME!\bin;!PATH!"
)
if exist "%~dp0..\tools\apache-maven-3.9.6\bin\mvn.cmd" (
    set "PATH=%~dp0..\tools\apache-maven-3.9.6\bin;!PATH!"
)

cd /d "%~dp0"
echo Packaging application into standalone runnable JAR...
call mvn clean package -DskipTests

if %ERRORLEVEL% equ 0 (
    echo.
    echo [SUCCESS] Package created at: target\apexdrive-1.0.0.jar
    echo You can run this single file directly with:
    echo    java -jar target\apexdrive-1.0.0.jar
    echo.
) else (
    echo [ERROR] Packaging failed with exit code %ERRORLEVEL%.
)

pause
