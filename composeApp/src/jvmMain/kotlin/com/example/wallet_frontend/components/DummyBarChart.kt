package com.example.wallet_frontend.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DummyBarChart(
    modifier: Modifier = Modifier,
    data: List<Pair<String, Float>>,
    color: Color
) {
    val textMeasurer = rememberTextMeasurer()

    val labelStyle: TextStyle = MaterialTheme.typography.labelSmall

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
        val yAxisBottom = chartHeight - 40

        Canvas(modifier = Modifier.fillMaxSize()) {

            data.forEachIndexed { index, (label, value) ->

                val barX = (index * (barWidth + barSpacing))
                val barHeight = (value / maxValue) * yAxisBottom

                drawRect(
                    color = color,
                    topLeft = Offset(x = barX, y = yAxisBottom - barHeight),
                    size = Size(width = barWidth, height = barHeight)
                )

                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    style = labelStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    color = Color.Black,
                    topLeft = Offset(
                        x = barX + (barWidth / 2) - (textLayoutResult.size.width / 2),
                        y = yAxisBottom + 8
                    )
                )
            }

            drawLine(
                color = Color.Gray,
                start = Offset(x = 0f, y = yAxisBottom),
                end = Offset(x = constraints.maxWidth.toFloat(), y = yAxisBottom),
                strokeWidth = 2f
            )
        }
    }
}