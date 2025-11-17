// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/components/SettingItems.kt

package com.example.wallet_frontend.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A reusable Composable for a single setting item.
 *
 * @param title The main text of the setting.
 * @param subtitle A description of what the setting does.
 * @param onClick The action to perform when clicked.
 */
@Composable
fun SettingRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Make the whole row clickable
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // This Column holds the title and subtitle
        Column(
            modifier = Modifier.weight(1f), // Takes up all available space
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

/**
 * A reusable Composable for a setting with a toggle switch.
 *
 * @param title The main text of the setting.
 * @param subtitle A description of what the setting does.
 * @param isChecked The current state of the switch.
 * @param onCheckedChange A (Boolean) -> Unit function called when the switch is toggled.
 */
@Composable
fun SwitchSettingRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) } // Click the row to toggle
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // The Switch toggle
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * A simple Text Composable to be used as a Section Header.
 */
@Composable
fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary, // Use the app's primary color
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 16.dp) // Add extra space above the header
    )
}

/**
 * A setting row for dangerous actions, styled in red.
 */
@Composable
fun DestructiveSettingRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error // Use the "error" color (red)
        )
    }
}