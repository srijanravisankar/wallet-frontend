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
import com.example.wallet_frontend.components.AddTransactionDialog
import com.example.wallet_frontend.components.TransactionRow
import com.example.wallet_frontend.models.Transaction
import com.example.wallet_frontend.network.TransactionApi
import kotlinx.coroutines.launch
import com.example.wallet_frontend.components.EditTransactionDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen() {

    val transactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val showDialog = remember { mutableStateOf(false) }
    val selectedTransaction = remember { mutableStateOf<Transaction?>(null) }


    val currentUser = UserSession.currentUser.value
    val userId = currentUser?.userId

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

    Scaffold(
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

        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog.value = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions.value) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    onClick = {
                        selectedTransaction.value = transaction
                    }
                )
            }
        }

        if (showDialog.value) {
            AddTransactionDialog(
                onDismiss = { showDialog.value = false },
                onSubmit = { newTransaction ->
                    showDialog.value = false

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

        // Edit Transaction Dialog
        selectedTransaction.value?.let { tx ->
            EditTransactionDialog(
                transaction = tx,
                onDismiss = { selectedTransaction.value = null },
                onSubmit = { updated ->
                    kotlinx.coroutines.GlobalScope.launch {
                        val success = TransactionApi.updateTransaction(
                            id = updated.transactionId!!,
                            updated = updated
                        )

                        if (success) {
                            transactions.value = TransactionApi.getTransactions(userId)
                        }

                        selectedTransaction.value = null
                    }
                },
                onDelete = {
                    kotlinx.coroutines.GlobalScope.launch {
                        tx.transactionId?.let { id ->
                            TransactionApi.deleteTransaction(id)
                        }
                        transactions.value = TransactionApi.getTransactions(userId)
                        selectedTransaction.value = null
                    }
                }
            )
        }

    }
}