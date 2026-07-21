package com.example.ui.screens.dashboard

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Goal
import com.example.data.model.Transaction
import com.example.ui.components.AddEditGoalDialog
import com.example.ui.components.AddEditTransactionDialog
import com.example.ui.components.StatCard
import com.example.ui.components.TransactionItemCard
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
fun DashboardScreen(
    viewModel: FinanceViewModel
) {
    val summary by viewModel.financialSummary.collectAsState()
    val categories by viewModel.categorySummaries.collectAsState()
    val goals by viewModel.allGoals.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val currencySymbol by viewModel.userCurrency.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalForEdit by remember { mutableStateOf<Goal?>(null) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(
                text = "PANEL FINANCIERO",
                color = IndigoAiAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dashboard Completo",
                color = TextPrimaryWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. STATS GRID (Saldo Actual, Ingresos, Egresos, Ganancia)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Main Saldo Actual Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurfaceCard)
                        .border(1.dp, DarkBorderLine, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(text = "SALDO TOTAL NETO", color = TextSecondaryMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = CurrencyFormatter.format(summary.totalBalance, currencySymbol),
                            color = TextPrimaryWhite,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Ingresos",
                        amount = summary.monthlyIncome,
                        currencySymbol = currencySymbol,
                        icon = Icons.Default.ArrowUpward,
                        iconTint = EmeraldIncome,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Egresos",
                        amount = summary.monthlyExpense,
                        currencySymbol = currencySymbol,
                        icon = Icons.Default.ArrowDownward,
                        iconTint = RoseExpense,
                        modifier = Modifier.weight(1f)
                    )
                }

                StatCard(
                    title = "Ganancia Neta",
                    amount = summary.netGain,
                    currencySymbol = currencySymbol,
                    icon = Icons.Default.TrendingUp,
                    iconTint = IndigoAiAccent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 2. INGRESOS VS EGRESOS BAR CHART COMPARISON
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
                        text = "COMPARATIVA INGRESOS VS EGRESOS",
                        color = TextSecondaryMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val maxVal = maxOf(summary.monthlyIncome, summary.monthlyExpense, 1.0)
                    val incRatio = (summary.monthlyIncome / maxVal).toFloat().coerceIn(0.05f, 1f)
                    val expRatio = (summary.monthlyExpense / maxVal).toFloat().coerceIn(0.05f, 1f)

                    // Ingresos Bar
                    Text(text = "Ingresos (${CurrencyFormatter.format(summary.monthlyIncome, currencySymbol)})", color = EmeraldIncome, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { incRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = EmeraldIncome,
                        trackColor = DarkBorderLine
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Egresos Bar
                    Text(text = "Egresos (${CurrencyFormatter.format(summary.monthlyExpense, currencySymbol)})", color = RoseExpense, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { expRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = RoseExpense,
                        trackColor = DarkBorderLine
                    )
                }
            }
        }

        // 3. POR CATEGORÍA BREAKDOWN
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
                        text = "MOVIMIENTOS POR CATEGORÍA",
                        color = TextSecondaryMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (categories.isEmpty()) {
                        Text(
                            text = "No hay datos de categorías grabados.",
                            color = TextSecondaryMuted,
                            fontSize = 13.sp
                        )
                    } else {
                        categories.forEach { cat ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = cat.category, color = TextPrimaryWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${CurrencyFormatter.format(cat.amount, currencySymbol)} (${(cat.percentage * 100).toInt()}%)",
                                        color = TextSecondaryMuted,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { cat.percentage },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (cat.type == com.example.data.model.TransactionType.INCOME) EmeraldIncome else IndigoAiAccent,
                                    trackColor = DarkBorderLine
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. OBJETIVOS (Goals Section - e.g. "Comprar Hilux")
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OBJETIVOS FINANCIEROS",
                    color = TextSecondaryMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "+ Agregar",
                    color = IndigoAiAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAddGoalDialog = true }
                )
            }
        }

        items(goals) { goal ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, DarkBorderLine, RoundedCornerShape(18.dp))
                    .clickable { selectedGoalForEdit = goal }
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IndigoAiAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Flag, contentDescription = "Objetivo", tint = IndigoAiAccent, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = goal.title, color = TextPrimaryWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = goal.category, color = TextSecondaryMuted, fontSize = 12.sp)
                            }
                        }
                        Text(
                            text = "${goal.progressPercentage}%",
                            color = IndigoAiAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { goal.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = IndigoAiAccent,
                        trackColor = DarkBorderLine
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ahorrado: ${CurrencyFormatter.format(goal.currentAmount, currencySymbol)}",
                            color = TextSecondaryMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Meta: ${CurrencyFormatter.format(goal.targetAmount, currencySymbol)}",
                            color = TextSecondaryMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5. RECENT MOVEMENTS
        item {
            Text(
                text = "ÚLTIMOS MOVIMIENTOS",
                color = TextSecondaryMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
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

    if (showAddGoalDialog) {
        AddEditGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onSave = { title, target, current, cat ->
                viewModel.addGoal(title, target, current, cat)
            }
        )
    }

    if (selectedGoalForEdit != null) {
        AddEditGoalDialog(
            initialGoal = selectedGoalForEdit,
            onDismiss = { selectedGoalForEdit = null },
            onSave = { title, target, current, cat ->
                selectedGoalForEdit?.let { existing ->
                    viewModel.updateGoal(
                        existing.copy(
                            title = title,
                            targetAmount = target,
                            currentAmount = current,
                            category = cat
                        )
                    )
                }
            },
            onDelete = {
                selectedGoalForEdit?.let { existing ->
                    viewModel.deleteGoal(existing.id)
                }
                selectedGoalForEdit = null
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
