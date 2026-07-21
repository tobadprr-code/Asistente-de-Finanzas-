# 🚀 NEXUS Finance AI — Quickstart Guide

> **Sistema Inteligente de Gestión Financiera, Activos y Proyecciones con IA**

---

## ⚡ Instalación Rápida en 1 Comando (< 2 Minutos)

Para clonar e iniciar el proyecto completo en cualquier sistema operativo:

```bash
git clone https://github.com/nexus-finance/nexus-app.git
cd nexus-app
make setup
```

*En Windows (CMD / PowerShell) también podés ejecutar:*
```cmd
setup.bat
```

---

## 🛠️ ¿Qué hace el Bootstrap automático?

El script de primera ejecución realiza automáticamente:
1. **Verificación de Prerequisitos:** Comprueba Docker, Docker Compose, Node.js, Python 3 y Git.
2. **Variables de Entorno:** Crea el archivo `.env` automáticamente desde `.env.example`.
3. **Base de Datos & Healthcheck:** Levanta PostgreSQL en Docker y espera la confirmación de salud.
4. **Migraciones:** Ejecuta las migraciones de esquema (`Alembic upgrade head`).
5. **Usuario e Información Demo:** Configura el Administrador inicial y te ofrece cargar datos de prueba (vehículos, cuentas y movimientos).

---

## 💻 Comandos Principales (`Makefile`)

| Comando | Descripción |
| :--- | :--- |
| `make setup` | Bootstrap completo idempotente de primera ejecución. |
| `make dev` | Inicia el entorno de desarrollo y compila la app. |
| `make stop` | Detiene los servicios Docker. |
| `make reset` | Reinicia la base de datos y vuelve a ejecutar setup. |
| `make seed` | Carga o recarga los datos de demostración en la base de datos. |
| `make logs` | Muestra los registros en tiempo real de los contenedores. |
| `make test` | Ejecuta las pruebas unitarias. |
| `make lint` | Analiza el código con reglas de linter. |
| `make format` | Formatea el código automáticamente. |

---

## 📱 Onboarding Interactivo en la App

Al abrir la aplicación por primera vez, verás el **Asistente de Bienvenida de 2 Minutos**:
- **Paso 1:** Tu Nombre.
- **Paso 2:** Rubro (Detailing, Compra/Venta de autos, Freelancer, Comercio, Otro).
- **Paso 3:** Cuentas Activas (Efectivo, Mercado Pago, Banco, Otro).
- **Paso 4:** Cargar datos de prueba vs. Empezar de cero.
- **Paso 5:** Entrada inmediata al Dashboard con diagnóstico IA adaptado.
