package com.example.wallet_frontend.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.models.Budget
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onSubmit: (Budget) -> Unit
) {
    // --- State Variables ---
    var budgetLimit by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var periodType by remember { mutableStateOf("monthly") }

    val periodTypes = listOf("daily", "weekly", "monthly", "yearly")

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
    var budgetLimitError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    // --- Date Picker State ---
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusMonths(1)) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDateTimestamp = startDate
        .atStartOfDay()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    val endDateTimestamp = endDate
        .atStartOfDay()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

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
        title = { Text("Add Budget") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

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
                        supportingText = {
                            if (categoryError != null) Text(categoryError!!, color = MaterialTheme.colorScheme.error)
                        },
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

                // --- Optional Subcategory ---
                OutlinedTextField(
                    value = subCategory,
                    onValueChange = { subCategory = it },
                    label = { Text("Subcategory (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Budget Limit (positive float only) ---
                OutlinedTextField(
                    value = budgetLimit,
                    onValueChange = {
                        budgetLimit = it
                        budgetLimitError = null
                    },
                    label = { Text("Budget Limit") },
                    isError = budgetLimitError != null,
                    supportingText = {
                        if (budgetLimitError != null) Text(budgetLimitError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Period Type Dropdown ---
                var periodTypeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = periodTypeExpanded,
                    onExpandedChange = { periodTypeExpanded = !periodTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = periodType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Period Type") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = periodTypeExpanded,
                        onDismissRequest = { periodTypeExpanded = false }
                    ) {
                        periodTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    periodType = option
                                    periodTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                // --- Start Date Picker ---
                OutlinedTextField(
                    value = startDate.toString(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .clickable { showStartDatePicker = true }
                        .fillMaxWidth(),
                    label = { Text("Start Date") }
                )

                if (showStartDatePicker) {
                    DatePickerDialog(
                        initialDate = startDate,
                        onDateSelected = {
                            startDate = it
                            showStartDatePicker = false
                        },
                        onDismiss = { showStartDatePicker = false }
                    )
                }

                // --- End Date Picker ---
                OutlinedTextField(
                    value = endDate.toString(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .clickable { showEndDatePicker = true }
                        .fillMaxWidth(),
                    label = { Text("End Date") }
                )

                if (showEndDatePicker) {
                    DatePickerDialog(
                        initialDate = endDate,
                        onDateSelected = {
                            endDate = it
                            showEndDatePicker = false
                        },
                        onDismiss = { showEndDatePicker = false }
                    )
                }

                // --- Optional Description ---
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // --- Validate ---
                    var valid = true

                    if (category.isBlank()) {
                        categoryError = "Select a category"
                        valid = false
                    }

                    if (budgetLimit.isBlank() || budgetLimit.toFloatOrNull() == null || budgetLimit.toFloat() <= 0) {
                        budgetLimitError = "Enter a valid positive number"
                        valid = false
                    }

                    if (!valid) return@Button

                    // --- Submit ---
                    onSubmit(
                        Budget(
                            budgetId = null,
                            userId = userId,
                            category = category,
                            subCategory = subCategory.ifBlank { null },
                            budgetLimit = budgetLimit,
                            periodType = periodType,
                            startDate = startDateTimestamp,
                            endDate = endDateTimestamp,
                            description = description.ifBlank { null }
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