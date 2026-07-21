package com.example

import com.example.data.model.TransactionType
import com.example.data.remote.AiResponseResult
import com.example.data.remote.GeminiFinanceService
import com.example.data.remote.PendingTransactionSession
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationalRegistrationTest {

    private val service = GeminiFinanceService()

    @Test
    fun `test RFC-007 multi-turn slot filling for car sale`() = runBlocking {
        // Turn 1: User says "Vendí un Gol."
        val turn1 = service.processConversationalMessage(
            userPrompt = "Vendí un Gol.",
            pendingSession = null,
            userName = "Diego",
            userAccounts = "Efectivo, Mercado Pago, Banco",
            totalBalance = 5000000.0,
            recentTx = emptyList()
        )

        assertTrue("Turn 1 should yield IncompleteTransaction", turn1 is AiResponseResult.IncompleteTransaction)
        val session1 = (turn1 as AiResponseResult.IncompleteTransaction).pendingSession
        assertEquals("Venta Gol", session1.title)
        assertEquals(TransactionType.INCOME, session1.type)
        assertEquals(null, session1.amount)

        // Turn 2: User says "8 millones."
        val turn2 = service.processConversationalMessage(
            userPrompt = "8 millones.",
            pendingSession = session1,
            userName = "Diego",
            userAccounts = "Efectivo, Mercado Pago, Banco",
            totalBalance = 5000000.0,
            recentTx = emptyList()
        )

        assertTrue("Turn 2 should yield IncompleteTransaction", turn2 is AiResponseResult.IncompleteTransaction)
        val session2 = (turn2 as AiResponseResult.IncompleteTransaction).pendingSession
        assertEquals(8000000.0, session2.amount!!, 0.1)
        assertEquals(null, session2.paymentMethod)

        // Turn 3: User says "Transferencia."
        val turn3 = service.processConversationalMessage(
            userPrompt = "Transferencia.",
            pendingSession = session2,
            userName = "Diego",
            userAccounts = "Efectivo, Mercado Pago, Banco",
            totalBalance = 5000000.0,
            recentTx = emptyList()
        )

        assertTrue("Turn 3 should yield ParsedTransaction", turn3 is AiResponseResult.ParsedTransaction)
        val completedTx = turn3 as AiResponseResult.ParsedTransaction
        assertEquals("Venta Gol", completedTx.title)
        assertEquals(8000000.0, completedTx.amount, 0.1)
        assertEquals(TransactionType.INCOME, completedTx.type)
        assertEquals("Transferencia", completedTx.paymentMethod)
    }

    @Test
    fun `test single turn complete registration`() = runBlocking {
        val result = service.processConversationalMessage(
            userPrompt = "Gasté 45 mil en el supermercado con tarjeta",
            pendingSession = null,
            userName = "Diego",
            userAccounts = "Efectivo, Banco",
            totalBalance = 1000000.0,
            recentTx = emptyList()
        )

        assertTrue("Should parse directly into transaction", result is AiResponseResult.ParsedTransaction)
        val tx = result as AiResponseResult.ParsedTransaction
        assertEquals(45000.0, tx.amount, 0.1)
        assertEquals(TransactionType.EXPENSE, tx.type)
    }
}
