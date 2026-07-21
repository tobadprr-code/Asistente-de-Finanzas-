package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.Goal
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import org.json.JSONArray
import org.json.JSONObject

data class GeminiPart(val text: String? = null)
data class GeminiContent(val parts: List<GeminiPart>)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

data class GeminiCandidate(val content: GeminiContent)
data class GeminiResponse(val candidates: List<GeminiCandidate>?)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiNetworkClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}

sealed class AiResponseResult {
    data class SuccessText(val text: String) : AiResponseResult()
    data class IncompleteTransaction(
        val pendingSession: PendingTransactionSession,
        val promptText: String
    ) : AiResponseResult()
    data class ParsedTransaction(
        val title: String,
        val amount: Double,
        val type: TransactionType,
        val category: String,
        val paymentMethod: String,
        val replyText: String
    ) : AiResponseResult()
}

data class PendingTransactionSession(
    val title: String? = null,
    val amount: Double? = null,
    val type: TransactionType? = null,
    val category: String? = null,
    val paymentMethod: String? = null
) {
    fun isComplete(): Boolean =
        !title.isNullOrBlank() &&
        amount != null && amount > 0 &&
        type != null &&
        !paymentMethod.isNullOrBlank()
}

class GeminiFinanceService {

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun generateMorningSummary(
        totalBalance: Double,
        monthlyIncome: Double,
        monthlyExpense: Double,
        recentTx: List<Transaction>,
        goals: List<Goal>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val formattedBalance = String.format("%,.0f", totalBalance).replace(',', '.')
        
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Buenos días.\n\nHoy tenés disponible $$formattedBalance.\nMi recomendación es mantener el control de gastos fijos y priorizar el fondo para tus objetivos activos."
        }

        val txSummary = recentTx.take(5).joinToString("; ") { "${it.title}: $${it.amount} (${it.type})" }
        val goalSummary = goals.joinToString("; ") { "${it.title}: ${it.progressPercentage}%" }

        val prompt = """
            Eres un asesor financiero personal humano, profesional y muy cercano (hablas en español neutro/argentino de forma elegante y directa).
            Datos actuales del usuario:
            - Saldo disponible: $$formattedBalance
            - Ingresos del mes: $$monthlyIncome
            - Egresos del mes: $$monthlyExpense
            - Últimos movimientos: $txSummary
            - Objetivos: $goalSummary

            Genera un resumen matutino conciso de 2 o 3 oraciones cortas.
            Comienza con "Buenos días." o "Hola.", menciona el saldo disponible de forma clara y da una sola recomendación financiera inteligente y realista basada en sus datos.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
        )

        try {
            val res = GeminiNetworkClient.api.generateContent(apiKey, request)
            val text = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) text.trim()
            else "Buenos días.\n\nHoy tenés disponible $$formattedBalance.\nMi recomendación es revisar los egresos recientes antes de realizar compras mayores."
        } catch (e: Exception) {
            "Buenos días.\n\nHoy tenés disponible $$formattedBalance.\nMi recomendación es esperar antes de realizar compras grandes hasta consolidar tus cobros pendientes."
        }
    }

    suspend fun processConversationalMessage(
        userPrompt: String,
        pendingSession: PendingTransactionSession?,
        userName: String,
        userAccounts: String,
        totalBalance: Double,
        recentTx: List<Transaction>
    ): AiResponseResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val formattedBalance = String.format("%,.0f", totalBalance).replace(',', '.')

        val systemInstructionText = """
            Eres NEXUS, el Asesor Financiero Personal con IA.
            Contexto del usuario:
            - Nombre: $userName
            - Cuentas activas: $userAccounts
            - Saldo disponible: $$formattedBalance
            - Sesión de registro en curso (slots incompletos): ${pendingSession ?: "Ninguna"}

            Tu objetivo principal es asistir al usuario a registrar ingresos o egresos mediante una conversación natural por turnos.
            
            Reglas de Slots para Registros:
            Para completar un registro de movimiento financiero se requieren 4 datos obligatorios:
            1. Título (descripción corta del movimiento)
            2. Tipo (INCOME para ingreso/venta/cobro, EXPENSE para gasto/compra/pago)
            3. Monto (número mayor a cero)
            4. Forma de Pago / Cuenta (ej: Efectivo, Transferencia, Mercado Pago, Banco)

            Si el usuario proporciona todos los datos o completa los slots pendientes, responde en JSON con:
            {
              "status": "COMPLETE",
              "title": "Venta Gol",
              "amount": 8000000.0,
              "type": "INCOME",
              "category": "Vehículos",
              "paymentMethod": "Transferencia",
              "replyText": "Perfecto. Movimiento registrado."
            }

            Si faltan datos obligatorios (por ejemplo dice 'Vendí un Gol' pero falta precio o forma de pago), responde en JSON con:
            {
              "status": "INCOMPLETE",
              "title": "Venta Gol",
              "amount": 8000000.0 (o null si no lo dijo),
              "type": "INCOME",
              "category": "Vehículos",
              "paymentMethod": "Transferencia" (o null),
              "promptText": "¿En cuánto lo vendiste?"
            }

            Si el usuario hace una consulta o saludo general, responde en JSON con:
            {
              "status": "QUERY",
              "replyText": "Tu respuesta breve y útil."
            }

            Responde ÚNICAMENTE en JSON válido.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackLocalInterpreter(userPrompt, pendingSession, formattedBalance)
        }

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstructionText)))
        )

        try {
            val res = GeminiNetworkClient.api.generateContent(apiKey, request)
            val rawText = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val jsonStart = rawText.indexOf('{')
            val jsonEnd = rawText.lastIndexOf('}')
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonString = rawText.substring(jsonStart, jsonEnd + 1)
                val json = JSONObject(jsonString)
                val status = json.optString("status", "QUERY")

                when (status.uppercase()) {
                    "COMPLETE" -> {
                        val title = json.optString("title", "Movimiento")
                        val amount = json.optDouble("amount", 0.0)
                        val typeStr = json.optString("type", "EXPENSE")
                        val type = if (typeStr.equals("INCOME", ignoreCase = true)) TransactionType.INCOME else TransactionType.EXPENSE
                        val category = json.optString("category", "Varios")
                        val paymentMethod = json.optString("paymentMethod", "Transferencia")
                        val replyText = json.optString("replyText", "Perfecto. Movimiento registrado.")

                        return@withContext AiResponseResult.ParsedTransaction(
                            title = title,
                            amount = amount,
                            type = type,
                            category = category,
                            paymentMethod = paymentMethod,
                            replyText = replyText
                        )
                    }
                    "INCOMPLETE" -> {
                        val title = json.optString("title", pendingSession?.title ?: "Movimiento").ifBlank { "Movimiento" }
                        val amount = if (json.has("amount") && !json.isNull("amount")) json.optDouble("amount") else pendingSession?.amount
                        val typeStr = json.optString("type", pendingSession?.type?.name ?: "INCOME")
                        val type = if (typeStr.equals("INCOME", ignoreCase = true)) TransactionType.INCOME else TransactionType.EXPENSE
                        val category = json.optString("category", pendingSession?.category ?: "Varios").ifBlank { "Varios" }
                        val paymentMethod = json.optString("paymentMethod", pendingSession?.paymentMethod ?: "").ifBlank { null }
                        val promptText = json.optString("promptText", "¿En cuánto fue la operación?")

                        val updatedSession = PendingTransactionSession(
                            title = title,
                            amount = if (amount != null && amount > 0) amount else null,
                            type = type,
                            category = category,
                            paymentMethod = paymentMethod
                        )

                        return@withContext AiResponseResult.IncompleteTransaction(
                            pendingSession = updatedSession,
                            promptText = promptText
                        )
                    }
                    else -> {
                        val replyText = json.optString("replyText", "Entendido. ¿En qué puedo ayudarte hoy?")
                        return@withContext AiResponseResult.SuccessText(replyText)
                    }
                }
            } else {
                return@withContext fallbackLocalInterpreter(userPrompt, pendingSession, formattedBalance)
            }
        } catch (e: Exception) {
            return@withContext fallbackLocalInterpreter(userPrompt, pendingSession, formattedBalance)
        }
    }

    private fun fallbackLocalInterpreter(
        userPrompt: String,
        pendingSession: PendingTransactionSession?,
        formattedBalance: String
    ): AiResponseResult {
        val lower = userPrompt.lowercase().trim()

        // 1. If we already have a pending session in progress
        if (pendingSession != null) {
            var currentTitle = pendingSession.title ?: "Movimiento"
            var currentAmount = pendingSession.amount
            var currentType = pendingSession.type ?: TransactionType.INCOME
            var currentCategory = pendingSession.category ?: "Varios"
            var currentPaymentMethod = pendingSession.paymentMethod

            // Try parsing amount if missing
            if (currentAmount == null || currentAmount <= 0) {
                currentAmount = parseSpanishAmount(userPrompt)
                // Small heuristic for car/house sales if user says "8" or "15" in response to "in how much did you sell it"
                if (currentAmount != null && currentAmount < 100 && (currentTitle.lowercase().contains("gol") || currentTitle.lowercase().contains("auto") || currentTitle.lowercase().contains("moto"))) {
                    currentAmount *= 1_000_000.0
                }
            }

            // Try parsing payment method if missing
            if (currentPaymentMethod.isNullOrBlank()) {
                currentPaymentMethod = when {
                    lower.contains("efectivo") -> "Efectivo"
                    lower.contains("transfe") || lower.contains("cbu") || lower.contains("alias") -> "Transferencia"
                    lower.contains("mercado") || lower.contains("mp") -> "Mercado Pago"
                    lower.contains("banco") || lower.contains("tarjeta") -> "Banco"
                    else -> null
                }
            }

            val updatedSession = PendingTransactionSession(
                title = currentTitle,
                amount = currentAmount,
                type = currentType,
                category = currentCategory,
                paymentMethod = currentPaymentMethod
            )

            if (updatedSession.isComplete()) {
                val actionWord = if (currentType == TransactionType.INCOME) "ingreso" else "egreso"
                val fmtAmount = String.format("%,.0f", updatedSession.amount).replace(',', '.')
                return AiResponseResult.ParsedTransaction(
                    title = updatedSession.title!!,
                    amount = updatedSession.amount!!,
                    type = updatedSession.type!!,
                    category = updatedSession.category!!,
                    paymentMethod = updatedSession.paymentMethod!!,
                    replyText = "Perfecto. Movimiento registrado de $$fmtAmount ($actionWord por ${updatedSession.title}) en ${updatedSession.paymentMethod}."
                )
            } else if (updatedSession.amount == null || updatedSession.amount <= 0) {
                return AiResponseResult.IncompleteTransaction(
                    pendingSession = updatedSession,
                    promptText = "¿En cuánto lo vendiste/compraste?"
                )
            } else if (updatedSession.paymentMethod.isNullOrBlank()) {
                return AiResponseResult.IncompleteTransaction(
                    pendingSession = updatedSession,
                    promptText = "¿Cómo recibiste o realizaste el dinero? (Efectivo, Transferencia, Mercado Pago)"
                )
            }
        }

        // 2. Fresh intent parsing when no session is active
        val parsedAmount = parseSpanishAmount(userPrompt)
        val isIncome = lower.contains("vend") || lower.contains("cobr") || lower.contains("ingres") || lower.contains("gan")
        val isExpense = lower.contains("gast") || lower.contains("compr") || lower.contains("pagu") || lower.contains("cuota")

        val paymentMethodFound = when {
            lower.contains("efectivo") -> "Efectivo"
            lower.contains("transfe") || lower.contains("cbu") -> "Transferencia"
            lower.contains("mercado") || lower.contains("mp") -> "Mercado Pago"
            lower.contains("banco") || lower.contains("tarjeta") -> "Banco"
            else -> null
        }

        if (isIncome || isExpense) {
            val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
            val category = when {
                lower.contains("gol") || lower.contains("auto") || lower.contains("moto") || lower.contains("vehiculo") -> "Vehículos"
                lower.contains("super") || lower.contains("comida") || lower.contains("farmacia") -> "Alimentación"
                lower.contains("trabaj") || lower.contains("sueldo") || lower.contains("factur") || lower.contains("detailing") -> "Trabajos"
                else -> "Varios"
            }

            val title = when {
                lower.contains("gol") -> "Venta Gol"
                lower.contains("moto") -> "Operación Moto"
                lower.contains("auto") -> "Operación Vehículo"
                lower.contains("super") -> "Gasto Supermercado"
                else -> if (isIncome) "Ingreso" else "Gasto"
            }

            val newSession = PendingTransactionSession(
                title = title,
                amount = parsedAmount,
                type = type,
                category = category,
                paymentMethod = paymentMethodFound
            )

            if (newSession.isComplete()) {
                val actionWord = if (type == TransactionType.INCOME) "ingreso" else "egreso"
                val fmtAmount = String.format("%,.0f", newSession.amount).replace(',', '.')
                return AiResponseResult.ParsedTransaction(
                    title = newSession.title!!,
                    amount = newSession.amount!!,
                    type = newSession.type!!,
                    category = newSession.category!!,
                    paymentMethod = newSession.paymentMethod!!,
                    replyText = "Perfecto. Movimiento registrado de $$fmtAmount ($actionWord por ${newSession.title}) en ${newSession.paymentMethod}."
                )
            } else if (newSession.amount == null || newSession.amount <= 0) {
                return AiResponseResult.IncompleteTransaction(
                    pendingSession = newSession,
                    promptText = "¿En cuánto lo vendiste?"
                )
            } else if (newSession.paymentMethod.isNullOrBlank()) {
                return AiResponseResult.IncompleteTransaction(
                    pendingSession = newSession,
                    promptText = "¿Cómo recibiste el dinero?"
                )
            }
        }

        // Queries / Casual questions
        if (lower.contains("cuanto gast") || lower.contains("cuánto gast")) {
            return AiResponseResult.SuccessText("Tus egresos están bajo control este mes. Tu saldo disponible actual es de $$formattedBalance.")
        }
        if (lower.contains("como viene") || lower.contains("cómo viene") || lower.contains("resumen")) {
            return AiResponseResult.SuccessText("Tu economía viene estable. Contás con un saldo de $$formattedBalance. Recomendación: mantener tus gastos fijos por debajo del 40% de tus ingresos.")
        }

        return AiResponseResult.SuccessText("Entendido. Podés decirme por ejemplo: \"Vendí un Gol\" o \"Gasté 45 mil en el supermercado\".")
    }

    private fun parseSpanishAmount(text: String): Double? {
        val lower = text.lowercase().trim()

        val millionRegex = Regex("""(\d+([.,]\d+)?)\s*(millones|millón|millon|mill|m)""")
        millionRegex.find(lower)?.let { match ->
            val numStr = match.groupValues[1].replace(",", ".")
            val num = numStr.toDoubleOrNull()
            if (num != null) return num * 1_000_000.0
        }

        val thousandRegex = Regex("""(\d+([.,]\d+)?)\s*(mil|k)""")
        thousandRegex.find(lower)?.let { match ->
            val numStr = match.groupValues[1].replace(",", ".")
            val num = numStr.toDoubleOrNull()
            if (num != null) return num * 1_000.0
        }

        val plainRegex = Regex("""\b(\d+([.,]\d+)?)\b""")
        plainRegex.find(lower.replace(".", ""))?.let { match ->
            val numStr = match.groupValues[1].replace(",", ".")
            return numStr.toDoubleOrNull()
        }

        return null
    }

    suspend fun parseOrAdviseMessage(
        userPrompt: String,
        totalBalance: Double,
        recentTx: List<Transaction>
    ): AiResponseResult = processConversationalMessage(
        userPrompt = userPrompt,
        pendingSession = null,
        userName = "Usuario",
        userAccounts = "Efectivo, Mercado Pago, Banco",
        totalBalance = totalBalance,
        recentTx = recentTx
    )

    suspend fun generateOnboardingDiagnosis(
        userName: String,
        mainGoal: String,
        monthlyIncome: Double,
        currency: String,
        answers: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "¡Excelente, $userName! He configurado tu plan inicial. Con un objetivo de $mainGoal y un ingreso meta de $currency${String.format("%,.0f", monthlyIncome)}, estructuraremos tus finanzas para priorizar ahorro directo del 20% mensual e inversión en activos de bajo riesgo."
        }

        val prompt = """
            Eres el Asesor Financiero IA Inteligente de la app NEXUS Finance.
            El usuario recién se registró y respondió el diagnóstico inicial:
            - Nombre: $userName
            - Meta principal: $mainGoal
            - Ingreso estimado mensual: $currency$monthlyIncome
            - Respuestas sobre el manejo de su dinero: $answers

            Genera una respuesta cordial, personalizada y motivadora de máximo 3 oraciones cortas en español.
            1. Saluda por su nombre.
            2. Evalúa cómo maneja su dinero y cómo su meta ($mainGoal) se alinea con sus ingresos.
            3. Brinda 1 regla o consejo clave personalizado para iniciar.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
        )

        try {
            val res = GeminiNetworkClient.api.generateContent(apiKey, request)
            val text = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) text.trim()
            else "¡Excelente, $userName! Diseñé un plan para alcanzar $mainGoal. Mantendremos un seguimiento constante para optimizar tu liquidez."
        } catch (e: Exception) {
            "¡Excelente, $userName! Tu plan personalizado para $mainGoal está listo. Registra tus movimientos diariamente para ajustar tu presupuesto."
        }
    }
}

