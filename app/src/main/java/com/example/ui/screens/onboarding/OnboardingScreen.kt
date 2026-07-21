package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: FinanceViewModel,
    onOnboardingCompleted: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) } // 0: Welcome, 1: Name, 2: Occupation, 3: Accounts, 4: Data Import, 5: Ready

    var name by remember { mutableStateOf("") }
    var selectedOccupation by remember { mutableStateOf("Compra/Venta de autos") }
    var selectedAccounts by remember { mutableStateOf(setOf("Efectivo", "Mercado Pago")) }
    var importDemoData by remember { mutableStateOf(true) }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            ObsidianBackground,
            DarkSurfaceCard,
            ObsidianBackground
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Progress (show if step > 0)
            if (step > 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NEXUS FINANCE AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoAiAccent,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Paso $step de 5",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { step / 5f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldIncome,
                        trackColor = DarkBorderLine
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content Switcher
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { width -> width } togetherWith fadeOut() + slideOutHorizontally { width -> -width }
                },
                label = "OnboardingSteps"
            ) { currentStep ->
                when (currentStep) {
                    0 -> Step0WelcomeScreen()
                    1 -> Step1NameInput(
                        name = name,
                        onNameChange = { name = it }
                    )
                    2 -> Step2OccupationSelect(
                        selectedOccupation = selectedOccupation,
                        onOccupationSelected = { selectedOccupation = it }
                    )
                    3 -> Step3AccountsSelect(
                        selectedAccounts = selectedAccounts,
                        onAccountsChanged = { selectedAccounts = it }
                    )
                    4 -> Step4DataChoice(
                        importDemoData = importDemoData,
                        onChoiceSelected = { importDemoData = it }
                    )
                    5 -> Step5ReadyScreen(
                        name = name,
                        occupation = selectedOccupation,
                        accounts = selectedAccounts,
                        importDemoData = importDemoData
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Action Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    0 -> {
                        Button(
                            onClick = { step = 1 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAiAccent)
                        ) {
                            Text(
                                text = "Comenzar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = TextPrimaryWhite
                            )
                        }
                    }
                    in 1..4 -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (step > 1) {
                                OutlinedButton(
                                    onClick = { step -= 1 },
                                    modifier = Modifier
                                        .weight(0.35f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLine)
                                ) {
                                    Text(
                                        text = "Atrás",
                                        color = TextSecondaryMuted,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (step == 1 && name.isBlank()) {
                                        name = "Martin"
                                    }
                                    step += 1
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoAiAccent)
                            ) {
                                Text(
                                    text = "Siguiente",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryWhite
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = TextPrimaryWhite
                                )
                            }
                        }
                    }
                    5 -> {
                        Button(
                            onClick = {
                                viewModel.completeOnboardingWithFlow(
                                    name = name,
                                    occupation = selectedOccupation,
                                    accounts = selectedAccounts.toList(),
                                    importDemoData = importDemoData
                                )
                                onOnboardingCompleted()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldIncome)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = TextPrimaryWhite,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Entrar al Dashboard",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryWhite
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step0WelcomeScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(IndigoAiAccent.copy(alpha = 0.2f))
                .border(2.dp, IndigoAiAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = IndigoAiAccent,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "👋 Bienvenido a\nNEXUS Finance AI",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Vamos a configurarlo en menos de 2 minutos.",
            fontSize = 15.sp,
            color = TextSecondaryMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorderLine, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "✨ ¿Qué obtendrás?",
                    color = TextPrimaryWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Control inteligente de ingresos, egresos y activos.\n• Diagnósticos matutinos con Inteligencia Artificial.\n• Cálculo automático de ROI en compra/venta de bienes.",
                    color = TextSecondaryMuted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun Step1NameInput(
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IndigoAiAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = IndigoAiAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Paso 1",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoAiAccent
                )
                Text(
                    text = "¿Cómo te llamás?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Ej: Martin", color = TextSecondaryMuted) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IndigoAiAccent,
                unfocusedBorderColor = DarkBorderLine,
                focusedContainerColor = DarkSurfaceCard,
                unfocusedContainerColor = DarkSurfaceCard,
                focusedTextColor = TextPrimaryWhite,
                unfocusedTextColor = TextPrimaryWhite
            ),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
private fun Step2OccupationSelect(
    selectedOccupation: String,
    onOccupationSelected: (String) -> Unit
) {
    val options = listOf(
        "Detailing",
        "Compra/Venta de autos",
        "Freelancer",
        "Comercio",
        "Otro"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IndigoAiAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = IndigoAiAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Paso 2",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoAiAccent
                )
                Text(
                    text = "¿A qué te dedicás?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                val isSelected = selectedOccupation == option
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) IndigoAiAccent.copy(alpha = 0.25f) else DarkSurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) IndigoAiAccent else DarkBorderLine,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onOccupationSelected(option) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            color = TextPrimaryWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = IndigoAiAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step3AccountsSelect(
    selectedAccounts: Set<String>,
    onAccountsChanged: (Set<String>) -> Unit
) {
    val options = listOf("Efectivo", "Mercado Pago", "Banco", "Otro")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IndigoAiAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = IndigoAiAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Paso 3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoAiAccent
                )
                Text(
                    text = "¿Qué cuentas usás?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                val isSelected = selectedAccounts.contains(option)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) IndigoAiAccent.copy(alpha = 0.25f) else DarkSurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) IndigoAiAccent else DarkBorderLine,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            val next = if (isSelected) selectedAccounts - option else selectedAccounts + option
                            onAccountsChanged(next)
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            color = TextPrimaryWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp
                        )
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                val next = if (isSelected) selectedAccounts - option else selectedAccounts + option
                                onAccountsChanged(next)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = IndigoAiAccent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step4DataChoice(
    importDemoData: Boolean,
    onChoiceSelected: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IndigoAiAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FolderZip,
                    contentDescription = null,
                    tint = IndigoAiAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Paso 4",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoAiAccent
                )
                Text(
                    text = "¿Datos de prueba o cero?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Option 1: Demo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (importDemoData) EmeraldIncome.copy(alpha = 0.2f) else DarkSurfaceCard)
                .border(
                    1.dp,
                    if (importDemoData) EmeraldIncome else DarkBorderLine,
                    RoundedCornerShape(16.dp)
                )
                .clickable { onChoiceSelected(true) }
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📦 Cargar Datos de Prueba",
                        color = TextPrimaryWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (importDemoData) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldIncome)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Recomendado. Incluye vehículos de muestra, caja inicial e historial para previsualizar los diagnósticos IA.",
                    color = TextSecondaryMuted,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Clean slate
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (!importDemoData) IndigoAiAccent.copy(alpha = 0.2f) else DarkSurfaceCard)
                .border(
                    1.dp,
                    if (!importDemoData) IndigoAiAccent else DarkBorderLine,
                    RoundedCornerShape(16.dp)
                )
                .clickable { onChoiceSelected(false) }
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨ Empezar de Cero",
                        color = TextPrimaryWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (!importDemoData) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = IndigoAiAccent)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pizarra limpia para ingresar tus movimientos y activos reales desde el primer día.",
                    color = TextSecondaryMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun Step5ReadyScreen(
    name: String,
    occupation: String,
    accounts: Set<String>,
    importDemoData: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(EmeraldIncome.copy(alpha = 0.2f))
                .border(2.dp, EmeraldIncome, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldIncome,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Listo, ${name.ifBlank { "Martin" }}!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tu entorno financiero está configurado correctamente.",
            fontSize = 14.sp,
            color = TextSecondaryMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorderLine, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "👤 Perfil: ${name.ifBlank { "Martin" }}", color = TextPrimaryWhite, fontSize = 14.sp)
                Text(text = "💼 Rubro: $occupation", color = TextPrimaryWhite, fontSize = 14.sp)
                Text(text = "💳 Cuentas: ${accounts.joinToString(", ")}", color = TextPrimaryWhite, fontSize = 14.sp)
                Text(
                    text = if (importDemoData) "📦 Modo: Datos Demo Cargados" else "✨ Modo: Pizarra Limpia",
                    color = EmeraldIncome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
