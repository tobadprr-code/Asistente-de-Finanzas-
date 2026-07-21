package com.example.data.model

enum class MessageSender {
    USER, AI
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAutoRegisteredTx: Boolean = false,
    val registeredTitle: String? = null,
    val registeredAmount: Double? = null,
    val registeredType: TransactionType? = null
)
