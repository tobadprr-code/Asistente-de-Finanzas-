package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.Goal
import com.example.data.model.MessageSender
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.data.remote.AiResponseResult
import com.example.data.remote.GeminiFinanceService
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FinanceRepository(
        transactionDao = database.transactionDao(),
        goalDao = database.goalDao(),
        userProfileDao = database.userProfileDao(),
        assetDao = database.assetDao(),
        timelineEventDao = database.timelineEventDao(),
        aiInsightDao = database.aiInsightDao(),
        appNotificationDao = database.appNotificationDao()
    )
    private val aiService = GeminiFinanceService()

    val userProfile: StateFlow<com.example.data.model.UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssets: StateFlow<List<com.example.data.model.Asset>> = repository.allAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTimelineEvents: StateFlow<List<com.example.data.model.TimelineEvent>> = repository.allTimelineEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInsights: StateFlow<List<com.example.data.model.AiInsight>> = repository.allInsights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<com.example.data.model.AppNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val financialSummary = repository.financialSummary
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.example.data.model.FinancialSummary()
        )

    val categorySummaries = repository.categorySummaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _morningSummaryText = MutableStateFlow<String>("")
    val morningSummaryText: StateFlow<String> = _morningSummaryText.asStateFlow()

    private val _isGeneratingSummary = MutableStateFlow<Boolean>(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary.asStateFlow()

    private val _isAiThinking = MutableStateFlow<Boolean>(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _activePendingSession = MutableStateFlow<com.example.data.remote.PendingTransactionSession?>(null)
    val activePendingSession: StateFlow<com.example.data.remote.PendingTransactionSession?> = _activePendingSession.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _userCurrency = MutableStateFlow<String>("$")
    val userCurrency: StateFlow<String> = _userCurrency.asStateFlow()

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<TransactionType?>(null)
    val selectedTypeFilter: StateFlow<TransactionType?> = _selectedTypeFilter.asStateFlow()

    val filteredTransactions = combine(
        allTransactions,
        searchQuery,
        selectedCategoryFilter,
        selectedTypeFilter
    ) { txList, query, cat, type ->
        txList.filter { tx ->
            val matchesQuery = query.isBlank() || tx.title.contains(query, ignoreCase = true) ||
                    tx.notes.contains(query, ignoreCase = true) ||
                    tx.category.contains(query, ignoreCase = true)
            val matchesCategory = cat == null || tx.category == cat
            val matchesType = type == null || tx.type == type
            matchesQuery && matchesCategory && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            refreshMorningSummary()
            initInitialChatMessage()
        }
    }

    private fun initInitialChatMessage() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Hola, soy tu asesor financiero personal. Podés decirme o escribirme cualquier movimiento o consulta, por ejemplo: \"Vendí un Gol por ocho millones\" o \"¿Cómo viene mi economía?\""
            )
        )
    }

    fun refreshMorningSummary() {
        viewModelScope.launch {
            _isGeneratingSummary.value = true
            val txs = allTransactions.value
            val summary = financialSummary.value
            val goals = allGoals.value
            val summaryText = aiService.generateMorningSummary(
                totalBalance = summary.totalBalance,
                monthlyIncome = summary.monthlyIncome,
                monthlyExpense = summary.monthlyExpense,
                recentTx = txs,
                goals = goals
            )
            _morningSummaryText.value = summaryText
            _isGeneratingSummary.value = false
        }
    }

    fun sendChatMessage(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage(sender = MessageSender.USER, text = prompt)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiThinking.value = true
            val profile = userProfile.value
            val userName = profile?.name ?: "Usuario"
            val userAccounts = profile?.activeAccounts ?: "Efectivo, Mercado Pago, Banco"
            val summary = financialSummary.value
            val txs = allTransactions.value
            val currentSession = _activePendingSession.value

            val result = aiService.processConversationalMessage(
                userPrompt = prompt,
                pendingSession = currentSession,
                userName = userName,
                userAccounts = userAccounts,
                totalBalance = summary.totalBalance,
                recentTx = txs
            )

            _isAiThinking.value = false

            when (result) {
                is AiResponseResult.SuccessText -> {
                    _activePendingSession.value = null
                    val aiMsg = ChatMessage(sender = MessageSender.AI, text = result.text)
                    _chatMessages.value = _chatMessages.value + aiMsg
                }
                is AiResponseResult.IncompleteTransaction -> {
                    _activePendingSession.value = result.pendingSession
                    val aiMsg = ChatMessage(sender = MessageSender.AI, text = result.promptText)
                    _chatMessages.value = _chatMessages.value + aiMsg
                }
                is AiResponseResult.ParsedTransaction -> {
                    _activePendingSession.value = null
                    // Tool Layer Execution: create_transaction()
                    val newTx = Transaction(
                        title = result.title,
                        amount = result.amount,
                        type = result.type,
                        category = result.category,
                        paymentMethod = result.paymentMethod,
                        notes = "Registrado vía Conversación NEXUS IA"
                    )
                    repository.insertTransaction(newTx)
                    refreshMorningSummary()

                    val aiMsg = ChatMessage(
                        sender = MessageSender.AI,
                        text = result.replyText,
                        isAutoRegisteredTx = true,
                        registeredTitle = result.title,
                        registeredAmount = result.amount,
                        registeredType = result.type
                    )
                    _chatMessages.value = _chatMessages.value + aiMsg
                }
            }
        }
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        paymentMethod: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val tx = Transaction(
                title = title.ifBlank { if (type == TransactionType.INCOME) "Ingreso" else "Gasto" },
                amount = amount,
                type = type,
                category = category.ifBlank { "Varios" },
                paymentMethod = paymentMethod.ifBlank { "Transferencia" },
                notes = notes
            )
            repository.insertTransaction(tx)
            refreshMorningSummary()
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            refreshMorningSummary()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            refreshMorningSummary()
        }
    }

    fun addGoal(title: String, targetAmount: Double, currentAmount: Double, category: String) {
        viewModelScope.launch {
            val goal = Goal(
                title = title,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                category = category
            )
            repository.insertGoal(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    // Asset CRUD
    fun addAsset(
        name: String,
        category: String,
        purchasePrice: Double,
        extraExpenses: Double,
        tags: String,
        notes: String
    ) {
        viewModelScope.launch {
            val asset = com.example.data.model.Asset(
                name = name,
                category = category,
                purchasePrice = purchasePrice,
                extraExpenses = extraExpenses,
                tags = tags,
                notes = notes
            )
            repository.insertAsset(asset)
        }
    }

    fun sellAsset(asset: com.example.data.model.Asset, salePrice: Double) {
        viewModelScope.launch {
            val updated = asset.copy(
                salePrice = salePrice,
                status = "SOLD",
                saleDateMillis = System.currentTimeMillis()
            )
            repository.updateAsset(updated)
            // Automatically log income transaction
            val saleIncomeTx = Transaction(
                title = "Venta Activo: ${asset.name}",
                amount = salePrice,
                type = TransactionType.INCOME,
                category = "Venta Activos",
                paymentMethod = "Transferencia",
                tags = asset.tags,
                assetId = asset.id,
                notes = "Cierre de venta con ganancia de $${updated.profit}"
            )
            repository.insertTransaction(saleIncomeTx)
            refreshMorningSummary()
        }
    }

    fun deleteAsset(id: Long) {
        viewModelScope.launch {
            repository.deleteAsset(id)
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setTypeFilter(type: TransactionType?) {
        _selectedTypeFilter.value = type
    }

    fun setCurrency(symbol: String) {
        _userCurrency.value = symbol
    }

    fun saveUserProfile(profile: com.example.data.model.UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
            _userCurrency.value = profile.primaryCurrency
        }
    }

    suspend fun processOnboardingDiagnosis(
        name: String,
        currency: String,
        mainGoal: String,
        monthlyIncome: Double,
        answers: String
    ): String {
        val diagnosis = aiService.generateOnboardingDiagnosis(
            userName = name,
            mainGoal = mainGoal,
            monthlyIncome = monthlyIncome,
            currency = currency,
            answers = answers
        )
        val profile = com.example.data.model.UserProfile(
            name = name,
            primaryCurrency = currency,
            mainFinancialGoal = mainGoal,
            monthlyTargetIncome = monthlyIncome,
            riskProfile = "Crecimiento",
            initialAiDiagnosisSummary = diagnosis,
            isOnboardingCompleted = true
        )
        repository.saveUserProfile(profile)
        _userCurrency.value = currency
        return diagnosis
    }

    fun completeOnboardingWithFlow(
        name: String,
        occupation: String,
        accounts: List<String>,
        importDemoData: Boolean
    ) {
        viewModelScope.launch {
            if (importDemoData) {
                repository.seedInitialDataIfEmpty()
            }
            val accountsString = accounts.joinToString(", ").ifBlank { "Efectivo" }
            val current = repository.userProfile.first()
            val profile = (current ?: com.example.data.model.UserProfile()).copy(
                name = name.ifBlank { "Martin" },
                occupation = occupation.ifBlank { "Compra/Venta de autos" },
                activeAccounts = accountsString,
                isOnboardingCompleted = true
            )
            repository.saveUserProfile(profile)
        }
    }
}
