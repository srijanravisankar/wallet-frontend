// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/screens/PieChartScreen.kt

package com.example.wallet_frontend.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class) // This is needed for DatePicker and SegmentedButton
@Composable
fun PieChartScreen() {

    // --- State for our Controls ---
    var selectedChartType by remember { mutableStateOf("Expenses") }
    val chartTypes = listOf("Expenses", "Budgets")

    // --- 1. UPDATED STATE FOR TIME ---
    // We've renamed the periods
    var selectedPeriod by remember { mutableStateOf("This Month") }
    val periods = listOf("Today", "This Week", "This Month", "This Year")

    // This state is for the calendar popup
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // This will hold our custom selected date
    var selectedCustomDate by remember { mutableStateOf<LocalDate?>(null) }
    // ------------------------------------


    // --- 2. DUMMY DATA FOR ALL TABS (Renamed) ---
    // (I've also added a "Custom" data set for when you pick a date)

    // --- Expenses Data ---
    val dummyExpenseToday = listOf(Triple("Coffee", 5.50f, Color(0xFFE91E63)), Triple("Lunch", 14.00f, Color(0xFF2196F3)))
    val dummyExpenseThisWeek = listOf(Triple("Groceries", 120.00f, Color(0xFFE91E63)), Triple("Gas", 45.00f, Color(0xFF9C27B0)), Triple("Dinner Out", 60.00f, Color(0xFF4CAF50)))
    val dummyExpenseThisMonth = listOf(Triple("Food", 485.50f, Color(0xFFE91E63)), Triple("Bills", 120.00f, Color(0xFF9C27B0)), Triple("Transportation", 75.00f, Color(0xFF2196F3)), Triple("Entertainment", 110.00f, Color(0xFF4CAF50)), Triple("Other", 50.25f, Color(0xFFFF9800)))
    val dummyExpenseThisYear = listOf(Triple("Housing", 15000.00f, Color(0xFF009688)), Triple("Food", 5500.00f, Color(0xFFE91E63)), Triple("Utilities", 2400.00f, Color(0xFF9C27B0)), Triple("Insurance", 2000.00f, Color(0xFF2196F3)), Triple("Vacation", 3000.00f, Color(0xFFFF9800)))
    val dummyExpenseCustom = listOf(Triple("Custom Event", 75.00f, Color(0xFFE91E63)), Triple("Booking", 250.00f, Color(0xFF2196F3)))

    // --- Budgets Data ---
    val dummyBudgetsToday = listOf(Triple("Food", 25.00f, Color(0xFFE91E63)), Triple("Coffee", 5.00f, Color(0xFF795548)))
    val dummyBudgetsThisWeek = listOf(Triple("Groceries", 150.00f, Color(0xFFE91E63)), Triple("Gas", 50.00f, Color(0xFF9C27B0)))
    val dummyBudgetsThisMonth = listOf(Triple("Food", 500.00f, Color(0xFFE91E63)), Triple("Transportation", 150.00f, Color(0xFF2196F3)), Triple("Entertainment", 200.00f, Color(0xFF9C27B0)))
    val dummyBudgetsThisYear = listOf(Triple("Vacation", 3000.00f, Color(0xFFFF9800)), Triple("Gifts", 500.00f, Color(0xFF607D8B)))
    val dummyBudgetsCustom = listOf(Triple("Party", 200.00f, Color(0xFF9C27B0)))


    // --- The Scaffold Layout ---
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

        // --- The Main Content Column ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Chart Type Toggle Buttons ---
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

            // --- 3. UPDATED Time Period Toggles ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // The main 4 toggle buttons
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.weight(1f) // Let it take most of the space
                ) {
                    periods.forEach { period ->
                        SegmentedButton(
                            shape = RoundedCornerShape(50),
                            onClick = {
                                selectedPeriod = period
                                selectedCustomDate = null // Clear custom date
                            },
                            // Check if this period is selected AND it's not the custom date
                            selected = (period == selectedPeriod && selectedCustomDate == null)
                        ) {
                            Text(period)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // The new "Custom Date" calendar button
                IconButton(
                    onClick = { showDatePicker = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        // If a custom date is set, show the button as "selected"
                        contentColor = if (selectedCustomDate != null) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Custom Date")
                }
            }


            Spacer(modifier = Modifier.height(32.dp))

            // --- 4. NEW: The Date Picker Dialog ---
            // This is the calendar popup.
            // It will only show when 'showDatePicker' is true.
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        // Hide the dialog when clicked outside
                        showDatePicker = false
                    },
                    confirmButton = {
                        // "OK" button
                        TextButton(
                            onClick = {
                                showDatePicker = false // Hide the dialog
                                // Get the selected date in milliseconds
                                val millis = datePickerState.selectedDateMillis ?: return@TextButton
                                // Convert it to a LocalDate object
                                selectedCustomDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                // Set the period to "Custom"
                                selectedPeriod = "Custom"
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        // "Cancel" button
                        TextButton(
                            onClick = {
                                showDatePicker = false // Hide the dialog
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    // This is the actual Calendar component
                    DatePicker(state = datePickerState)
                }
            }


            // --- 5. FULLY UPDATED CHART LOGIC ---
            val dataToDisplay: List<Triple<String, Float, Color>>

            when (selectedChartType) {
                "Expenses" -> {
                    dataToDisplay = when (selectedPeriod) {
                        "Today" -> dummyExpenseToday
                        "This Week" -> dummyExpenseThisWeek
                        "This Month" -> dummyExpenseThisMonth
                        "This Year" -> dummyExpenseThisYear
                        "Custom" -> dummyExpenseCustom // Show custom data
                        else -> emptyList()
                    }
                }
                "Budgets" -> {
                    dataToDisplay = when (selectedPeriod) {
                        "Today" -> dummyBudgetsToday
                        "This Week" -> dummyBudgetsThisWeek
                        "This Month" -> dummyBudgetsThisMonth
                        "This Year" -> dummyBudgetsThisYear
                        "Custom" -> dummyBudgetsCustom // Show custom data
                        else -> emptyList()
                    }
                }
                else -> {
                    dataToDisplay = emptyList() // Failsafe
                }
            }

            // A small text to show which date is selected
            if (selectedCustomDate != null) {
                Text(
                    text = "Showing data for: $selectedCustomDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Call the chart function (this part is unchanged)
            DummyPieChart(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                data = dataToDisplay
            )
        }
    }
}


/**
 * --- OUR DUMMY CHART COMPOSABLE ---
 * (This function is identical to before, it's perfect)
 */
@Composable
fun DummyPieChart(
    modifier: Modifier = Modifier,
    data: List<Triple<String, Float, Color>>
) {
    // 1. Calculate the total value
    val totalValue = data.sumOf { it.second.toDouble() }.toFloat()

    // 2. Draw chart and legend side-by-side
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // --- Part A: The Canvas (The Chart itself) ---
        if (data.isEmpty()) {
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No data for this period", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        } else {
            Canvas(
                modifier = Modifier
                    .size(300.dp)
            ) {
                val strokeWidth = 100f
                var currentStartAngle = -90f

                // 3. Loop over each piece of data
                data.forEach { (label, value, color) ->

                    // 4. Calculate this slice's angle
                    val sweepAngle = (value / totalValue) * 360f

                    // 5. Draw the arc
                    drawArc(
                        color = color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth)
                    )

                    // 6. Move the "start" for the next slice
                    currentStartAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // --- Part B: The Legend (The text labels) ---
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
                            .size(12.dp)
                            .background(color, shape = RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val percentage = (value / totalValue) * 100
                    val percentageText = if (totalValue > 0) "(${percentage.toInt()}%)" else ""

                    Text(
                        text = "$label $percentageText",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}