package com.example.wallet_frontend.components

// --- IMPORTS ---
// We need to import 'Arrangement' and 'Column'
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
// We are NO LONGER using NavigationRail
import androidx.compose.material3.NavigationRailItem // We ARE still using this
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * This is our upgraded, professional navigation panel.
 *
 * --- WHAT'S NEW ---
 * We are replacing 'NavigationRail' with a standard 'Column'.
 * This lets us use 'verticalArrangement = Arrangement.SpaceEvenly'
 * to automatically spread all our buttons out perfectly.
 *
 * @param currentSelectedScreen The name of the screen that is currently active.
 * @param onScreenSelected The "callback function" that we call when an item is clicked.
 */
@Composable
fun SideNavigationPanel(
    currentSelectedScreen: String,
    onScreenSelected: (String) -> Unit
) {
    // --- 1. We replaced 'NavigationRail' with 'Column' ---
    Column(
        modifier = Modifier
            .fillMaxHeight() // Fill the full height
            .width(96.dp) // A good width for an icon-based panel
            .background(Color(0xFFFCEEEE)) // Our light pink color
            .padding(vertical = 16.dp), // Give some space at the very top and bottom

        // --- 2. THIS IS THE MAGIC ---
        // This tells the Column to take all its children and
        // arrange them vertically, dividing all the empty space
        // *evenly* between them.
        verticalArrangement = Arrangement.SpaceEvenly,

        // This centers all the children (our buttons) in the middle
        // of the 96.dp-wide column.
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- 3. "My Wallet" Logo Header ---
        // This is now the *first* item in our evenly-spaced list.
        Column(
            // We don't need padding here anymore, Arrangement.SpaceEvenly handles it.
            horizontalAlignment = Alignment.CenterHorizontally // Center the icon/text
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "My Wallet Logo"
            )
//            Text("My Wallet", style = MaterialTheme.typography.labelSmall)
        }

        // --- 4. Navigation Items ---
        // These are the rest of our evenly-spaced items.
        // We can still use 'NavigationRailItem' because it's a
        // great component for an icon + label.

        // --- Transactions Item ---
        NavigationRailItem(
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Transactions") },
            label = { Text("Transactions") },
            selected = (currentSelectedScreen == "Transactions"),
            onClick = { onScreenSelected("Transactions") }
        )

        // --- Budgets Item ---
        NavigationRailItem(
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Budgets") },
            label = { Text("Budgets") },
            selected = (currentSelectedScreen == "Budgets"),
            onClick = { onScreenSelected("Budgets") }
        )

        // --- Pie Chart Item ---
        NavigationRailItem(
            icon = { Icon(Icons.Default.PieChart, contentDescription = "Pie Chart") },
            label = { Text("Pie Chart") },
            selected = (currentSelectedScreen == "Pie Chart"),
            onClick = { onScreenSelected("Pie Chart") }
        )

        // --- Bar Chart Item ---
        NavigationRailItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Bar Chart") },
            label = { Text("Bar Chart") },
            selected = (currentSelectedScreen == "Bar Chart"),
            onClick = { onScreenSelected("Bar Chart") }
        )

        // --- Insights Item ---
//        NavigationRailItem(
//            icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
//            label = { Text("Insights") },
//            selected = (currentSelectedScreen == "Insights"),
//            onClick = { onScreenSelected("Insights") }
//        )

        // --- 5. WE REMOVED THE SPACER ---
        // We don't need 'Spacer(Modifier.weight(1f))' anymore
        // because 'Arrangement.SpaceEvenly' does all the work!

        // --- Settings Item ---
        // This is now the *last* item in our evenly-spaced list.
        NavigationRailItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = (currentSelectedScreen == "Settings"),
            onClick = { onScreenSelected("Settings") }
        )
    } // End of our Column
}