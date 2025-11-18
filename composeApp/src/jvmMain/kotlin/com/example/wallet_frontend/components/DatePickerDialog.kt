package com.example.wallet_frontend.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var tempDate by remember { mutableStateOf(initialDate) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Select Date", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = tempDate.toString(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Date") }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { tempDate = tempDate.minusDays(1) }) {
                        Text("-1 day")
                    }
                    Button(onClick = { tempDate = tempDate.plusDays(1) }) {
                        Text("+1 day")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = { onDateSelected(tempDate) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
