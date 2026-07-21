package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Martin",
    val occupation: String = "Compra/Venta de autos",
    val activeAccounts: String = "Efectivo, Mercado Pago",
    val primaryCurrency: String = "$",
    val mainFinancialGoal: String = "Comprar Vehículo / Hilux",
    val monthlyTargetIncome: Double = 5000000.0,
    val riskProfile: String = "Crecimiento",
    val initialAiDiagnosisSummary: String = "",
    val isOnboardingCompleted: Boolean = false
)
