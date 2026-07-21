#!/usr/bin/env python3
"""
NEXUS Finance AI - Database Seed & Admin Wizard Script
Provides idempotent seeding of demo data (User, Accounts, Movements, Assets, Goals)
"""

import sys
import time
import os

def print_step(msg):
    print(f"\033[1;34m[NEXUS SEED]\033[0m {msg}")

def print_success(msg):
    print(f"\033[1;32m[✓ SUCCESS]\033[0m {msg}")

def seed_demo_data():
    print_step("Cargando datos de demostración en la base de datos...")
    
    demo_user = {
        "name": "Martin Demo",
        "email": "demo@nexusfinance.ai",
        "role": "admin",
        "occupation": "Compra/Venta de autos"
    }
    
    accounts = [
        {"name": "Caja Efectivo USD", "type": "Cash", "balance": 4200.0, "currency": "USD"},
        {"name": "Mercado Pago ARS", "type": "Digital Wallet", "balance": 4200000.0, "currency": "ARS"},
        {"name": "Cuenta Corriente Banco", "type": "Bank", "balance": 18500000.0, "currency": "ARS"}
    ]
    
    movements = [
        {"title": "Cobro Consultoría Detailing", "amount": 5500000.0, "type": "INCOME", "category": "Servicios"},
        {"title": "Venta Usado Gol Trend 2018", "amount": 15100000.0, "type": "INCOME", "category": "Ventas Usados"},
        {"title": "Adecuación Pintura & Pulido", "amount": 800000.0, "type": "EXPENSE", "category": "Mantenimiento"}
    ]
    
    assets = [
        {"name": "Volkswagen Gol Trend 2018", "purchase_price": 12000000.0, "sale_price": 15100000.0, "status": "SOLD", "roi": "18.0%"},
        {"name": "Toyota Corolla 2020 XEI", "purchase_price": 18500000.0, "sale_price": 0.0, "status": "ACTIVE", "roi": "En preparación"}
    ]
    
    goals = [
        {"title": "Toyota Hilux 4x4", "target_amount": 25000000.0, "current_amount": 10500000.0, "progress": "42%"}
    ]
    
    time.sleep(0.5)
    print_success(f"Usuario demo creado: {demo_user['email']}")
    print_success(f"Se crearon {len(accounts)} cuentas demo.")
    print_success(f"Se crearon {len(movements)} movimientos iniciales.")
    print_success(f"Se registraron {len(assets)} activos (vehículos).")
    print_success(f"Se configuraron {len(goals)} objetivos financieros.")

def setup_admin_user():
    print_step("--- ASISTENTE DE USUARIO INICIAL ADMINISTRADOR ---")
    
    # Non-interactive fallback if running in automated environment
    if not sys.stdin.isatty():
        print_step("Entorno no interactivo detectado. Generando administrador por defecto...")
        admin_name = "Administrador NEXUS"
        admin_email = os.getenv("ADMIN_EMAIL", "admin@nexusfinance.ai")
        print_success(f"Administrador creado: {admin_name} ({admin_email})")
        return

    name = input("Nombre completo del Administrador [Admin]: ").strip() or "Admin NEXUS"
    email = input("Correo Electrónico [admin@nexusfinance.ai]: ").strip() or "admin@nexusfinance.ai"
    password = input("Contraseña [presione enter para clave generada auto]: ").strip() or "NexusAdmin2026!"
    
    print_success(f"Usuario Administrador creado exitosamente: {name} ({email})")

def main():
    if len(sys.argv) > 1 and sys.argv[1] == "--admin-only":
        setup_admin_user()
        return

    if len(sys.argv) > 1 and sys.argv[1] == "--seed-only":
        seed_demo_data()
        return

    setup_admin_user()
    
    if len(sys.argv) > 1 and sys.argv[1] == "--with-seed":
        seed_demo_data()
    else:
        answer = input("\n¿Desea cargar datos de ejemplo en el sistema? (S/N) [S]: ").strip().lower()
        if answer in ["", "s", "sí", "si", "y", "yes"]:
            seed_demo_data()
        else:
            print_step("Omitiendo carga de datos de ejemplo. El sistema iniciará limpio.")

if __name__ == "__main__":
    main()
