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
import com.example.wallet_frontend.models.Budget
import java.math.BigDecimal

@Composable
fun BudgetCard(
    budget: Budget,
    currentSpent: BigDecimal,
    modifier: Modifier = Modifier
) {
    val limit = budget.budgetLimit.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val remaining = limit - currentSpent
    val progress = if (limit > BigDecimal.ZERO) {
        (currentSpent / limit).toFloat()
    } else 0f

    val progressBarColor = if (progress > 1f) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.fillMaxWidth(),   // ← use incoming modifier
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = budget.category,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$${currentSpent.toPlainString()} / $${limit.toPlainString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressBarColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val remainingText =
                    if (remaining >= BigDecimal.ZERO)
                        "$${remaining.toPlainString()} remaining"
                    else
                        "$${remaining.abs().toPlainString()} over budget"

                Text(
                    text = remainingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = budget.periodType.replaceFirstChar { it.titlecase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
