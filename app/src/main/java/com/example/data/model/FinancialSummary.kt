package com.example.data.model

data class FinancialSummary(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val netGain: Double = 0.0,
    val transactionCount: Int = 0
)

data class CategorySummary(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val type: TransactionType
)
