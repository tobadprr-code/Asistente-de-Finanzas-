package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val paymentMethod: String = "Transferencia",
    val dateMillis: Long = System.currentTimeMillis(),
    val notes: String = ""
)
