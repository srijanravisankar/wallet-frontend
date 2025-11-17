//package com.example.wallet_frontend
//
//// --- IMPORTS ---
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.wallet_frontend.components.SideNavigationPanel // Our component import
//import com.example.wallet_frontend.screens.BarChartScreen
//import com.example.wallet_frontend.screens.BudgetsScreen
//import com.example.wallet_frontend.screens.PieChartScreen
//import com.example.wallet_frontend.screens.SettingsScreen
//import com.example.wallet_frontend.screens.TransactionsScreen
//
///**
// * This is our main application Composable (the "smart" one).
// * It holds the "memory" and decides what to show.
// */
//@Composable
//fun App() {
//
//    // --- 1. THE "MEMORY" ---
//    // This "remembers" the currently selected screen.
//    // The default value is "Transactions", which is still perfect.
//    var selectedScreen by remember { mutableStateOf("Transactions") }
//
//    // 'MaterialTheme' wrapper, just like before.
//    MaterialTheme {
//
//        // --- 2. THE MAIN LAYOUT ---
//        // 'Row' layout, just like before.
//        Row(modifier = Modifier.fillMaxSize()) {
//
//            // --- 3. THE NAVIGATION PANEL (Left Side) ---
//            //
//            // THE BEST PART:
//            // Because we built a reusable component, we don't need
//            // to change this code *at all*. It already works.
//            //
//            // It passes the 'selectedScreen' DOWN...
//            // and it gets the new screen name ("Pie Chart", etc.) UP
//            // from the 'onScreenSelected' callback.
//            SideNavigationPanel(
//                currentSelectedScreen = selectedScreen,
//                onScreenSelected = { screenName ->
//                    // This 'screenName' variable will now be "Pie Chart",
//                    // "Bar Chart", etc. when you click them.
//                    selectedScreen = screenName
//                }
//            )
//
//            // --- 4. THE CONTENT AREA (Right Side) ---
//            // This is the same Box as before.
//            Box(
//                modifier = Modifier
//                    .weight(1f) // Fill the remaining space
//                    .padding(16.dp)
//            ) {
//
//                // --- 5. THE "SCREEN SWITCHER" (UPDATED) ---
//                // We *must* update this 'when' statement to
//                // handle the new screen names we added.
//                when (selectedScreen) {
//                    "Transactions" -> {
//                        TransactionsScreen()
//                    }
//                    "Budgets" -> {
//                        BudgetsScreen()
//                    }
//                    // --- NEW CASE ---
//                    "Pie Chart" -> {
//                        PieChartScreen()
//                    }
//                    // --- NEW CASE ---
//                    "Bar Chart" -> {
//                        BarChartScreen()
//                    }
//                    // --- NEW CASE ---
//                    "Insights" -> {
//                        Text(
//                            text = "This is the Insights Screen",
//                            style = MaterialTheme.typography.headlineMedium
//                        )
//                    }
//                    "Settings" -> {
//                        SettingsScreen()
//                    }
//                } // End of our 'when' statement
//            } // End of our 'Box' (the content area)
//        } // End of our 'Row' (the main layout)
//    } // End of our 'MaterialTheme'
//}


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

    // Check if user is logged in
    val isLoggedIn by UserSession.currentUser

    MaterialTheme {
        if (isLoggedIn == null) {
            // Show login screen if not logged in
            LoginScreen(
                onLoginSuccess = {
                    // This will trigger recomposition and show the main app
                }
            )
        } else {
            // Show main app if logged in
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