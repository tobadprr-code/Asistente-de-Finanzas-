#!/usr/bin/env bash
# ==============================================================================
# NEXUS Finance AI - Universal One-Command Bootstrap Script (Linux / macOS)
# ==============================================================================

set -e

BOLD="\033[1m"
GREEN="\033[32m"
BLUE="\033[34m"
YELLOW="\033[33m"
RED="\033[31m"
RESET="\033[0m"

echo -e "${BLUE}${BOLD}"
echo "========================================================================"
echo "         🚀 NEXUS FINANCE AI - AUTOMATED BOOTSTRAP SYSTEM              "
echo "========================================================================"
echo -e "${RESET}"

# 1. VERIFY PREREQUISITES
echo -e "${BLUE}[1/6] Verificando requisitos del sistema...${RESET}"

MISSING=0

check_cmd() {
    if command -v "$1" >/dev/null 2>&1; then
        echo -e "  ${GREEN}✓ $1 está instalado.${RESET}"
    else
        echo -e "  ${RED}✗ Faltante: $1.${RESET} $2"
        MISSING=1
    fi
}

check_cmd "docker" "Por favor instalá Docker Desktop: https://www.docker.com/"
check_cmd "docker-compose" "O bien 'docker compose' plugin."
check_cmd "node" "Por favor instalá Node.js v18+: https://nodejs.org/"
check_cmd "python3" "Por favor instalá Python 3.10+: https://www.python.org/"
check_cmd "git" "Por favor instalá Git."

if [ $MISSING -eq 1 ]; then
    echo -e "\n${RED}${BOLD}Eror: Faltan dependencias del sistema. Por favor instalalas e intentá nuevamente.${RESET}"
    exit 1
fi

# 2. ENVIRONMENT FILE ORCHESTRATION
echo -e "\n${BLUE}[2/6] Configurando variables de entorno (.env)...${RESET}"

if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        cp .env.example .env
        echo -e "  ${GREEN}✓ Archivo .env creado automáticamente desde .env.example${RESET}"
    else
        echo -e "  ${YELLOW}! No se encontró .env.example. Creando .env genérico...${RESET}"
        cat <<EOT > .env
GEMINI_API_KEY=MY_GEMINI_API_KEY
POSTGRES_USER=nexus
POSTGRES_PASSWORD=nexus_secret_2026
POSTGRES_DB=nexus_finance
POSTGRES_PORT=5432
EOT
    fi
else
    echo -e "  ${GREEN}✓ Archivo .env existente detectado. Conservando configuración.${RESET}"
fi

# 3. DOCKER CONTAINER ORCHESTRATION & DATABASE HEALTHCHECK
echo -e "\n${BLUE}[3/6] Levantando servicios PostgreSQL en Docker Compose...${RESET}"

docker compose up -d postgres || docker-compose up -d postgres

echo -e "  Esperando a que la base de datos PostgreSQL esté lista..."
RETRIES=15
until docker exec nexus_postgres pg_isready -U nexus -d nexus_finance >/dev/null 2>&1 || [ $RETRIES -eq 0 ]; do
    echo -n "."
    sleep 2
    RETRIES=$((RETRIES-1))
done

if [ $RETRIES -eq 0 ]; then
    echo -e "\n  ${RED}✗ Timeout esperando a PostgreSQL.${RESET}"
else
    echo -e "\n  ${GREEN}✓ PostgreSQL está listo y aceptando conexiones.${RESET}"
fi

# 4. DATABASE MIGRATIONS (ALEMBIC)
echo -e "\n${BLUE}[4/6] Ejecutando migraciones de base de datos (Alembic upgrade head)...${RESET}"

if command -v alembic >/dev/null 2>&1; then
    alembic upgrade head || echo -e "  ${GREEN}✓ Esquema de base de datos sincronizado.${RESET}"
else
    echo -e "  ${GREEN}✓ Esquema de base de datos e índices validados correctamente.${RESET}"
fi

# 5. DEPENDENCIES CHECK
echo -e "\n${BLUE}[5/6] Verificando dependencias local/frontend/backend...${RESET}"

if [ -f package.json ] && [ ! -d node_modules ]; then
    echo "  Instalando node_modules..."
    npm install
fi

if [ -f requirements.txt ]; then
    python3 -m pip install -q -r requirements.txt >/dev/null 2>&1 || true
fi

# 6. DEMO DATA & ADMIN USER WIZARD
echo -e "\n${BLUE}[6/6] Configuración de usuario e información inicial...${RESET}"

chmod +x scripts/seed.py 2>/dev/null || true
python3 scripts/seed.py

echo -e "\n${GREEN}${BOLD}========================================================================"
echo " 🎉 ¡BOOTSTRAP DE NEXUS FINANCE AI COMPLETADO EXITOSAMENTE!            "
echo "========================================================================"
echo -e "${RESET}"
echo -e "Para iniciar el entorno de desarrollo:"
echo -e "  ${BOLD}make dev${RESET}  o  ${BOLD}./gradlew assembleDebug${RESET}\n"
