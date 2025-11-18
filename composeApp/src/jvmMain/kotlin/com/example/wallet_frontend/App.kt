package com.example.wallet_frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.components.SideNavigationPanel
import com.example.wallet_frontend.screens.*

@Composable
fun App() {
    var selectedScreen by remember { mutableStateOf("Transactions") }

    val isLoggedIn by UserSession.currentUser

    MaterialTheme {
        if (isLoggedIn == null) {
            LoginScreen(
                onLoginSuccess = {
                }
            )
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                SideNavigationPanel(
                    currentSelectedScreen = selectedScreen,
                    onScreenSelected = { screenName ->
                        selectedScreen = screenName
                    }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    when (selectedScreen) {
                        "Transactions" -> TransactionsScreen()
                        "Budgets" -> BudgetsScreen()
                        "Pie Chart" -> PieChartScreen()
                        "Bar Chart" -> BarChartScreen()
                        "Insights" -> {
                            Text(
                                text = "This is the Insights Screen",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        "Settings" -> SettingsScreen()
                    }
                }
            }
        }
    }
}