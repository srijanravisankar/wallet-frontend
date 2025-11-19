package com.example.wallet_frontend.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.models.Budget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetDialog(
    budget: Budget,
    onDismiss: () -> Unit,
    onSubmit: (Budget) -> Unit,
    onDelete: () -> Unit
) {
    var category by remember { mutableStateOf(budget.category) }
    var subCategory by remember { mutableStateOf(budget.subCategory ?: "") }
    var budgetLimit by remember { mutableStateOf(budget.budgetLimit) }
    var description by remember { mutableStateOf(budget.description ?: "") }

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

    var categoryExpanded by remember { mutableStateOf(false) }

    var budgetLimitError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Budget") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // CATEGORY DROPDOWN
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
                            if (categoryError != null)
                                Text(categoryError!!, color = MaterialTheme.colorScheme.error)
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

                // SUBCATEGORY
                OutlinedTextField(
                    value = subCategory,
                    onValueChange = { subCategory = it },
                    label = { Text("Subcategory (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // BUDGET LIMIT
                OutlinedTextField(
                    value = budgetLimit,
                    onValueChange = {
                        budgetLimit = it
                        budgetLimitError = null
                    },
                    label = { Text("Budget Limit") },
                    isError = budgetLimitError != null,
                    supportingText = {
                        if (budgetLimitError != null)
                            Text(budgetLimitError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // DESCRIPTION
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

                    onSubmit(
                        budget.copy(
                            category = category,
                            subCategory = subCategory.ifBlank { null },
                            budgetLimit = budgetLimit,
                            description = description.ifBlank { null }
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
