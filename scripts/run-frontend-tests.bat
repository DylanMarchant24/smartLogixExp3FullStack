@echo off
echo ============================================
echo  SmartLogix EP3 - Pruebas Frontend
echo  Autor: Benjamin - DSY1106
echo ============================================

set ROOT=%~dp0..

echo.
echo [1/2] Instalando dependencias npm...
cd /d "%ROOT%\frontend"
call npm install --silent
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] npm install falló
    pause
    exit /b 1
)

echo.
echo [2/2] Ejecutando pruebas con cobertura...
call npm run test:coverage
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Pruebas frontend fallaron
) else (
    echo [OK] Frontend - Reporte: frontend\coverage\lcov-report\index.html
)

echo.
echo ============================================
echo  Pruebas frontend completadas.
echo ============================================
pause
