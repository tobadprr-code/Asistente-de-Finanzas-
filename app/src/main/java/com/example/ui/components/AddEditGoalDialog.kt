package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Goal
import com.example.ui.theme.DarkBorderLine
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.IndigoAiAccent
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun AddEditGoalDialog(
    initialGoal: Goal? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, targetAmount: Double, currentAmount: Double, category: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(initialGoal?.title ?: "") }
    var targetText by remember { mutableStateOf(initialGoal?.targetAmount?.let { if (it > 0) it.toString() else "" } ?: "") }
    var currentText by remember { mutableStateOf(initialGoal?.currentAmount?.let { if (it > 0) it.toString() else "" } ?: "") }
    var category by remember { mutableStateOf(initialGoal?.category ?: "Vehículos") }

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
                        text = if (initialGoal == null) "NUEVO OBJETIVO" else "EDITAR OBJETIVO",
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre del objetivo", color = TextSecondaryMuted) },
                    placeholder = { Text("Ej: Comprar Hilux", color = TextSecondaryMuted.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAiAccent, unfocusedBorderColor = DarkBorderLine,
                        focusedTextColor = TextPrimaryWhite, unfocusedTextColor = TextPrimaryWhite
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Monto Objetivo ($)", color = TextSecondaryMuted) },
                    placeholder = { Text("Ej: 25000000", color = TextSecondaryMuted.copy(alpha = 0.5f)) },
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
                    value = currentText,
                    onValueChange = { currentText = it },
                    label = { Text("Monto Acumulado ($)", color = TextSecondaryMuted) },
                    placeholder = { Text("Ej: 10500000", color = TextSecondaryMuted.copy(alpha = 0.5f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAiAccent, unfocusedBorderColor = DarkBorderLine,
                        focusedTextColor = TextPrimaryWhite, unfocusedTextColor = TextPrimaryWhite
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = RoseExpense)
                        }
                    }
                    Button(
                        onClick = {
                            val tgt = targetText.toDoubleOrNull() ?: 0.0
                            val cur = currentText.toDoubleOrNull() ?: 0.0
                            if (title.isNotBlank() && tgt > 0) {
                                onSave(title, tgt, cur, category)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoAiAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Guardar Objetivo", color = TextPrimaryWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
