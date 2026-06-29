@echo off
echo ============================================
echo  SmartLogix EP3 - Pruebas Backend
echo  Autor: Benjamin - DSY1106
echo ============================================

set ROOT=%~dp0..

echo.
echo [1/4] Ejecutando pruebas BFF...
cd /d "%ROOT%\bff"
call mvn clean test jacoco:report -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falló BFF
) else (
    echo [OK] BFF - Reporte: bff\target\site\jacoco\index.html
)

echo.
echo [2/4] Ejecutando pruebas ms-inventario...
cd /d "%ROOT%\ms-inventario"
call mvn clean test jacoco:report -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falló ms-inventario
) else (
    echo [OK] ms-inventario - Reporte: ms-inventario\target\site\jacoco\index.html
)

echo.
echo [3/4] Ejecutando pruebas ms-pedidos...
cd /d "%ROOT%\ms-pedidos"
call mvn clean test jacoco:report -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falló ms-pedidos
) else (
    echo [OK] ms-pedidos - Reporte: ms-pedidos\target\site\jacoco\index.html
)

echo.
echo [4/4] Ejecutando pruebas ms-envios...
cd /d "%ROOT%\ms-envios"
call mvn clean test jacoco:report -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falló ms-envios
) else (
    echo [OK] ms-envios - Reporte: ms-envios\target\site\jacoco\index.html
)

echo.
echo ============================================
echo  Pruebas backend completadas.
echo  Abrir los index.html en el navegador para
echo  ver los reportes de cobertura JaCoCo.
echo ============================================
pause
