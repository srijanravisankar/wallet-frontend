// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/components/TransactionRow.kt

package com.example.wallet_frontend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.models.Transaction // Import the data class we just made

/**
 * This is a "dumb" component for showing a single transaction.
 * It takes a 'transaction' object and displays it.
 *
 * @param transaction The Transaction data to display.
 */
@Composable
fun TransactionRow(transaction: Transaction) {

    // We'll use a 'Card' for a modern, slightly-elevated look
    Card(
        modifier = Modifier
            .fillMaxWidth() // Make the card fill the width
            .padding(vertical = 4.dp), // Add space between cards
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        // 'Row' is the main layout for the card's content
        Row(
            modifier = Modifier
                .padding(16.dp) // Padding inside the card
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Align everything vertically
        ) {

            // --- 1. The Icon (Left Side) ---

            // Decide the icon and color based on the transaction type
            val icon = if (transaction.transactionType == "income") {
                Icons.Default.ArrowUpward // Green "Up" arrow
            } else {
                Icons.Default.ArrowDownward // Red "Down" arrow
            }
            val iconColor = if (transaction.transactionType == "income") {
                Color(0xFF00C853) // A nice green
            } else {
                Color(0xFFD50000) // A nice red
            }

            // A Box to hold the icon with a colored background
            Box(
                modifier = Modifier
                    .size(40.dp) // A fixed 40x40 size
                    .clip(RoundedCornerShape(8.dp)) // Rounded corners
                    .background(iconColor.copy(alpha = 0.1f)), // Light, transparent background
                contentAlignment = Alignment.Center // Center the icon inside the Box
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = transaction.transactionType,
                    tint = iconColor // Make the icon itself the solid red/green
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between icon and text

            // --- 2. Title and Category (Middle) ---
            Column(
                modifier = Modifier
                    .weight(1f) // This 'weight' makes this column fill all available space
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between text and amount

            // --- 3. Amount and Date (Right Side) ---
            Column(
                horizontalAlignment = Alignment.End // Align text to the right
            ) {
                // Format the amount string
                val amountText = if (transaction.transactionType == "income") {
                    "+$${transaction.amount}"
                } else {
                    "-$${transaction.amount}"
                }

                Text(
                    text = amountText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = iconColor // Use the same red/green color
                )
                Text(
                    // We'll just show the date part (e.g., "2025-11-14")
                    text = transaction.date.substringBefore("T"),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}