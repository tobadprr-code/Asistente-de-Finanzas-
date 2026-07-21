package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "Vehículos", // Vehículos, Inmuebles, Equipamiento, Mercadería
    val purchasePrice: Double,
    val extraExpenses: Double = 0.0,
    val salePrice: Double = 0.0,
    val status: String = "ACTIVE", // ACTIVE, SOLD
    val tags: String = "",
    val purchaseDateMillis: Long = System.currentTimeMillis(),
    val saleDateMillis: Long? = null,
    val notes: String = ""
) {
    val totalCost: Double
        get() = purchasePrice + extraExpenses

    val profit: Double
        get() = if (status == "SOLD") salePrice - totalCost else 0.0

    val roiPercentage: Double
        get() = if (totalCost > 0 && status == "SOLD") (profit / totalCost) * 100.0 else 0.0
}
