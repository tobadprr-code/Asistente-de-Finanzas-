package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import com.example.ui.components.AddEditTransactionDialog
import com.example.ui.components.QuickTextEntryModal
import com.example.ui.components.StatCard
import com.example.ui.components.TransactionItemCard
import com.example.ui.components.VoiceRecordingModal
import com.example.ui.theme.CyanAiAccent
import com.example.ui.theme.DarkBorderLine
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoAiAccent
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CurrencyFormatter

@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    onNavigateToChat: () -> Unit
) {
    val morningSummary by viewModel.morningSummaryText.collectAsState()
    val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsState()
    val summary by viewModel.financialSummary.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val currencySymbol by viewModel.userCurrency.collectAsState()

    var showVoiceModal by remember { mutableStateOf(false) }
    var showQuickTextModal by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Top App Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VALOR",
                        color = IndigoAiAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Resumen Inteligente",
                        color = TextPrimaryWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceCard)
                        .border(1.dp, DarkBorderLine, CircleShape)
                        .clickable { viewModel.refreshMorningSummary() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isGeneratingSummary) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = IndigoAiAccent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar AI",
                            tint = TextSecondaryMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 1. AI MORNING SMART SUMMARY CARD (First thing the user sees!)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                IndigoAiAccent.copy(alpha = 0.25f),
                                DarkSurfaceCard,
                                CyanAiAccent.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(1.dp, IndigoAiAccent.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = CyanAiAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ASESOR FINANCIERO IA",
                                color = CyanAiAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "HOY",
                            color = TextSecondaryMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = morningSummary.ifBlank { "Procesando recomendación para tu dinero..." },
                        color = TextPrimaryWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // 2. LARGE PRIMARY ACTION BUTTONS BELOW SUMMARY (🎤 HABLAR | ⌨ ESCRIBIR)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 🎤 Hablar Button
                Button(
                    onClick = { showVoiceModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAiAccent),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Hablar",
                            tint = TextPrimaryWhite,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hablar",
                            color = TextPrimaryWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ⌨ Escribir Button
                Button(
                    onClick = { showQuickTextModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceCard),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .border(1.dp, DarkBorderLine, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Escribir",
                            tint = TextPrimaryWhite,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Escribir",
                            color = TextPrimaryWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. DASHBOARD RESUMIDO
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RESUMEN DE CUENTA",
                    color = TextSecondaryMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, DarkBorderLine, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Saldo Actual Disponible",
                        color = TextSecondaryMuted,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = CurrencyFormatter.format(summary.totalBalance, currencySymbol),
                        color = TextPrimaryWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DarkBorderLine)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Ingresos", color = TextSecondaryMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+${CurrencyFormatter.format(summary.monthlyIncome, currencySymbol)}",
                                color = EmeraldIncome,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Egresos", color = TextSecondaryMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "-${CurrencyFormatter.format(summary.monthlyExpense, currencySymbol)}",
                                color = RoseExpense,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. RECENT MOVEMENTS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ÚLTIMOS MOVIMIENTOS",
                    color = TextSecondaryMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "+ Nuevo",
                    color = IndigoAiAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAddDialog = true }
                )
            }
        }

        items(transactions.take(5)) { tx ->
            TransactionItemCard(
                transaction = tx,
                currencySymbol = currencySymbol,
                onClick = { selectedTxForEdit = tx }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if (showVoiceModal) {
        VoiceRecordingModal(
            onDismiss = { showVoiceModal = false },
            onSpeechCaptured = { transcript ->
                viewModel.sendChatMessage(transcript)
                onNavigateToChat()
            }
        )
    }

    if (showQuickTextModal) {
        QuickTextEntryModal(
            onDismiss = { showQuickTextModal = false },
            onSubmitText = { text ->
                viewModel.sendChatMessage(text)
                onNavigateToChat()
            }
        )
    }

    if (showAddDialog) {
        AddEditTransactionDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, amt, type, category, method, notes ->
                viewModel.addTransaction(title, amt, type, category, method, notes)
            }
        )
    }

    if (selectedTxForEdit != null) {
        AddEditTransactionDialog(
            initialTransaction = selectedTxForEdit,
            onDismiss = { selectedTxForEdit = null },
            onSave = { title, amt, type, category, method, notes ->
                selectedTxForEdit?.let { existing ->
                    viewModel.updateTransaction(
                        existing.copy(
                            title = title,
                            amount = amt,
                            type = type,
                            category = category,
                            paymentMethod = method,
                            notes = notes
                        )
                    )
                }
            },
            onDelete = {
                selectedTxForEdit?.let { existing ->
                    viewModel.deleteTransaction(existing.id)
                }
                selectedTxForEdit = null
            }
        )
    }
}
