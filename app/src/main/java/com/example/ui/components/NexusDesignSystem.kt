package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * NEXUS Official Design System Reusable Components
 */

enum class NexusButtonVariant {
    PRIMARY,   // Neon Green background, Black text, Arrow
    SECONDARY, // Dark background #161616, White text
    GHOST,     // Transparent, subtle border
    OUTLINED   // Border only
}

@Composable
fun NexusCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NexusBlackSecondary,
    borderColor: Color = NexusBorderDark,
    hasGlow: Boolean = false,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var cardModifier = modifier
        .clip(RoundedCornerShape(cornerRadius))
        .background(backgroundColor)
        .border(
            width = 1.dp,
            color = if (hasGlow) NexusNeonGreen.copy(alpha = 0.5f) else borderColor,
            shape = RoundedCornerShape(cornerRadius)
        )

    if (hasGlow) {
        cardModifier = cardModifier.shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(cornerRadius),
            spotColor = NexusNeonGreen,
            ambientColor = NexusNeonGreen
        )
    }

    if (onClick != null) {
        cardModifier = cardModifier.clickable { onClick() }
    }

    Column(
        modifier = cardModifier.padding(16.dp),
        content = content
    )
}

@Composable
fun NexusButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: NexusButtonVariant = NexusButtonVariant.PRIMARY,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    val backgroundColor = when (variant) {
        NexusButtonVariant.PRIMARY -> if (enabled) NexusNeonGreen else NexusGray700
        NexusButtonVariant.SECONDARY -> NexusBlackSecondary
        NexusButtonVariant.GHOST -> Color.Transparent
        NexusButtonVariant.OUTLINED -> Color.Transparent
    }

    val textColor = when (variant) {
        NexusButtonVariant.PRIMARY -> NexusBlackPrimary
        NexusButtonVariant.SECONDARY -> NexusPureWhite
        NexusButtonVariant.GHOST -> NexusPureWhite
        NexusButtonVariant.OUTLINED -> NexusPureWhite
    }

    val borderColor = when (variant) {
        NexusButtonVariant.PRIMARY -> Color.Transparent
        NexusButtonVariant.SECONDARY -> NexusBorderSubtle
        NexusButtonVariant.GHOST -> Color.Transparent
        NexusButtonVariant.OUTLINED -> NexusBorderSubtle
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                icon()
            } else if (variant == NexusButtonVariant.PRIMARY) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = NexusBlackPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun NexusBadge(
    text: String,
    modifier: Modifier = Modifier,
    isNeon: Boolean = true,
    backgroundColor: Color? = null,
    textColor: Color? = null
) {
    val bg = backgroundColor ?: if (isNeon) NexusNeonGreen else NexusBlackSecondary
    val txt = textColor ?: if (isNeon) NexusBlackPrimary else NexusGray300

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = txt,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NexusInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        textStyle = LocalTextStyle.current.copy(
            color = NexusPureWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        cursorBrush = SolidColor(NexusNeonGreen),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NexusInputBackground)
            .border(1.dp, NexusBorderSubtle, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = NexusGray500,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun NexusStatCard(
    title: String,
    value: String,
    trendText: String? = null,
    isPositiveTrend: Boolean = true,
    modifier: Modifier = Modifier
) {
    NexusCard(modifier = modifier) {
        Text(
            text = title,
            color = NexusGray300,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = NexusPureWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        if (!trendText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isPositiveTrend) "↗ $trendText" else "↘ $trendText",
                    color = if (isPositiveTrend) NexusNeonGreen else NexusExpenseRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NexusTopBar(
    userName: String = "Martín",
    notificationCount: Int = 3,
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NexusBlackPrimary)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(NexusBlackSecondary)
                    .border(1.dp, NexusNeonGreen.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "X",
                    color = NexusNeonGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "NEXUS",
                color = NexusPureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
        }

        // Profile & Notification Action
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NexusBlackSecondary)
                    .clickable { onNotificationClick() }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = NexusPureWhite,
                    modifier = Modifier.size(18.dp)
                )
                if (notificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(NexusNeonGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notificationCount.toString(),
                            color = NexusBlackPrimary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(NexusBlackSecondary)
                    .border(1.dp, NexusBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(NexusNeonGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        color = NexusBlackPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = userName,
                    color = NexusPureWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
