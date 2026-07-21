package com.example.ui.screens.settings

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddEditGoalDialog
import com.example.ui.theme.DarkBorderLine
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoAiAccent
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.Exporter

@Composable
fun SettingsScreen(
    viewModel: FinanceViewModel
) {
    val context = LocalContext.current
    val currentCurrency by viewModel.userCurrency.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val summary by viewModel.financialSummary.collectAsState()

    var showGoalDialog by remember { mutableStateOf(false) }

    val currencies = listOf("$", "USD", "ARS", "EUR", "MXN", "COP")

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
                text = "CONFIGURACIÓN",
                color = IndigoAiAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Ajustes & Reportes",
                color = TextPrimaryWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. Profile Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, DarkBorderLine, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(IndigoAiAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = TextPrimaryWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Usuario Principal",
                            color = TextPrimaryWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Plan V1 Personal - Sin límites",
                            color = TextSecondaryMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 2. Currency Preferences
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
                        text = "MONEDA Y FORMATO",
                        color = TextSecondaryMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currencies.forEach { curr ->
                            val isSelected = curr == currentCurrency
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) IndigoAiAccent else DarkBorderLine.copy(alpha = 0.3f))
                                    .clickable { viewModel.setCurrency(curr) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = curr,
                                    color = TextPrimaryWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Export Options (PDF & Excel/CSV)
        item {
            Text(
                text = "EXPORTACIÓN DE DATOS",
                color = TextSecondaryMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // PDF Export
                SettingsActionCard(
                    title = "Exportar Reporte PDF",
                    subtitle = "Genera un balance profesional en PDF listo para enviar",
                    icon = Icons.Default.PictureAsPdf,
                    iconTint = RoseExpense,
                    onClick = {
                        val pdfFile = Exporter.exportPdf(context, summary, transactions)
                        if (pdfFile != null) {
                            Exporter.shareFile(context, pdfFile, "application/pdf")
                        } else {
                            Toast.makeText(context, "Error al generar PDF", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Excel / CSV Export
                SettingsActionCard(
                    title = "Exportar Excel / CSV",
                    subtitle = "Descarga todos tus movimientos en formato CSV compatible con Excel",
                    icon = Icons.Default.TableChart,
                    iconTint = EmeraldIncome,
                    onClick = {
                        val csvFile = Exporter.exportCsv(context, transactions)
                        if (csvFile != null) {
                            Exporter.shareFile(context, csvFile, "text/csv")
                        } else {
                            Toast.makeText(context, "Error al generar CSV", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        // 4. Objetivos
        item {
            SettingsActionCard(
                title = "Gestionar Objetivos",
                subtitle = "Agregá o modificá metas financieras (Ej: Comprar Hilux)",
                icon = Icons.Default.Flag,
                iconTint = IndigoAiAccent,
                onClick = { showGoalDialog = true }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if (showGoalDialog) {
        AddEditGoalDialog(
            onDismiss = { showGoalDialog = false },
            onSave = { title, target, current, cat ->
                viewModel.addGoal(title, target, current, cat)
            }
        )
    }
}

@Composable
fun SettingsActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, DarkBorderLine, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = title, color = TextPrimaryWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, color = TextSecondaryMuted, fontSize = 12.sp)
                }
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Ver", tint = TextSecondaryMuted)
        }
    }
}
