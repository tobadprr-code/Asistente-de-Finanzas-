package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.theme.DarkBorderLine
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoAiAccent
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun AddEditTransactionDialog(
    initialTransaction: Transaction? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, type: TransactionType, category: String, paymentMethod: String, notes: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var amountText by remember { mutableStateOf(initialTransaction?.amount?.let { if (it > 0) it.toString() else "" } ?: "") }
    var selectedType by remember { mutableStateOf(initialTransaction?.type ?: TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(initialTransaction?.category ?: "Varios") }
    var paymentMethod by remember { mutableStateOf(initialTransaction?.paymentMethod ?: "Transferencia") }
    var notes by remember { mutableStateOf(initialTransaction?.notes ?: "") }

    val categories = listOf("Trabajos", "Ventas", "Vehículos", "Alimentación", "Servicios", "Seguros", "Varios")
    val methods = listOf("Transferencia", "Efectivo", "Tarjeta", "Cripto")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLine),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialTransaction == null) "NUEVO MOVIMIENTO" else "EDITAR MOVIMIENTO",
                        color = TextPrimaryWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondaryMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type Selector (Income vs Expense)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedType == TransactionType.INCOME) EmeraldIncome else DarkBorderLine.copy(alpha = 0.3f))
                            .clickable { selectedType = TransactionType.INCOME }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "INGRESO", color = TextPrimaryWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedType == TransactionType.EXPENSE) RoseExpense else DarkBorderLine.copy(alpha = 0.3f))
                            .clickable { selectedType = TransactionType.EXPENSE }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "EGRESO", color = TextPrimaryWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto ($)", color = TextSecondaryMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAiAccent, unfocusedBorderColor = DarkBorderLine,
                        focusedTextColor = TextPrimaryWhite, unfocusedTextColor = TextPrimaryWhite
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título / Concepto", color = TextSecondaryMuted) },
                    placeholder = { Text("Ej: Venta de vehículo", color = TextSecondaryMuted.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAiAccent, unfocusedBorderColor = DarkBorderLine,
                        focusedTextColor = TextPrimaryWhite, unfocusedTextColor = TextPrimaryWhite
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Categoría:", color = TextSecondaryMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (category == cat) IndigoAiAccent else DarkBorderLine.copy(alpha = 0.3f))
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = cat, color = TextPrimaryWhite, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Método de Pago:", color = TextSecondaryMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.forEach { m ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (paymentMethod == m) IndigoAiAccent else DarkBorderLine.copy(alpha = 0.3f))
                                .clickable { paymentMethod = m }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = m, color = TextPrimaryWhite, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(RoseExpense.copy(alpha = 0.2f))
                                .size(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = RoseExpense)
                        }
                    }
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onSave(title, amt, selectedType, category, paymentMethod, notes)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoAiAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Guardar", color = TextPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
