package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class NavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    DASHBOARD("Dashboard", Icons.Filled.PieChart, Icons.Outlined.PieChart),
    ASSETS("Activos", Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar),
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
            .background(NexusBlackPrimary)
            .navigationBarsPadding()
            .testTag("bottom_nav_bar")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(NexusBorderSubtle)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = destination == currentDestination
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) NexusNeonGreen else NexusGray500,
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
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .testTag("nav_tab_${destination.name.lowercase()}")
                    ) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.title,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = destination.title,
                            color = if (isSelected) NexusPureWhite else NexusGray500,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

