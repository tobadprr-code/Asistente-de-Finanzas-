package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.components.NexusBadge
import com.example.ui.components.NexusInput
import com.example.ui.components.NexusTopBar
import com.example.ui.components.VoiceRecordingModal
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CurrencyFormatter

@Composable
fun ChatScreen(
    viewModel: FinanceViewModel
) {
    val messages by viewModel.chatMessages.collectAsState()
    val currencySymbol by viewModel.userCurrency.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val pendingSession by viewModel.activePendingSession.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showVoiceModal by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val dynamicPrompts = when {
        pendingSession?.amount == null -> listOf("8 millones", "12 millones", "45 mil", "250 mil")
        pendingSession?.paymentMethod.isNullOrBlank() -> listOf("Transferencia", "Efectivo", "Mercado Pago", "Banco")
        else -> listOf(
            "Vendí un Gol",
            "Gasté 45 mil en la farmacia",
            "¿Cuánto gasté este mes?",
            "¿Cómo viene mi economía?"
        )
    }

    LaunchedEffect(messages.size, isAiThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusBlackPrimary)
    ) {
        // Top Bar
        NexusTopBar(userName = "Martín", notificationCount = 3)

        // Chat Header Title Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NexusBlackSecondary)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NexusNeonGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = NexusNeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NEXUS FINANCIERO IA",
                    color = NexusPureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Registro por conversación y análisis",
                    color = NexusGray500,
                    fontSize = 11.sp
                )
            }
            NexusBadge(text = "CONECTADO", isNeon = true)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NexusBorderSubtle)
        )

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .testTag("chat_messages_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            items(messages) { msg ->
                ChatMessageBubble(msg = msg, currencySymbol = currencySymbol)
            }

            if (isAiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(NexusBlackSecondary)
                                .border(1.dp, NexusNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Pensando",
                                    tint = NexusNeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "NEXUS está procesando...",
                                    color = NexusGray300,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Quick Suggestions Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dynamicPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(NexusBlackSecondary)
                        .border(1.dp, NexusBorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { viewModel.sendChatMessage(prompt) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("quick_prompt_$prompt")
                ) {
                    Text(
                        text = prompt,
                        color = NexusPureWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NexusBlackSecondary)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showVoiceModal = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NexusBlackCard)
                    .border(1.dp, NexusBorderSubtle, CircleShape)
                    .size(42.dp)
                    .testTag("voice_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Grabar voz",
                    tint = NexusNeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            NexusInput(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = "Escribí o hablá un movimiento...",
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendChatMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NexusNeonGreen)
                    .size(42.dp)
                    .testTag("send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = NexusBlackPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showVoiceModal) {
        VoiceRecordingModal(
            onDismiss = { showVoiceModal = false },
            onSpeechCaptured = { transcript ->
                viewModel.sendChatMessage(transcript)
            }
        )
    }
}

@Composable
fun ChatMessageBubble(
    msg: ChatMessage,
    currencySymbol: String
) {
    val isUser = msg.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) NexusNeonGreen else NexusBlackSecondary)
                .border(
                    width = 1.dp,
                    color = if (isUser) NexusNeonGreen else NexusBorderSubtle,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = msg.text,
                    color = if (isUser) NexusBlackPrimary else NexusPureWhite,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
                )

                if (msg.isAutoRegisteredTx && msg.registeredTitle != null && msg.registeredAmount != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexusBlackCard)
                            .border(1.dp, NexusNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Registrado",
                                tint = NexusNeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "REGISTRADO AUTOMÁTICAMENTE",
                                    color = NexusNeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${msg.registeredTitle}: ${CurrencyFormatter.format(msg.registeredAmount, currencySymbol)}",
                                    color = NexusPureWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = CurrencyFormatter.formatDateShort(msg.timestamp),
            color = NexusGray500,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

