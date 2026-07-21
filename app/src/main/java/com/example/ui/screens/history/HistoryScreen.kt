package com.example.ui.screens.history

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.components.AddEditTransactionDialog
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

import androidx.compose.runtime.mutableIntStateOf

@Composable
fun HistoryScreen(
    viewModel: FinanceViewModel
) {
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val timelineEvents by viewModel.allTimelineEvents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCat by viewModel.selectedCategoryFilter.collectAsState()
    val selectedType by viewModel.selectedTypeFilter.collectAsState()
    val currencySymbol by viewModel.userCurrency.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Transactions, 1: Timeline
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }

    val categories = listOf("Todos", "Trabajos", "Ventas", "Vehículos", "Alimentación", "Servicios", "Seguros", "Varios")

    Scaffold(
        floatingActionButton = {
            if (activeTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = IndigoAiAccent,
                    contentColor = TextPrimaryWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Movimiento")
                }
            }
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBackground)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HISTORIAL Y EVENTOS",
                color = IndigoAiAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Registro Financiero",
                color = TextPrimaryWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tabs: Movimientos vs Línea de Tiempo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceCard)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == 0) IndigoAiAccent else DarkSurfaceCard)
                        .clickable { activeTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MOVIMIENTOS",
                        color = TextPrimaryWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == 1) IndigoAiAccent else DarkSurfaceCard)
                        .clickable { activeTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LÍNEA DE TIEMPO",
                        color = TextPrimaryWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (activeTab == 0) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Buscar por concepto o notas...", color = TextSecondaryMuted, fontSize = 14.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = TextSecondaryMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAiAccent, unfocusedBorderColor = DarkBorderLine,
                        focusedTextColor = TextPrimaryWhite, unfocusedTextColor = TextPrimaryWhite,
                        focusedContainerColor = DarkSurfaceCard, unfocusedContainerColor = DarkSurfaceCard
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Type Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedType == null) IndigoAiAccent else DarkSurfaceCard)
                            .border(1.dp, DarkBorderLine, RoundedCornerShape(10.dp))
                            .clickable { viewModel.setTypeFilter(null) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "TODOS", color = TextPrimaryWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedType == TransactionType.INCOME) EmeraldIncome else DarkSurfaceCard)
                            .border(1.dp, DarkBorderLine, RoundedCornerShape(10.dp))
                            .clickable { viewModel.setTypeFilter(TransactionType.INCOME) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "INGRESOS", color = TextPrimaryWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedType == TransactionType.EXPENSE) RoseExpense else DarkSurfaceCard)
                            .border(1.dp, DarkBorderLine, RoundedCornerShape(10.dp))
                            .clickable { viewModel.setTypeFilter(TransactionType.EXPENSE) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "EGRESOS", color = TextPrimaryWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Horizontal Filter Row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val isSelected = (cat == "Todos" && selectedCat == null) || (cat == selectedCat)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) IndigoAiAccent else DarkSurfaceCard)
                                .border(1.dp, DarkBorderLine, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setCategoryFilter(if (cat == "Todos") null else cat) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = cat, color = TextPrimaryWhite, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Movements List
                if (filteredTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron movimientos.",
                            color = TextSecondaryMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredTransactions) { tx ->
                            TransactionItemCard(
                                transaction = tx,
                                currencySymbol = currencySymbol,
                                onClick = { selectedTxForEdit = tx }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            } else {
                // Timeline Events View
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(timelineEvents) { event ->
                        TimelineEventCard(event = event)
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
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

@Composable
private fun TimelineEventCard(event: com.example.data.model.TimelineEvent) {
    val dateStr = remember(event.timestampMillis) {
        val sdf = java.text.SimpleDateFormat("dd MMM • HH:mm", java.util.Locale.getDefault())
        sdf.format(java.util.Date(event.timestampMillis))
    }

    val eventColor = when (event.eventType) {
        "ASSET_SALE" -> EmeraldIncome
        "ASSET_BUY" -> IndigoAiAccent
        "GOAL_PROGRESS" -> IndigoAiAccent
        else -> TextSecondaryMuted
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorderLine, RoundedCornerShape(14.dp)),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    color = TextPrimaryWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateStr,
                    color = TextSecondaryMuted,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.description,
                color = TextSecondaryMuted,
                fontSize = 12.sp
            )
            if (event.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = event.tags,
                    color = eventColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
