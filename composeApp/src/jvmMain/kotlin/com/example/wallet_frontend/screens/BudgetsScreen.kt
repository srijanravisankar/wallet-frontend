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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.components.AddBudgetDialog
import com.example.wallet_frontend.components.BudgetCard
import com.example.wallet_frontend.components.EditBudgetDialog
import com.example.wallet_frontend.models.Budget
import com.example.wallet_frontend.models.Transaction
import com.example.wallet_frontend.network.BudgetApi
import com.example.wallet_frontend.network.TransactionApi
import kotlinx.coroutines.launch
import java.math.BigDecimal
import androidx.compose.foundation.clickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen() {

    val budgets = remember { mutableStateOf<List<Budget>>(emptyList()) }
    val transactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val showDialog = remember { mutableStateOf(false) }
    val selectedBudget = remember { mutableStateOf<Budget?>(null) }


    val currentUser = UserSession.currentUser.value
    val userId = currentUser?.userId

    if (userId == null) {
        return
    }

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

    val spendingByCategory = remember(transactions.value, budgets.value) {
        transactions.value
            .filter { it.transactionType == "expense" }
            .filter { transaction ->
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
                val spent = spendingByCategory[budget.category] ?: BigDecimal.ZERO

                BudgetCard(
                    budget = budget,
                    currentSpent = spent,
                    modifier = Modifier.clickable {
                        selectedBudget.value = budget
                    }
                )
            }

        }

        if (showDialog.value) {
            AddBudgetDialog (
                onDismiss = { showDialog.value = false },
                onSubmit = { newTransaction ->
                    showDialog.value = false

                    kotlinx.coroutines.GlobalScope.launch {
                        val success = BudgetApi.addBudget(newTransaction)

                        if (success) {
                            val freshList = BudgetApi.getBudgets(userId = userId)
                            budgets.value = freshList
                        } else {
                            println("Failed to add transaction")
                        }
                    }
                }
            )
        }

        selectedBudget.value?.let { b ->
            EditBudgetDialog(
                budget = b,
                onDismiss = { selectedBudget.value = null },
                onSubmit = { updated ->
                    kotlinx.coroutines.GlobalScope.launch {
                        val success = BudgetApi.updateBudget(b.budgetId!!, updated)
                        if (success) {
                            budgets.value = BudgetApi.getBudgets(userId)
                        }
                        selectedBudget.value = null
                    }
                },
                onDelete = {
                    kotlinx.coroutines.GlobalScope.launch {
                        b.budgetId?.let { id -> BudgetApi.deleteBudget(id) }
                        budgets.value = BudgetApi.getBudgets(userId)
                        selectedBudget.value = null
                    }
                }
            )
        }

    }
}