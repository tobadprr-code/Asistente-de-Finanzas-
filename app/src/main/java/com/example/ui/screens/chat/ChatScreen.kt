package com.example.ui.screens.chat

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.TransactionType
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
fun ChatScreen(
    viewModel: FinanceViewModel
) {
    val messages by viewModel.chatMessages.collectAsState()
    val currencySymbol by viewModel.userCurrency.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var showVoiceModal by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val quickPrompts = listOf(
        "Vendí un Gol por 8 millones",
        "¿Cuánto gasté este mes?",
        "¿Cómo viene mi economía?",
        "Gasté 45 mil en la farmacia"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceCard)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(IndigoAiAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = CyanAiAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ASESOR FINANCIERO IA",
                    color = TextPrimaryWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Respuestas en tiempo real con datos reales",
                    color = TextSecondaryMuted,
                    fontSize = 11.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkBorderLine)
        )

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            items(messages) { msg ->
                ChatMessageBubble(msg = msg, currencySymbol = currencySymbol)
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
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceCard)
                        .border(1.dp, DarkBorderLine, RoundedCornerShape(12.dp))
                        .clickable { viewModel.sendChatMessage(prompt) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = prompt,
                        color = TextPrimaryWhite,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceCard)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showVoiceModal = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(IndigoAiAccent.copy(alpha = 0.2f))
                    .size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Grabar voz",
                    tint = IndigoAiAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Escribí o hablá un movimiento...",
                        color = TextSecondaryMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoAiAccent,
                    unfocusedBorderColor = DarkBorderLine,
                    focusedTextColor = TextPrimaryWhite,
                    unfocusedTextColor = TextPrimaryWhite,
                    focusedContainerColor = DarkBorderLine.copy(alpha = 0.2f),
                    unfocusedContainerColor = DarkBorderLine.copy(alpha = 0.2f)
                )
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
                    .background(IndigoAiAccent)
                    .size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = TextPrimaryWhite,
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
                .background(if (isUser) IndigoAiAccent else DarkSurfaceCard)
                .border(
                    width = 1.dp,
                    color = if (isUser) IndigoAiAccent else DarkBorderLine,
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
                    color = TextPrimaryWhite,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                if (msg.isAutoRegisteredTx && msg.registeredTitle != null && msg.registeredAmount != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBorderLine.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Registrado",
                                tint = EmeraldIncome,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "REGISTRADO AUTOMÁTICAMENTE",
                                    color = EmeraldIncome,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${msg.registeredTitle}: ${CurrencyFormatter.format(msg.registeredAmount, currencySymbol)}",
                                    color = TextPrimaryWhite,
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
            color = TextSecondaryMuted,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
