@echo off
setlocal

echo.
echo ============================================
echo   Stock Management App - Backend Startup
echo ============================================
echo.

:: ── Check Java ────────────────────────────────
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH.
    echo         Install Java 17+ from https://adoptium.net
    pause
    exit /b 1
)
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo [OK]    Java found: %%~v
    goto :java_done
)
:java_done

:: ── Check Maven ───────────────────────────────
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed or not in PATH.
    echo         Install Maven 3.8+ from https://maven.apache.org
    pause
    exit /b 1
)
echo [OK]    Maven found

:: ── Detect MySQL ──────────────────────────────
set "USE_H2=false"
set "MYSQL_CMD="

where mysql >nul 2>&1
if %errorlevel% equ 0 (
    set "MYSQL_CMD=mysql"
    echo [OK]    MySQL client found in PATH
    goto :mysql_found
)

:: Search common Windows install locations
for %%d in (
    "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe"
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
    "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe"
    "C:\xampp\mysql\bin\mysql.exe"
    "C:\wamp64\bin\mysql\mysql8.0.31\bin\mysql.exe"
    "C:\wamp64\bin\mysql\mysql5.7.36\bin\mysql.exe"
    "C:\laragon\bin\mysql\mysql-8.0.30-winx64\bin\mysql.exe"
) do (
    if exist %%d (
        set "MYSQL_CMD=%%~d"
        echo [OK]    MySQL client found: %%~d
        goto :mysql_found
    )
)

:: MySQL not found anywhere — fall back to H2
echo [INFO]  MySQL is not installed on this machine.
echo [INFO]  The app will use H2 embedded database instead.
echo         Data is saved in backend\data\ and persists between restarts.
set "USE_H2=true"
goto :start_backend

:mysql_found
:: ── Load .env if it exists ────────────────────
set "DB_USERNAME=root"
set "DB_PASSWORD=mysql"

if exist "%~dp0backend\.env" (
    echo [INFO]  Loading backend\.env configuration...
    for /f "usebackq tokens=1,* delims==" %%a in ("%~dp0backend\.env") do (
        if "%%a"=="DB_USERNAME" set "DB_USERNAME=%%b"
        if "%%a"=="DB_PASSWORD" set "DB_PASSWORD=%%b"
    )
)

:: ── Create database if not exists ─────────────
echo.
echo [INFO]  Creating database "stockdb" if it does not exist...
"%MYSQL_CMD%" -u %DB_USERNAME% -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS stockdb;" 2>nul
if %errorlevel% neq 0 (
    echo [WARN]  Could not create database automatically.
    echo         Possible reasons: MySQL service is not running, or wrong credentials.
    echo.
    set /p "FALLBACK=         Use H2 embedded database instead? (Y/N): "
    if /i "%FALLBACK%"=="N" (
        echo.
        echo         Start MySQL service and try again, or edit backend\.env credentials.
        pause
        exit /b 1
    )
    set "USE_H2=true"
) else (
    echo [OK]    Database "stockdb" is ready
)

:: ── Start Spring Boot ─────────────────────────
:start_backend
echo.

if "%USE_H2%"=="true" (
    echo ============================================
    echo   Starting backend with H2 database
    echo   API:        http://localhost:8080/api
    echo   Health:     http://localhost:8080/api/health
    echo   Swagger:    http://localhost:8080/swagger-ui.html
    echo   H2 Console: http://localhost:8080/h2-console
    echo   ----------------------------------------
    echo   H2 Console settings:
    echo     JDBC URL:  jdbc:h2:file:./data/stockdb
    echo     Username:  sa
    echo     Password:  (leave empty)
    echo ============================================
    echo.
    echo   Press Ctrl+C to stop the server.
    echo.
    cd /d "%~dp0backend"
    mvn spring-boot:run -Dspring-boot.run.profiles=h2
) else (
    echo ============================================
    echo   Starting backend with MySQL database
    echo   API:     http://localhost:8080/api
    echo   Health:  http://localhost:8080/api/health
    echo   Swagger: http://localhost:8080/swagger-ui.html
    echo ============================================
    echo.
    echo   Press Ctrl+C to stop the server.
    echo.
    cd /d "%~dp0backend"
    mvn spring-boot:run
)

endlocal
