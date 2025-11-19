package com.example.wallet_frontend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.network.BudgetApi
import com.example.wallet_frontend.network.TransactionApi
import kotlinx.coroutines.launch
import com.example.wallet_frontend.components.DestructiveSettingRow
import com.example.wallet_frontend.components.SettingSectionHeader
import com.example.wallet_frontend.components.SettingRow
import com.example.wallet_frontend.network.UserApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    val currentUser by UserSession.currentUser
    val userId = currentUser?.userId

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteTransactionsDialog by remember { mutableStateOf(false) }
    var showDeleteBudgetsDialog by remember { mutableStateOf(false) }
    var showUpdateProfileDialog by remember { mutableStateOf(false) }
    var showUpdatePasswordDialog by remember { mutableStateOf(false) }


    var isDeleting by remember { mutableStateOf(false) }
    var deleteMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = {
            if (deleteMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { deleteMessage = null }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(deleteMessage!!)
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            item {
                SettingSectionHeader(title = "PROFILE")
            }
            item {
                SettingRow(
                    title = "First Name",
                    subtitle = currentUser?.firstName ?: "Not available"
                ) { /* No action needed */ }
            }
            item {
                SettingRow(
                    title = "Last Name",
                    subtitle = currentUser?.lastName ?: "Not available"
                ) { /* No action needed */ }
            }
            item {
                SettingRow(
                    title = "Email",
                    subtitle = currentUser?.email ?: "Not available"
                ) { /* No action needed */ }
            }
            item {
                Button(
                    onClick = { showUpdateProfileDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Update Profile")
                }
            }
            item {
                Button(
                    onClick = { showUpdatePasswordDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Change Password")
                }
            }

            item {
                SettingRow(
                    title = "User ID",
                    subtitle = currentUser?.userId?.toString() ?: "Not available"
                ) { /* No action needed */ }
            }

            item {
                SettingSectionHeader(title = "ACCOUNT")
            }
            item {
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Sign Out")
                }
            }

            item {
                SettingSectionHeader(title = "DATA MANAGEMENT")
            }
            item {
                DestructiveSettingRow(
                    title = "Delete All Transactions",
                    enabled = !isDeleting
                ) {
                    showDeleteTransactionsDialog = true
                }
            }
            item {
                DestructiveSettingRow(
                    title = "Delete All Budgets",
                    enabled = !isDeleting
                ) {
                    showDeleteBudgetsDialog = true
                }
            }

            item {
                SettingSectionHeader(title = "ABOUT")
            }
            item {
                SettingRow(
                    title = "App Version",
                    subtitle = "1.0.0 (MVP)"
                ) { /* No action needed */ }
            }
            item {
                SettingRow(
                    title = "Account Created",
                    subtitle = currentUser?.createdAt ?: "Not available"
                ) { /* No action needed */ }
            }
        }
    }

    if (showUpdateProfileDialog && currentUser != null) {
        var first by remember { mutableStateOf(currentUser!!.firstName) }
        var last by remember { mutableStateOf(currentUser!!.lastName) }
        var email by remember { mutableStateOf(currentUser!!.email) }

        AlertDialog(
            onDismissRequest = { showUpdateProfileDialog = false },
            title = { Text("Update Profile") },
            text = {
                Column {
                    OutlinedTextField(value = first, onValueChange = { first = it }, label = { Text("First Name") })
                    OutlinedTextField(value = last, onValueChange = { last = it }, label = { Text("Last Name") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        UserApi.updateUser(
                            userId!!,
                            first,
                            last,
                            email
                        )
                        // update local session
                        UserSession.updateUser(first, last, email)
                        showUpdateProfileDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showUpdatePasswordDialog) {

        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showUpdatePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
                    OutlinedTextField(value = oldPass, onValueChange = { oldPass = it }, label = { Text("Old Password") })
                    OutlinedTextField(value = newPass, onValueChange = { newPass = it }, label = { Text("New Password") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val success = UserApi.updatePassword(userId!!, oldPass, newPass)
                        deleteMessage = if (success) "Password Updated" else "Incorrect Password"
                        showUpdatePasswordDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showUpdatePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }



    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                Button(
                    onClick = {
                        UserSession.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteTransactionsDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteTransactionsDialog = false },
            title = { Text("Delete All Transactions") },
            text = {
                Text("This will permanently delete all your transactions. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userId != null) {
                            isDeleting = true
                            showDeleteTransactionsDialog = false

                            scope.launch {
                                try {
                                    // Fetch all transactions
                                    val transactions = TransactionApi.getTransactions(userId)
                                    var deletedCount = 0
                                    var failedCount = 0

                                    // Delete each transaction
                                    transactions.forEach { transaction ->
                                        transaction.transactionId?.let { id ->
                                            val success = TransactionApi.deleteTransaction(id)
                                            if (success) deletedCount++ else failedCount++
                                        }
                                    }

                                    deleteMessage = if (failedCount == 0) {
                                        "Successfully deleted $deletedCount transactions"
                                    } else {
                                        "Deleted $deletedCount transactions, $failedCount failed"
                                    }
                                } catch (e: Exception) {
                                    deleteMessage = "Error deleting transactions: ${e.message}"
                                    println("Error: ${e.message}")
                                } finally {
                                    isDeleting = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        Text("Delete All")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteTransactionsDialog = false },
                    enabled = !isDeleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteBudgetsDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteBudgetsDialog = false },
            title = { Text("Delete All Budgets") },
            text = {
                Text("This will permanently delete all your budgets. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userId != null) {
                            isDeleting = true
                            showDeleteBudgetsDialog = false

                            scope.launch {
                                try {
                                    // Fetch all budgets
                                    val budgets = BudgetApi.getBudgets(userId)
                                    var deletedCount = 0
                                    var failedCount = 0

                                    // Delete each budget
                                    budgets.forEach { budget ->
                                        budget.budgetId?.let { id ->
                                            val success = BudgetApi.deleteBudget(id)
                                            if (success) deletedCount++ else failedCount++
                                        }
                                    }

                                    deleteMessage = if (failedCount == 0) {
                                        "Successfully deleted $deletedCount budgets"
                                    } else {
                                        "Deleted $deletedCount budgets, $failedCount failed"
                                    }
                                } catch (e: Exception) {
                                    deleteMessage = "Error deleting budgets: ${e.message}"
                                    println("Error: ${e.message}")
                                } finally {
                                    isDeleting = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        Text("Delete All")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteBudgetsDialog = false },
                    enabled = !isDeleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}