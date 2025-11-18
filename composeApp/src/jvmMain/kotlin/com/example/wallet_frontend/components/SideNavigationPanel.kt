package com.example.wallet_frontend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SideNavigationPanel(
    currentSelectedScreen: String,
    onScreenSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(96.dp)
            .background(Color(0xFFFCEEEE))
            .padding(vertical = 16.dp),

        verticalArrangement = Arrangement.SpaceEvenly,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "My Wallet Logo"
            )
        }


        NavigationRailItem(
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Transactions") },
            label = { Text("Transactions") },
            selected = (currentSelectedScreen == "Transactions"),
            onClick = { onScreenSelected("Transactions") }
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Budgets") },
            label = { Text("Budgets") },
            selected = (currentSelectedScreen == "Budgets"),
            onClick = { onScreenSelected("Budgets") }
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.PieChart, contentDescription = "Pie Chart") },
            label = { Text("Pie Chart") },
            selected = (currentSelectedScreen == "Pie Chart"),
            onClick = { onScreenSelected("Pie Chart") }
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = (currentSelectedScreen == "Settings"),
            onClick = { onScreenSelected("Settings") }
        )
    }
}