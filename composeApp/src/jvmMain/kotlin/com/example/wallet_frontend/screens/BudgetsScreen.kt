// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/screens/BudgetsScreen.kt

package com.example.wallet_frontend.screens

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.components.AddBudgetDialog
import com.example.wallet_frontend.components.BudgetCard // Import our new card component
import com.example.wallet_frontend.models.Budget // Import our new data class
import com.example.wallet_frontend.models.Transaction
import com.example.wallet_frontend.models.User
import com.example.wallet_frontend.network.BudgetApi
import com.example.wallet_frontend.network.TransactionApi
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen() {

    val budgets = remember { mutableStateOf<List<Budget>>(emptyList()) }
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

    // Fetch both budgets and transactions
    LaunchedEffect(Unit) {
        try {
            val budgetResult = BudgetApi.getBudgets(userId = userId)
            budgets.value = budgetResult

            val transactionResult = TransactionApi.getTransactions(userId = userId)
            transactions.value = transactionResult
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
        }
    }

    // Calculate spending per category from transactions
    val spendingByCategory = remember(transactions.value, budgets.value) {
        transactions.value
            .filter { it.transactionType == "expense" } // Only expenses
            .filter { transaction ->
                // Filter transactions within budget period
                // You'll need to parse dates and check if transaction.date
                // falls within each budget's startDate and endDate
                budgets.value.any { budget ->
                    budget.category == transaction.category
                    // TODO: Add date range check here
                }
            }
            .groupBy { it.category }
            .mapValues { (_, transactionsInCategory) ->
                transactionsInCategory.sumOf {
                    BigDecimal(it.amount)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Budgets") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog.value = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(budgets.value) { budget ->
                // Get the actual spending for this budget's category
                val spent = spendingByCategory[budget.category] ?: BigDecimal.ZERO

                BudgetCard(budget = budget, currentSpent = spent)
            }
        }

        if (showDialog.value) {
            AddBudgetDialog (
                onDismiss = { showDialog.value = false },
                onSubmit = { newTransaction ->
                    showDialog.value = false

                    // Launch a coroutine to POST to backend
                    kotlinx.coroutines.GlobalScope.launch {
                        val success = BudgetApi.addBudget(newTransaction)

                        if (success) {
                            // reload from backend
                            val freshList = BudgetApi.getBudgets(userId = userId)
                            budgets.value = freshList
                        } else {
                            println("Failed to add transaction")
                        }
                    }
                }
            )
        }
    }
}