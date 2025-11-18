package com.example.wallet_frontend.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet_frontend.UserSession
import com.example.wallet_frontend.models.Budget
import com.example.wallet_frontend.models.Transaction
import com.example.wallet_frontend.network.BudgetApi
import com.example.wallet_frontend.network.TransactionApi
import java.math.BigDecimal
import java.time.*
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieChartScreen() {

    val currentUser by UserSession.currentUser
    val userId = currentUser?.userId

    if (userId == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Please log in to view analytics")
        }
        return
    }

    var selectedChartType by remember { mutableStateOf("Expenses") }
    val chartTypes = listOf("Expenses", "Budgets")

    var selectedPeriod by remember { mutableStateOf("This Month") }
    val periods = listOf("Today", "This Week", "This Month", "This Year")

    var selectedCustomDate by remember { mutableStateOf<LocalDate?>(null) }

    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var budgets by remember { mutableStateOf<List<Budget>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userId, selectedPeriod, selectedCustomDate) {
        isLoading = true
        try {
            transactions = TransactionApi.getTransactions(userId)
            budgets = BudgetApi.getBudgets(userId)
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    val dateRange = remember(selectedPeriod, selectedCustomDate) {
        val now = LocalDate.now()
        when (selectedPeriod) {
            "Today" -> now to now

            "This Week" -> {
                val start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                val end = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                start to end
            }

            "This Month" -> now.withDayOfMonth(1) to now.withDayOfMonth(now.lengthOfMonth())
            "This Year" -> now.withDayOfYear(1) to now.withDayOfYear(now.lengthOfYear())

            "Custom" -> selectedCustomDate?.let { it to it } ?: (now to now)

            else -> now to now
        }
    }


    val dataToDisplay = remember(transactions, budgets, selectedChartType, dateRange) {
        if (selectedChartType == "Expenses") {
            val filteredTransactions = transactions.filter { transaction ->
                transaction.transactionType == "expense" &&
                        isWithinDateRange(transaction.date, dateRange.first, dateRange.second)
            }

            filteredTransactions
                .groupBy { it.category }
                .map { (category, txns) ->
                    val total = txns.sumOf { BigDecimal(it.amount) }.toFloat()
                    Triple(category, total, getCategoryColor(category))
                }
                .sortedByDescending { it.second }
        } else {
            val filteredBudgets = budgets.filter { budget ->
                isWithinDateRange(budget.startDate, dateRange.first, dateRange.second) ||
                        isWithinDateRange(budget.endDate, dateRange.first, dateRange.second)
            }

            filteredBudgets.map { budget ->
                Triple(
                    budget.category,
                    BigDecimal(budget.budgetLimit).toFloat(),
                    getCategoryColor(budget.category)
                )
            }.sortedByDescending { it.second }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spending Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                chartTypes.forEach { chartType ->
                    SegmentedButton(
                        shape = RoundedCornerShape(50),
                        onClick = { selectedChartType = chartType },
                        selected = (chartType == selectedChartType)
                    ) {
                        Text(chartType)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.weight(1f)
                ) {
                    periods.forEach { period ->
                        SegmentedButton(
                            shape = RoundedCornerShape(50),
                            onClick = {
                                selectedPeriod = period
                                selectedCustomDate = null
                            },
                            selected = (period == selectedPeriod && selectedCustomDate == null)
                        ) {
                            Text(period)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            } else {
                // Display the chart
                PieChartWithLegend(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    data = dataToDisplay
                )
            }
        }
    }
}

fun isWithinDateRange(dateString: String, start: LocalDate, end: LocalDate): Boolean {
    return try {
        val date = when {
            dateString.contains("T") -> LocalDate.parse(dateString.substring(0, 10))
            else -> LocalDate.parse(dateString)
        }
        !date.isBefore(start) && !date.isAfter(end)
    } catch (e: Exception) {
        println("Date parse error: $dateString -> ${e.message}")
        false
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food" -> Color(0xFFE91E63)
        "Utilities" -> Color(0xFF9C27B0)
        "Transportation" -> Color(0xFF2196F3)
        "Personal" -> Color(0xFF4CAF50)
        "Family" -> Color(0xFFFF9800)
        "Entertainment" -> Color(0xFF00BCD4)
        "Subscriptions" -> Color(0xFFFF5722)
        "Miscellaneous" -> Color(0xFF607D8B)
        else -> Color(0xFF795548)
    }
}

@Composable
fun PieChartWithLegend(
    modifier: Modifier = Modifier,
    data: List<Triple<String, Float, Color>>
) {
    val totalValue = data.sumOf { it.second.toDouble() }.toFloat()

    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // The Pie Chart
        if (data.isEmpty()) {
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No data for this period",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        } else {
            Canvas(
                modifier = Modifier.size(300.dp)
            ) {
                val strokeWidth = 100f
                var currentStartAngle = -90f

                data.forEach { (_, value, color) ->
                    val sweepAngle = (value / totalValue) * 360f

                    drawArc(
                        color = color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth)
                    )

                    currentStartAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // The Legend
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            data.forEach { (label, value, color) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color, shape = RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val percentage = (value / totalValue) * 100
                    val percentageText = if (totalValue > 0) "(${percentage.toInt()}%)" else ""

                    Text(
                        text = "$label: $${"%.2f".format(value)} $percentageText",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}