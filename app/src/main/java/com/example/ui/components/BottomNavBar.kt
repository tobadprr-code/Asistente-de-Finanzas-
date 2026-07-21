package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorderLine
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.IndigoAiAccent
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryMuted

enum class NavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    DASHBOARD("Dashboard", Icons.Filled.PieChart, Icons.Outlined.PieChart),
    HISTORY("Historial", Icons.Filled.History, Icons.Outlined.History),
    CHAT("Asesor IA", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    SETTINGS("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun BottomNavBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurfaceCard)
            .navigationBarsPadding()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkBorderLine)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = destination == currentDestination
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) IndigoAiAccent else TextSecondaryMuted,
                        label = "color"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(destination) }
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.title,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = destination.title,
                            color = if (isSelected) TextPrimaryWhite else TextSecondaryMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
