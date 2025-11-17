package com.example.wallet_frontend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession

// Import the reusable components we just created
import com.example.wallet_frontend.components.DestructiveSettingRow
import com.example.wallet_frontend.components.SettingSectionHeader
import com.example.wallet_frontend.components.SettingRow
import com.example.wallet_frontend.components.SwitchSettingRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    // --- State ---
    var isDarkMode by remember { mutableStateOf(false) }

    // Get the current logged-in user
    val currentUser by UserSession.currentUser

    // Show confirmation dialog for logout
    var showLogoutDialog by remember { mutableStateOf(false) }

    // --- Layout ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- 1. Profile Section ---
            item {
                SettingSectionHeader(title = "PROFILE")
            }
            item {
                SettingRow(
                    title = "First Name",
                    subtitle = currentUser?.firstName ?: "Not available"
                ) { /* TODO: Show edit dialog */ }
            }
            item {
                SettingRow(
                    title = "Last Name",
                    subtitle = currentUser?.lastName ?: "Not available"
                ) { /* TODO: Show edit dialog */ }
            }
            item {
                SettingRow(
                    title = "Email",
                    subtitle = currentUser?.email ?: "Not available"
                ) { /* TODO: Show edit dialog */ }
            }
            item {
                SettingRow(
                    title = "User ID",
                    subtitle = currentUser?.userId?.toString() ?: "Not available"
                ) { /* No action needed */ }
            }
            item {
                SettingRow(
                    title = "Change Password",
                    subtitle = "********"
                ) { /* TODO: Show password change dialog */ }
            }

            // --- 2. Account Section ---
            item {
                SettingSectionHeader(title = "ACCOUNT")
            }
            item {
                // Sign Out Button
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

            // --- 3. Appearance Section ---
            item {
                SettingSectionHeader(title = "APPEARANCE")
            }
            item {
                SwitchSettingRow(
                    title = "Dark Mode",
                    subtitle = if (isDarkMode) "Enabled" else "Disabled",
                    isChecked = isDarkMode,
                    onCheckedChange = { isDarkMode = it }
                )
            }

            // --- 4. Data Management Section ---
            item {
                SettingSectionHeader(title = "DATA MANAGEMENT")
            }
            item {
                DestructiveSettingRow(title = "Delete All Transactions") {
                    /* TODO: Show confirmation dialog */
                }
            }
            item {
                DestructiveSettingRow(title = "Delete All Budgets") {
                    /* TODO: Show confirmation dialog */
                }
            }
            item {
                DestructiveSettingRow(title = "Delete Account") {
                    /* TODO: Show final confirmation dialog */
                }
            }

            // --- 5. About Section ---
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

    // --- Logout Confirmation Dialog ---
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
}