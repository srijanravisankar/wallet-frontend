// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/components/DummyBarChart.kt

package com.example.wallet_frontend.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
// --- NEW IMPORT ---
import androidx.compose.ui.text.TextStyle // We need this
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * --- OUR NEW CUSTOM BAR CHART COMPONENT ---
 *
 * This function knows how to draw a simple bar chart.
 * It uses NO external libraries, only the built-in Canvas.
 */
@Composable
fun DummyBarChart(
    modifier: Modifier = Modifier,
    data: List<Pair<String, Float>>, // A list of (Label, Value)
    color: Color
) {
    val textMeasurer = rememberTextMeasurer()

    // --- THIS IS THE FIX ---
    // We "grab" the text style here, in the @Composable context
    val labelStyle: TextStyle = MaterialTheme.typography.labelSmall
    // -----------------------

    BoxWithConstraints(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (data.isEmpty()) {
            Text("No data for this period", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            return@BoxWithConstraints
        }

        val barCount = data.size
        val allBarsWidth = (constraints.maxWidth * 0.8f)
        val barWidth = allBarsWidth / barCount
        val barSpacing = (constraints.maxWidth * 0.2f) / (barCount - 1).coerceAtLeast(1)

        val maxValue = data.maxOfOrNull { it.second } ?: 1f
        val chartHeight = constraints.maxHeight.toFloat()
        val yAxisBottom = chartHeight - 40 // Leave 40px for labels

        Canvas(modifier = Modifier.fillMaxSize()) {

            data.forEachIndexed { index, (label, value) ->

                val barX = (index * (barWidth + barSpacing))
                val barHeight = (value / maxValue) * yAxisBottom

                // Draw the bar
                drawRect(
                    color = color,
                    topLeft = Offset(x = barX, y = yAxisBottom - barHeight),
                    size = Size(width = barWidth, height = barHeight)
                )

                // --- 4. Draw the X-axis Labels ---
                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    // --- HERE, WE USE THE VARIABLE ---
                    style = labelStyle, // This is no longer an error
                    // ---------------------------------
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    color = Color.Black,
                    topLeft = Offset(
                        x = barX + (barWidth / 2) - (textLayoutResult.size.width / 2),
                        y = yAxisBottom + 8 // 8px below the axis
                    )
                )
            }

            // --- 5. Draw the X-axis Line ---
            drawLine(
                color = Color.Gray,
                start = Offset(x = 0f, y = yAxisBottom),
                end = Offset(x = constraints.maxWidth.toFloat(), y = yAxisBottom),
                strokeWidth = 2f
            )
        }
    }
}