package com.example.data.repository

import com.example.data.local.GoalDao
import com.example.data.local.TransactionDao
import com.example.data.model.Goal
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.data.model.FinancialSummary
import com.example.data.model.CategorySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()

    val financialSummary: Flow<FinancialSummary> = allTransactions.map { list ->
        var income = 0.0
        var expense = 0.0
        for (tx in list) {
            if (tx.type == TransactionType.INCOME) {
                income += tx.amount
            } else {
                expense += tx.amount
            }
        }
        val balance = income - expense
        val netGain = income - expense
        FinancialSummary(
            totalBalance = balance,
            monthlyIncome = income,
            monthlyExpense = expense,
            netGain = netGain,
            transactionCount = list.size
        )
    }

    val categorySummaries: Flow<List<CategorySummary>> = allTransactions.map { list ->
        val categoryMap = mutableMapOf<Pair<String, TransactionType>, Double>()
        var totalExp = 0.0
        var totalInc = 0.0

        for (tx in list) {
            val key = Pair(tx.category, tx.type)
            categoryMap[key] = (categoryMap[key] ?: 0.0) + tx.amount
            if (tx.type == TransactionType.EXPENSE) totalExp += tx.amount else totalInc += tx.amount
        }

        categoryMap.map { (key, sum) ->
            val total = if (key.second == TransactionType.EXPENSE) totalExp else totalInc
            val pct = if (total > 0) (sum / total).toFloat() else 0f
            CategorySummary(
                category = key.first,
                amount = sum,
                percentage = pct,
                type = key.second
            )
        }.sortedByDescending { it.amount }
    }

    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun insertGoal(goal: Goal): Long {
        return goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(id: Long) {
        goalDao.deleteGoalById(id)
    }

    suspend fun seedInitialDataIfEmpty() {
        val currentTx = allTransactions.first()
        if (currentTx.isEmpty()) {
            // Seed realistic V1 Argentine/LatAm finance data as requested in prompt ($4.200.000 available balance baseline)
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L

            transactionDao.insertTransaction(
                Transaction(
                    title = "Cobro Trabajo Pendiente #1",
                    amount = 5500000.0,
                    type = TransactionType.INCOME,
                    category = "Trabajos",
                    paymentMethod = "Transferencia",
                    dateMillis = now - (dayMillis * 2),
                    notes = "Proyecto de consultoría"
                )
            )
            transactionDao.insertTransaction(
                Transaction(
                    title = "Venta Usado",
                    amount = 1800000.0,
                    type = TransactionType.INCOME,
                    category = "Ventas",
                    paymentMethod = "Transferencia",
                    dateMillis = now - (dayMillis * 5),
                    notes = "Moto usaba"
                )
            )
            transactionDao.insertTransaction(
                Transaction(
                    title = "Supermercado y Provisiones",
                    amount = 280000.0,
                    type = TransactionType.EXPENSE,
                    category = "Alimentación",
                    paymentMethod = "Tarjeta",
                    dateMillis = now - (dayMillis * 1),
                    notes = "Compras mensuales"
                )
            )
            transactionDao.insertTransaction(
                Transaction(
                    title = "Mantenimiento Vehículo",
                    amount = 120000.0,
                    type = TransactionType.EXPENSE,
                    category = "Vehículos",
                    paymentMethod = "Efectivo",
                    dateMillis = now - (dayMillis * 3),
                    notes = "Cambio de aceite"
                )
            )
            transactionDao.insertTransaction(
                Transaction(
                    title = "Servicios y Utilidades",
                    amount = 270000.0,
                    type = TransactionType.EXPENSE,
                    category = "Servicios",
                    paymentMethod = "Transferencia",
                    dateMillis = now - (dayMillis * 4),
                    notes = "Luz, internet, agua"
                )
            )
            transactionDao.insertTransaction(
                Transaction(
                    title = "Cuota Seguro",
                    amount = 2430000.0,
                    type = TransactionType.EXPENSE,
                    category = "Seguros",
                    paymentMethod = "Debito Directo",
                    dateMillis = now - (dayMillis * 6),
                    notes = "Seguro integral"
                )
            )
        }

        val currentGoals = allGoals.first()
        if (currentGoals.isEmpty()) {
            goalDao.insertGoal(
                Goal(
                    title = "Comprar Hilux",
                    targetAmount = 25000000.0,
                    currentAmount = 10500000.0,
                    category = "Vehículos",
                    deadline = "2026-12"
                )
            )
            goalDao.insertGoal(
                Goal(
                    title = "Fondo de Reserva",
                    targetAmount = 10000000.0,
                    currentAmount = 4200000.0,
                    category = "Inversión",
                    deadline = "2026-09"
                )
            )
        }
    }
}
