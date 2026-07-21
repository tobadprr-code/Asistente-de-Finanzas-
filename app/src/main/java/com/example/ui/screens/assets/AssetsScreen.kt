package com.example.ui.screens.assets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Asset
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    viewModel: FinanceViewModel
) {
    val assets by viewModel.allAssets.collectAsState()
    val currencySymbol by viewModel.userCurrency.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var assetToSell by remember { mutableStateOf<Asset?>(null) }

    val activeAssets = assets.filter { it.status == "ACTIVE" }
    val soldAssets = assets.filter { it.status == "SOLD" }

    val totalActiveCapital = activeAssets.sumOf { it.totalCost }
    val totalProfitRealized = soldAssets.sumOf { it.profit }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gesti\u00f3n de Activos & Usados",
                            color = TextPrimaryWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Compra, adecuaci\u00f3n, venta y retorno %",
                            color = TextSecondaryMuted,
                            fontSize = 13.sp
                        )
                    }

                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = IndigoAiAccent,
                        contentColor = TextPrimaryWhite,
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Activo")
                    }
                }
            }

            // Capital & Profit Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, DarkBorderLine, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Capital Activo",
                                color = TextSecondaryMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currencySymbol${String.format("%,.0f", totalActiveCapital)}",
                                color = TextPrimaryWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activeAssets.size} unidades en lista",
                                color = TextSecondaryMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, EmeraldIncome.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ganancia Realizada",
                                color = EmeraldIncome,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$currencySymbol${String.format("%,.0f", totalProfitRealized)}",
                                color = EmeraldIncome,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${soldAssets.size} ventas cerradas",
                                color = TextSecondaryMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Active Assets Section
            item {
                Text(
                    text = "En Stock / En Preparaci\u00f3n (${activeAssets.size})",
                    color = TextPrimaryWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (activeAssets.isEmpty()) {
                item {
                    Text(
                        text = "No ten\u00e9s activos activos registrados.",
                        color = TextSecondaryMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                items(activeAssets) { asset ->
                    ActiveAssetCard(
                        asset = asset,
                        currencySymbol = currencySymbol,
                        onSellClick = { assetToSell = asset }
                    )
                }
            }

            // Closed Sales Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Historial de Ventas & Márgenes Realizados",
                    color = TextPrimaryWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (soldAssets.isEmpty()) {
                item {
                    Text(
                        text = "A\u00fan no has registrado ventas completadas.",
                        color = TextSecondaryMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                items(soldAssets) { asset ->
                    SoldAssetCard(
                        asset = asset,
                        currencySymbol = currencySymbol
                    )
                }
            }
        }
    }

    // Modal Add Asset
    if (showAddDialog) {
        AddAssetDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, purchase, extra, tags, notes ->
                viewModel.addAsset(name, category, purchase, extra, tags, notes)
                showAddDialog = false
            }
        )
    }

    // Modal Sell Asset
    if (assetToSell != null) {
        SellAssetDialog(
            asset = assetToSell!!,
            currencySymbol = currencySymbol,
            onDismiss = { assetToSell = null },
            onConfirm = { salePrice ->
                viewModel.sellAsset(assetToSell!!, salePrice)
                assetToSell = null
            }
        )
    }
}

@Composable
private fun ActiveAssetCard(
    asset: Asset,
    currencySymbol: String,
    onSellClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorderLine, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(IndigoAiAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = IndigoAiAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = asset.name,
                            color = TextPrimaryWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = asset.category + if (asset.tags.isNotBlank()) " • ${asset.tags}" else "",
                            color = TextSecondaryMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = onSellClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldIncome),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sell,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Cerrar Venta", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Costo Compra", color = TextSecondaryMuted, fontSize = 11.sp)
                    Text(
                        text = "$currencySymbol${String.format("%,.0f", asset.purchasePrice)}",
                        color = TextPrimaryWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(text = "Gastos / Adecuación", color = TextSecondaryMuted, fontSize = 11.sp)
                    Text(
                        text = "$currencySymbol${String.format("%,.0f", asset.extraExpenses)}",
                        color = TextPrimaryWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(text = "Inversión Total", color = IndigoAiAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$currencySymbol${String.format("%,.0f", asset.totalCost)}",
                        color = IndigoAiAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (asset.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notas: ${asset.notes}",
                    color = TextSecondaryMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SoldAssetCard(
    asset: Asset,
    currencySymbol: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EmeraldIncome.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldIncome.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = EmeraldIncome,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = asset.name,
                            color = TextPrimaryWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Venta Cerrada ${if (asset.tags.isNotBlank()) "• ${asset.tags}" else ""}",
                            color = EmeraldIncome,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldIncome.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+${String.format("%.1f", asset.roiPercentage)}% ROI",
                        color = EmeraldIncome,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Inversión Total", color = TextSecondaryMuted, fontSize = 11.sp)
                    Text(
                        text = "$currencySymbol${String.format("%,.0f", asset.totalCost)}",
                        color = TextPrimaryWhite,
                        fontSize = 13.sp
                    )
                }
                Column {
                    Text(text = "Precio de Venta", color = TextSecondaryMuted, fontSize = 11.sp)
                    Text(
                        text = "$currencySymbol${String.format("%,.0f", asset.salePrice)}",
                        color = TextPrimaryWhite,
                        fontSize = 13.sp
                    )
                }
                Column {
                    Text(text = "Ganancia Neta", color = EmeraldIncome, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "+$currencySymbol${String.format("%,.0f", asset.profit)}",
                        color = EmeraldIncome,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AddAssetDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, purchasePrice: Double, extraExpenses: Double, tags: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Vehículos") }
    var purchasePriceText by remember { mutableStateOf("") }
    var extraExpensesText by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("#concesionaria") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceCard,
        title = {
            Text(text = "Registrar Nuevo Activo / Vehículo", color = TextPrimaryWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre / Modelo (Ej: Volkswagen Gol 2018)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Precio Compra ($currencySymbol)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = extraExpensesText,
                        onValueChange = { extraExpensesText = it },
                        label = { Text("Adecuación/Gastos") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Etiquetas (Ej: #gol #concesionaria)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Detalles / Estado") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val purchase = purchasePriceText.toDoubleOrNull() ?: 0.0
                    val extra = extraExpensesText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && purchase > 0) {
                        onConfirm(name, category, purchase, extra, tags, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoAiAccent)
            ) {
                Text("Guardar Activo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondaryMuted)
            }
        }
    )
}

@Composable
private fun SellAssetDialog(
    asset: Asset,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (salePrice: Double) -> Unit
) {
    var salePriceText by remember { mutableStateOf("") }

    val salePrice = salePriceText.toDoubleOrNull() ?: 0.0
    val estimatedProfit = salePrice - asset.totalCost
    val estimatedRoi = if (asset.totalCost > 0 && salePrice > 0) (estimatedProfit / asset.totalCost) * 100 else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceCard,
        title = {
            Text(text = "Cerrar Venta de ${asset.name}", color = TextPrimaryWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Inversión total registrada: $currencySymbol${String.format("%,.0f", asset.totalCost)}",
                    color = TextSecondaryMuted,
                    fontSize = 13.sp
                )
                OutlinedTextField(
                    value = salePriceText,
                    onValueChange = { salePriceText = it },
                    label = { Text("Precio de Venta Final ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (salePrice > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldIncome.copy(alpha = 0.15f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Ganancia Neta: +$currencySymbol${String.format("%,.0f", estimatedProfit)}",
                                color = EmeraldIncome,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Margen de Rentabilidad: ${String.format("%.1f", estimatedRoi)}% ROI",
                                color = EmeraldIncome,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (salePrice > 0) {
                        onConfirm(salePrice)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldIncome)
            ) {
                Text("Confirmar Venta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondaryMuted)
            }
        }
    )
}
