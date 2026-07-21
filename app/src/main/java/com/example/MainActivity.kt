package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavDestination
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ValorTheme
import com.example.ui.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ValorTheme {
                var currentDestination by remember { mutableStateOf(NavDestination.HOME) }

                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            currentDestination = currentDestination,
                            onNavigate = { destination ->
                                currentDestination = destination
                            }
                        )
                    },
                    containerColor = ObsidianBackground,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ObsidianBackground)
                            .padding(innerPadding)
                    ) {
                        Crossfade(
                            targetState = currentDestination,
                            label = "ScreenTransition"
                        ) { destination ->
                            when (destination) {
                                NavDestination.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToChat = { currentDestination = NavDestination.CHAT }
                                )
                                NavDestination.DASHBOARD -> DashboardScreen(
                                    viewModel = viewModel
                                )
                                NavDestination.HISTORY -> HistoryScreen(
                                    viewModel = viewModel
                                )
                                NavDestination.CHAT -> ChatScreen(
                                    viewModel = viewModel
                                )
                                NavDestination.SETTINGS -> SettingsScreen(
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
