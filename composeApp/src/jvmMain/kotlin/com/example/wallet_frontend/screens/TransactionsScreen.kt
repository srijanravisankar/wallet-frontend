// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/screens/TransactionsScreen.kt

package com.example.wallet_frontend.screens

// --- NEW IMPORTS ---
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.components.AddTransactionDialog
import com.example.wallet_frontend.components.TransactionRow // Import our new row component
import com.example.wallet_frontend.models.Transaction // Import our new data class
import com.example.wallet_frontend.network.TransactionApi
import kotlinx.coroutines.launch

/**
 * This is the "smart" composable for the Transactions screen.
 * It will (eventually) hold the logic and state for this screen.
 * For now, it just displays a dummy layout.
 */
@OptIn(ExperimentalMaterial3Api::class) // We need this for the 'TopAppBar'
@Composable
fun TransactionsScreen() {

    // --- 1. Create Dummy Data ---
    // We'll create a hard-coded list of transactions to show.
    // This is our "dummy" data.
    val transactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val showDialog = remember { mutableStateOf(false) }

    // Get the user ID from UserSession
    // Get the user ID from UserSession
    val currentUser = UserSession.currentUser.value
    val userId = currentUser?.userId

    // If no user is logged in, return early
    if (userId == null) {
        return
    }

    LaunchedEffect(Unit) {
        try {
            val result = TransactionApi.getTransactions(userId = userId)
            transactions.value = result
        } catch (e: Exception) {
            println("Error fetching transactions: ${e.message}")
        }
    }

    // --- 2. Use 'Scaffold' for a modern layout ---
    // 'Scaffold' gives us slots for a top bar, bottom bar, and
    // a Floating Action Button (FAB).
    Scaffold(
        // --- 3. The Top Title Bar ---
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                // We'll give it a clean, white background
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },

        // --- 4. The "+" Button ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog.value = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }

    ) { paddingValues ->
        // This 'paddingValues' comes from the Scaffold. It tells us
        // how much space the top bar is using, so our list
        // doesn't hide underneath it.

        // --- 5. The Scrollable List ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply the padding from the Scaffold

            // Add some padding around the whole list
            contentPadding = PaddingValues(16.dp),
            // Add space *between* each item in the list
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // This 'items' block is like a 'forEach' loop
            // for our list of dummy transactions.
            items(transactions.value) { transaction ->
                // For each transaction, call our reusable component!
                TransactionRow(transaction = transaction)
            }
        }

        if (showDialog.value) {
            AddTransactionDialog(
                onDismiss = { showDialog.value = false },
                onSubmit = { newTransaction ->
                    showDialog.value = false

                    // Launch a coroutine to POST to backend
                    kotlinx.coroutines.GlobalScope.launch {
                        val success = TransactionApi.addTransaction(newTransaction)

                        if (success) {
                            // reload from backend
                            val freshList = TransactionApi.getTransactions(userId = userId)
                            transactions.value = freshList
                        } else {
                            println("Failed to add transaction")
                        }
                    }
                }
            )
        }
    }
}