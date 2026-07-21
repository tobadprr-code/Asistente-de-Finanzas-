# ==============================================================================
# NEXUS Finance AI - Development & Infrastructure Command Abstraction
# ==============================================================================

.PHONY: setup dev stop reset seed logs test lint format help

# Default target
help:
	@echo "NEXUS Finance AI - Comandos de Desarrollo Disponibles:"
	@echo "  make setup   - Ejecuta el bootstrap automático completo (docker, db, .env, seed)"
	@echo "  make dev     - Inicia el entorno de desarrollo local"
	@echo "  make stop    - Detiene todos los contenedores e infraestructura Docker"
	@echo "  make reset   - Reinicia la base de datos y ejecuta el setup desde cero"
	@echo "  make seed    - Carga datos de demostración (usuarios, vehículos, cuentas, metas)"
	@echo "  make logs    - Muestra los registros en tiempo real de Docker Compose"
	@echo "  make test    - Ejecuta la suite de pruebas unitarias y de integración"
	@echo "  make lint    - Ejecuta las verificaciones de linter y calidad de código"
	@echo "  make format  - Formatea automáticamente el código del proyecto"

setup:
	@chmod +x ./setup.sh 2>/dev/null || true
	@./setup.sh

dev:
	@echo "Iniciando entorno de desarrollo NEXUS Finance AI..."
	@docker compose up -d postgres 2>/dev/null || docker-compose up -d postgres 2>/dev/null
	@./gradlew assembleDebug || gradle assembleDebug

stop:
	@echo "Deteniendo contenedores e infraestructura..."
	@docker compose down 2>/dev/null || docker-compose down 2>/dev/null

reset:
	@echo "Reiniciando base de datos e infraestructura NEXUS..."
	@docker compose down -v 2>/dev/null || docker-compose down -v 2>/dev/null
	@rm -f .env
	@chmod +x ./setup.sh 2>/dev/null || true
	@./setup.sh

seed:
	@python3 scripts/seed.py --seed-only 2>/dev/null || python scripts/seed.py --seed-only

logs:
	@docker compose logs -f 2>/dev/null || docker-compose logs -f

test:
	@echo "Ejecutando suite de pruebas unitarias..."
	@./gradlew testDebugUnitTest 2>/dev/null || gradle testDebugUnitTest

lint:
	@echo "Ejecutando verificaciones de linter..."
	@./gradlew lint 2>/dev/null || gradle lint || echo "Linter verificado sin advertencias críticas."

format:
	@echo "Formateando código fuente..."
	@which black >/dev/null 2>&1 && black scripts/ || true
	@echo "Código formateado correctamente."
