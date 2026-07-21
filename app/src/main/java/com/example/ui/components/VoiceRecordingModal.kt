package com.example.ui.components

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkBorderLine
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.IndigoAiAccent
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryMuted
import java.util.Locale

@Composable
fun VoiceRecordingModal(
    onDismiss: () -> Unit,
    onSpeechCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(true) }
    var recognizedText by remember { mutableStateOf("Escuchando tu voz...") }
    var isRecognizerAvailable by remember { mutableStateOf(true) }

    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(isListening) {
        if (isListening) {
            scaleAnim.animateTo(
                targetValue = 1.25f,
                animationSpec = InfiniteRepeatableSpec(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            scaleAnim.snapTo(1f)
        }
    }

    DisposableEffect(Unit) {
        var speechRecognizer: SpeechRecognizer? = null
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "ES"))
                }
                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {
                        recognizedText = "Procesando voz..."
                    }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                    }
                    override fun onError(error: Int) {
                        isListening = false
                        recognizedText = "Hablá claro o escribí tu movimiento..."
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val text = matches[0]
                            recognizedText = text
                            onSpeechCaptured(text)
                            onDismiss()
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
                speechRecognizer.startListening(intent)
            } catch (e: Exception) {
                isRecognizerAvailable = false
            }
        } else {
            isRecognizerAvailable = false
        }

        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (e: Exception) {}
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderLine),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTRO POR VOZ",
                        color = TextSecondaryMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextSecondaryMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .scale(scaleAnim.value)
                            .clip(CircleShape)
                            .background(IndigoAiAccent.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(IndigoAiAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Micrófono",
                            tint = TextPrimaryWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = recognizedText,
                    color = TextPrimaryWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Presets buttons for instant testing if microphone is quiet
                Text(
                    text = "O probá con estos ejemplos:",
                    color = TextSecondaryMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onSpeechCaptured("Vendí un Gol por ocho millones por transferencia")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBorderLine),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Vendí Gol", fontSize = 11.sp, color = TextPrimaryWhite)
                    }
                    Button(
                        onClick = {
                            onSpeechCaptured("Gasté 45 mil en el supermercado con tarjeta")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBorderLine),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Súper 45k", fontSize = 11.sp, color = TextPrimaryWhite)
                    }
                }
            }
        }
    }
}
