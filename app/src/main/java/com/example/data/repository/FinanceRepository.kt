package com.example.data.repository

import com.example.data.local.AssetDao
import com.example.data.local.GoalDao
import com.example.data.local.TimelineEventDao
import com.example.data.local.TransactionDao
import com.example.data.local.UserProfileDao
import com.example.data.local.AiInsightDao
import com.example.data.local.AppNotificationDao
import com.example.data.model.AiInsight
import com.example.data.model.AppNotification
import com.example.data.model.Asset
import com.example.data.model.Goal
import com.example.data.model.TimelineEvent
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.data.model.TransactionType
import com.example.data.model.FinancialSummary
import com.example.data.model.CategorySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val userProfileDao: UserProfileDao,
    private val assetDao: AssetDao,
    private val timelineEventDao: TimelineEventDao,
    private val aiInsightDao: AiInsightDao,
    private val appNotificationDao: AppNotificationDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val allAssets: Flow<List<Asset>> = assetDao.getAllAssets()
    val allTimelineEvents: Flow<List<TimelineEvent>> = timelineEventDao.getAllEvents()
    val allInsights: Flow<List<AiInsight>> = aiInsightDao.getAllInsights()
    val allNotifications: Flow<List<AppNotification>> = appNotificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = appNotificationDao.getUnreadCount()

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.saveUserProfile(profile)
    }

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
        val id = transactionDao.insertTransaction(transaction)
        logEvent(
            title = "Transacción Registrada",
            description = "${if (transaction.type == TransactionType.INCOME) "Ingreso" else "Egreso"} de $${transaction.amount} en ${transaction.category}",
            eventType = "TRANSACTION",
            tags = transaction.tags
        )
        return id
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    // Asset operations
    suspend fun insertAsset(asset: Asset): Long {
        val id = assetDao.insertAsset(asset)
        logEvent(
            title = "Activo Registrado: ${asset.name}",
            description = "Compra por $${asset.purchasePrice}. Categ: ${asset.category}",
            eventType = "ASSET_BUY",
            tags = asset.tags
        )
        return id
    }

    suspend fun updateAsset(asset: Asset) {
        assetDao.updateAsset(asset)
        if (asset.status == "SOLD") {
            logEvent(
                title = "Activo Vendido: ${asset.name}",
                description = "Venta por $${asset.salePrice}. Ganancia Neta: $${asset.profit} (${String.format("%.1f", asset.roiPercentage)}% ROI)",
                eventType = "ASSET_SALE",
                tags = asset.tags
            )
            addNotification(
                title = "Venta de ${asset.name}",
                message = "Generaste un margen neto de $${String.format("%,.0f", asset.profit)} (${String.format("%.1f", asset.roiPercentage)}% ROI)!",
                type = "INSIGHT"
            )
        }
    }

    suspend fun deleteAsset(id: Long) {
        assetDao.deleteAsset(id)
    }

    // Goal operations
    suspend fun insertGoal(goal: Goal): Long {
        val id = goalDao.insertGoal(goal)
        logEvent(
            title = "Nueva Meta: ${goal.title}",
            description = "Objetivo $${goal.targetAmount}",
            eventType = "GOAL_PROGRESS"
        )
        return id
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(id: Long) {
        goalDao.deleteGoalById(id)
    }

    // Timeline, Insights & Notifications
    suspend fun logEvent(title: String, description: String, eventType: String, tags: String = "") {
        timelineEventDao.insertEvent(
            TimelineEvent(
                title = title,
                description = description,
                eventType = eventType,
                tags = tags
            )
        )
    }

    suspend fun addInsight(title: String, content: String, insightType: String = "INSIGHT", metricImpact: String = "") {
        aiInsightDao.insertInsight(
            AiInsight(
                title = title,
                content = content,
                insightType = insightType,
                metricImpact = metricImpact
            )
        )
    }

    suspend fun addNotification(title: String, message: String, type: String = "INFO") {
        appNotificationDao.insertNotification(
            AppNotification(
                title = title,
                message = message,
                type = type
            )
        )
    }

    suspend fun markNotificationsRead() {
        appNotificationDao.markAllAsRead()
    }

    suspend fun seedInitialDataIfEmpty() {
        val currentTx = allTransactions.first()
        if (currentTx.isEmpty()) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L

            transactionDao.insertTransaction(
                Transaction(
                    title = "Cobro Trabajo Pendiente #1",
                    amount = 5500000.0,
                    type = TransactionType.INCOME,
                    category = "Trabajos",
                    paymentMethod = "Transferencia",
                    tags = "#consultoria #cobro",
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
                    tags = "#usados #moto",
                    dateMillis = now - (dayMillis * 5),
                    notes = "Moto usada"
                )
            )
            transactionDao.insertTransaction(
                Transaction(
                    title = "Supermercado y Provisiones",
                    amount = 280000.0,
                    type = TransactionType.EXPENSE,
                    category = "Alimentación",
                    paymentMethod = "Tarjeta",
                    tags = "#gastoshogar",
                    dateMillis = now - (dayMillis * 1),
                    notes = "Compras mensuales"
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
        }

        val currentAssets = allAssets.first()
        if (currentAssets.isEmpty()) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L
            assetDao.insertAsset(
                Asset(
                    name = "Volkswagen Gol Trend 2018",
                    category = "Vehículos",
                    purchasePrice = 12000000.0,
                    extraExpenses = 800000.0, // pintura y transferencia
                    salePrice = 15100000.0,
                    status = "SOLD",
                    tags = "#gol #concesionaria #revendedor",
                    purchaseDateMillis = now - (dayMillis * 25),
                    saleDateMillis = now - (dayMillis * 5),
                    notes = "Comprado de oportunidad, se realizó pintura y pulido."
                )
            )
            assetDao.insertAsset(
                Asset(
                    name = "Toyota Corolla 2020 XEI",
                    category = "Vehículos",
                    purchasePrice = 18500000.0,
                    extraExpenses = 300000.0,
                    salePrice = 0.0,
                    status = "ACTIVE",
                    tags = "#toyota #enventa",
                    purchaseDateMillis = now - (dayMillis * 8),
                    notes = "En preparación para venta. Precio estimado publicación: $21.500.000"
                )
            )

            // Seed insights and timeline events
            aiInsightDao.insertInsight(
                AiInsight(
                    title = "Margen de Compra-Venta Vehículos",
                    content = "La venta del Gol Trend 2018 te dejó una rentabilidad neta del 18.0% sobre el capital invertido en 20 días. Se recomienda reinvertir el retorno en la Toyota Corolla 2020.",
                    insightType = "INSIGHT",
                    metricImpact = "+$2.300.000 (18.0% ROI)"
                )
            )
            aiInsightDao.insertInsight(
                AiInsight(
                    title = "Diagnóstico Diario de Liquidez",
                    content = "Cuentas con $4.200.000 de liquidez libre. Estás al 42% del objetivo para la Hilux.",
                    insightType = "DIGEST",
                    metricImpact = "Liquidez Optimizada"
                )
            )

            appNotificationDao.insertNotification(
                AppNotification(
                    title = "¡Gran Venta Registrada!",
                    message = "El Gol Trend te generó un margen neto de $2.300.000.",
                    type = "INSIGHT"
                )
            )
            appNotificationDao.insertNotification(
                AppNotification(
                    title = "Objetivo Hilux al 42%",
                    message = "Llegaste a $10.500.000 acumulados.",
                    type = "GOAL"
                )
            )

            timelineEventDao.insertEvent(
                TimelineEvent(
                    title = "Venta Cerrada: Gol Trend 2018",
                    description = "Precio Venta: $15.100.000 | Costo Total: $12.800.000 | Ganancia Neta: $2.300.000 (18% ROI)",
                    eventType = "ASSET_SALE",
                    tags = "#gol #ganancia"
                )
            )
            timelineEventDao.insertEvent(
                TimelineEvent(
                    title = "Compra Activo: Toyota Corolla 2020",
                    description = "Inversión inicial: $18.500.000",
                    eventType = "ASSET_BUY",
                    tags = "#toyota"
                )
            )
        }

        val profile = userProfileDao.getUserProfileOnce()
        if (profile == null) {
            userProfileDao.saveUserProfile(
                UserProfile(
                    name = "Martin",
                    primaryCurrency = "$",
                    mainFinancialGoal = "Comprar Vehículo / Hilux",
                    monthlyTargetIncome = 5000000.0,
                    riskProfile = "Crecimiento",
                    initialAiDiagnosisSummary = "Tu perfil estratégico está enfocado en crecimiento acelerado y acumulación de capital para la compra de tu vehículo sin comprometer tu liquidez mensual.",
                    isOnboardingCompleted = true
                )
            )
        }
    }
}
