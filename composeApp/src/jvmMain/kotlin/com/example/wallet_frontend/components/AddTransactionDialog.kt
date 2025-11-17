package com.example.wallet_frontend.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.models.Transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onSubmit: (Transaction) -> Unit
) {
    // --- State Variables ---
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var transactionType by remember { mutableStateOf("expense") }
    val transactionTypes = listOf("expense", "income")

    val categories = listOf(
        "Food",
        "Utilities",
        "Transportation",
        "Personal",
        "Family",
        "Entertainment",
        "Subscriptions",
        "Miscellaneous"
    )

    // --- Validation Error States ---
    var titleError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    // --- Date Picker State ---
    val dateFormatter = DateTimeFormatter.ISO_DATE
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val timestamp = selectedDate
        .atStartOfDay()
        .atOffset(java.time.ZoneOffset.UTC)
        .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)


    // Get the user ID from UserSession
    // Get the user ID from UserSession
    val currentUser = UserSession.currentUser.value
    val userId = currentUser?.userId

    // If no user is logged in, return early
    if (userId == null) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transaction") },
        text = {

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // --- Title ---
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = { Text("Title") },
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) Text(titleError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Amount (positive float only) ---
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = null
                    },
                    label = { Text("Amount") },
                    isError = amountError != null,
                    supportingText = {
                        if (amountError != null) Text(amountError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Category Dropdown ---
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        isError = categoryError != null,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                    categoryError = null
                                }
                            )
                        }
                    }
                }

                // Optional subcategory
                OutlinedTextField(
                    value = subCategory,
                    onValueChange = { subCategory = it },
                    label = { Text("Subcategory (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Transaction Type Dropdown ---
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = transactionType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        transactionTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    transactionType = option
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // --- Date Picker Button ---
                OutlinedTextField(
                    value = selectedDate.toString(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .clickable { showDatePicker = true }
                        .fillMaxWidth(),
                    label = { Text("Date") }
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        initialDate = selectedDate,
                        onDateSelected = {
                            selectedDate = it
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }

                // Optional fields:
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {

                    // --- Validate ---
                    var valid = true

                    if (title.isBlank()) {
                        titleError = "Title cannot be empty"
                        valid = false
                    }

                    if (amount.isBlank() || amount.toFloatOrNull() == null || amount.toFloat() <= 0) {
                        amountError = "Enter a valid positive number"
                        valid = false
                    }

                    if (category.isBlank()) {
                        categoryError = "Select a category"
                        valid = false
                    }

                    if (!valid) return@Button

                    // --- Submit ---
                    onSubmit(
                        Transaction(
                            transactionId = null,
                            userId = userId,
                            title = title,
                            category = category,
                            subCategory = subCategory.ifBlank { null },
                            transactionType = transactionType,
                            amount = amount,
                            date = timestamp,
                            description = description.ifBlank { null },
                            location = location.ifBlank { null }
                        )
                    )
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
