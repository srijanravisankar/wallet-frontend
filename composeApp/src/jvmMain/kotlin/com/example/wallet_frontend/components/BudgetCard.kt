// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/components/BudgetCard.kt

package com.example.wallet_frontend.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wallet_frontend.models.Budget // Import our new Budget model
import java.math.BigDecimal // We'll use this for safe math

/**
 * This is a "dumb" component for showing a single budget's progress.
 *
 * @param budget The Budget object to display (which has the 'limit').
 * @param currentSpent How much has *actually* been spent (we'll pass this in).
 */
@Composable
fun BudgetCard(budget: Budget, currentSpent: BigDecimal) {

    // --- 1. Do Calculations ---
    // We convert the 'String' limit from our model into a 'BigDecimal'
    // for safe and accurate math.
    val limit = budget.budgetLimit.toBigDecimalOrNull() ?: BigDecimal.ZERO

    // Calculate how much is remaining
    val remaining = limit - currentSpent

    // Calculate progress as a fraction (e.g., 0.75 for 75%)
    // We must check if 'limit' is zero to avoid dividing by zero!
    val progress = if (limit > BigDecimal.ZERO) {
        (currentSpent / limit).toFloat()
    } else {
        0f // If the limit is 0, progress is 0
    }

    // Pick a color for the progress bar.
    // If you're over budget (progress > 1.0), make it red.
    val progressBarColor = if (progress > 1.0f) {
        MaterialTheme.colorScheme.error // Red
    } else {
        MaterialTheme.colorScheme.primary // Normal theme color
    }

    // --- 2. Build the UI ---
    // We use a Card for that modern, clean look.
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Padding inside the card
        ) {

            // --- Top Row: Category and Limit ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // The category name
                Text(
                    text = budget.category,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f)) // "eats" the empty space

                // The "Amount / Limit" text
                Text(
                    text = "$${currentSpent.toPlainString()} / $${limit.toPlainString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Space

            // --- The Progress Bar ---
            LinearProgressIndicator(
                progress = { progress }, // Our calculated progress
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = progressBarColor // The color we chose (normal or red)
            )

            Spacer(modifier = Modifier.height(8.dp)) // Space

            // --- Bottom Row: Remaining and Period ---
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Show how much is left (or how much you're over)
                val remainingText = if (remaining >= BigDecimal.ZERO) {
                    "$${remaining.toPlainString()} remaining"
                } else {
                    "$${remaining.abs().toPlainString()} over budget"
                }

                Text(
                    text = remainingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f)) // "eats" the empty space

                // Show the period (e.g., "monthly")
                Text(
                    text = budget.periodType.replaceFirstChar { it.titlecase() }, // Capitalize it
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}