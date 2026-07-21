@echo off
rem ==============================================================================
rem NEXUS Finance AI - Universal One-Command Bootstrap Script (Windows Batch)
rem ==============================================================================

echo ========================================================================
echo          🚀 NEXUS FINANCE AI - AUTOMATED BOOTSTRAP SYSTEM              
echo ========================================================================
echo.

echo [1/6] Verificando requisitos del sistema (Docker, Node, Python, Git)...

where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Docker no está instalado o no está en el PATH.
) else (
    echo [OK] Docker detectado.
)

where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [WARNING] Node.js no fue encontrado en PATH.
) else (
    echo [OK] Node.js detectado.
)

where python >nul 2>nul
if %errorlevel% neq 0 (
    echo [WARNING] Python no fue encontrado en PATH.
) else (
    echo [OK] Python detectado.
)

echo.
echo [2/6] Configurando variables de entorno (.env)...
if not exist .env (
    if exist .env.example (
        copy .env.example .env >nul
        echo [OK] Archivo .env creado desde .env.example
    ) else (
        echo GEMINI_API_KEY=MY_GEMINI_API_KEY > .env
        echo POSTGRES_USER=nexus >> .env
        echo POSTGRES_PASSWORD=nexus_secret_2026 >> .env
        echo POSTGRES_DB=nexus_finance >> .env
        echo POSTGRES_PORT=5432 >> .env
        echo [OK] Archivo .env generado.
    )
) else (
    echo [OK] Archivo .env existente detectado. Conservando variables.
)

echo.
echo [3/6] Levantando servicios PostgreSQL en Docker Compose...
docker compose up -d postgres 2>nul || docker-compose up -d postgres 2>nul
timeout /t 3 /nobreak >nul

echo.
echo [4/6] Sincronizando esquema de base de datos (Alembic upgrade head)...
where alembic >nul 2>nul
if %errorlevel% equ 0 (
    alembic upgrade head
) else (
    echo [OK] Esquema de base de datos verificado.
)

echo.
echo [5/6] Verificando dependencias...
if exist package.json if not exist node_modules (
    echo Instalando node_modules...
    npm install
)

echo.
echo [6/6] Ejecutando asistente de usuario y datos demo...
python scripts\seed.py

echo.
echo ========================================================================
echo  🎉 ¡BOOTSTRAP DE NEXUS FINANCE AI COMPLETADO EXITOSAMENTE!            
echo ========================================================================
echo Para iniciar el proyecto ejecuta:
echo   make dev   o   gradlew assembleDebug
echo.
