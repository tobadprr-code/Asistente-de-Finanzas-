package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Goal
import com.example.data.model.Transaction
import com.example.ui.components.*
import com.example.ui.theme.*
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
    val userProfile by viewModel.userProfile.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalForEdit by remember { mutableStateOf<Goal?>(null) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusBlackPrimary)
    ) {
        // NEXUS Top Header Bar
        NexusTopBar(
            userName = userProfile?.name ?: "Martín",
            notificationCount = 3
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hola, ${userProfile?.name ?: "Martín"}",
                                color = NexusPureWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "👋", fontSize = 20.sp)
                        }
                        Text(
                            text = "Resumen del día",
                            color = NexusGray500,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    NexusBadge(text = "PRO IA", isNeon = true)
                }
            }

            // 1. STATS GRID (Saldo Disponible, Ingresos, Egresos, Resultado)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Main Saldo Card
                    NexusCard(hasGlow = true) {
                        Text(
                            text = "Saldo disponible",
                            color = NexusGray500,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = CurrencyFormatter.format(summary.totalBalance, currencySymbol),
                            color = NexusPureWhite,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "↗ 8.5% vs ayer",
                                color = NexusNeonGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NexusStatCard(
                            title = "Ingresos del mes",
                            value = CurrencyFormatter.format(summary.monthlyIncome, currencySymbol),
                            trendText = "12.5% vs mes pasado",
                            isPositiveTrend = true,
                            modifier = Modifier.weight(1f)
                        )
                        NexusStatCard(
                            title = "Gastos del mes",
                            value = CurrencyFormatter.format(summary.monthlyExpense, currencySymbol),
                            trendText = "8.2% vs mes pasado",
                            isPositiveTrend = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    NexusStatCard(
                        title = "Resultado del mes",
                        value = CurrencyFormatter.format(summary.netGain, currencySymbol),
                        trendText = "18.7% vs mes pasado",
                        isPositiveTrend = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. INGRESOS VS EGRESOS COMPARISON
            item {
                NexusCard {
                    Text(
                        text = "Ingresos vs Gastos",
                        color = NexusGray300,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val maxVal = maxOf(summary.monthlyIncome, summary.monthlyExpense, 1.0)
                    val incRatio = (summary.monthlyIncome / maxVal).toFloat().coerceIn(0.05f, 1f)
                    val expRatio = (summary.monthlyExpense / maxVal).toFloat().coerceIn(0.05f, 1f)

                    // Ingresos Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Ingresos", color = NexusNeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = CurrencyFormatter.format(summary.monthlyIncome, currencySymbol), color = NexusPureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { incRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = NexusNeonGreen,
                        trackColor = NexusBlackCard
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Egresos Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Gastos", color = NexusExpenseRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = CurrencyFormatter.format(summary.monthlyExpense, currencySymbol), color = NexusPureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { expRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = NexusExpenseRed,
                        trackColor = NexusBlackCard
                    )
                }
            }

            // 3. POR CATEGORÍA BREAKDOWN
            item {
                NexusCard {
                    Text(
                        text = "Distribución por Categoría",
                        color = NexusGray300,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (categories.isEmpty()) {
                        Text(
                            text = "No hay registros grabados en este período.",
                            color = NexusGray500,
                            fontSize = 13.sp
                        )
                    } else {
                        categories.forEach { cat ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = cat.category, color = NexusPureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${CurrencyFormatter.format(cat.amount, currencySymbol)} (${(cat.percentage * 100).toInt()}%)",
                                        color = NexusGray300,
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
                                    color = if (cat.type == com.example.data.model.TransactionType.INCOME) NexusNeonGreen else NexusGray300,
                                    trackColor = NexusBlackCard
                                )
                            }
                        }
                    }
                }
            }

            // 4. OBJETIVOS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Objetivos Financieros",
                        color = NexusGray300,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+ Agregar",
                        color = NexusNeonGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showAddGoalDialog = true }
                    )
                }
            }

            items(goals) { goal ->
                NexusCard(onClick = { selectedGoalForEdit = goal }) {
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
                                    .background(NexusNeonGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Flag, contentDescription = "Objetivo", tint = NexusNeonGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = goal.title, color = NexusPureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = goal.category, color = NexusGray500, fontSize = 12.sp)
                            }
                        }
                        Text(
                            text = "${goal.progressPercentage}%",
                            color = NexusNeonGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { goal.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NexusNeonGreen,
                        trackColor = NexusBlackCard
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ahorrado: ${CurrencyFormatter.format(goal.currentAmount, currencySymbol)}",
                            color = NexusGray500,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Meta: ${CurrencyFormatter.format(goal.targetAmount, currencySymbol)}",
                            color = NexusGray500,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 5. ÚLTIMOS MOVIMIENTOS
            item {
                Text(
                    text = "Últimos movimientos",
                    color = NexusGray300,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
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

