// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/screens/BarChartScreen.kt

package com.example.wallet_frontend.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// --- Import our NEW Bar Chart component ---
import com.example.wallet_frontend.components.DummyBarChart

/**
 * This is the main screen for the "Bar Chart" section.
 * It uses the SAME state logic as the PieChartScreen.
 * It calls our new custom-drawn 'DummyBarChart'.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarChartScreen() {

    // --- 1. All this state is identical to PieChartScreen ---
    var selectedChartType by remember { mutableStateOf("Expenses") }
    val chartTypes = listOf("Expenses", "Income", "Budgets")

    var selectedPeriod by remember { mutableStateOf("This Month") }
    val periods = listOf("Today", "This Week", "This Month", "This Year")

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var selectedCustomDate by remember { mutableStateOf<LocalDate?>(null) }

    // --- 2. Dummy Data for the Bar Charts ---
    // (Note: For "Budgets", we'll just show the spending)

    // --- Expenses ---
    val dummyExpenseToday = listOf("Coffee" to 5.50f, "Lunch" to 14.00f)
    val dummyExpenseThisWeek = listOf("Mon" to 15f, "Tue" to 20f, "Wed" to 10f, "Thu" to 35f, "Fri" to 5f, "Sat" to 40f, "Sun" to 12f)
    val dummyExpenseThisMonth = listOf("Week 1" to 150f, "Week 2" to 200f, "Week 3" to 120f, "Week 4" to 300f)
    val dummyExpenseThisYear = listOf("Jan" to 500f, "Feb" to 450f, "Mar" to 600f, "Apr" to 550f, "May" to 700f, "Jun" to 650f)
    val dummyExpenseCustom = listOf("Item A" to 100f, "Item B" to 25f)

    // --- Income ---
    val dummyIncomeThisWeek = listOf("Mon" to 0f, "Tue" to 0f, "Wed" to 300f, "Thu" to 0f, "Fri" to 500f, "Sat" to 0f, "Sun" to 0f)
    val dummyIncomeThisMonth = listOf("Week 1" to 500f, "Week 2" to 1200f, "Week 3" to 500f, "Week 4" to 2000f)
    val dummyIncomeThisYear = listOf("Jan" to 4000f, "Feb" to 4000f, "Mar" to 4500f, "Apr" to 4000f)

    // --- Budgets (Comparing categories) ---
    val dummyBudgetsData = listOf("Food" to 380f, "Transport" to 100f, "Fun" to 210f, "Bills" to 120f)


    // --- The Scaffold Layout ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trends & Budgets") },
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

            // --- Time Period Toggles (with Calendar) ---
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

                IconButton(
                    onClick = { showDatePicker = true },
                    colors = IconButtonDefaults.iconButtonColors(
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

            // --- Date Picker Dialog (unchanged) ---
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                                val millis = datePickerState.selectedDateMillis ?: return@TextButton
                                selectedCustomDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                selectedPeriod = "Custom"
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. THE LOGIC BLOCK ---
            // We select the right data and color based on the toggles

            val (dataToDisplay, chartColor) = when (selectedChartType) {
                "Expenses" -> {
                    val data = when (selectedPeriod) {
                        "Today" -> dummyExpenseToday
                        "This Week" -> dummyExpenseThisWeek
                        "This Month" -> dummyExpenseThisMonth
                        "This Year" -> dummyExpenseThisYear
                        "Custom" -> dummyExpenseCustom
                        else -> emptyList()
                    }
                    data to Color(0xFFE91E63) // Red color for expenses
                }
                "Income" -> {
                    val data = when (selectedPeriod) {
                        "This Week" -> dummyIncomeThisWeek
                        "This Month" -> dummyIncomeThisMonth
                        "This Year" -> dummyIncomeThisYear
                        // (No dummy data for Today/Custom in this example)
                        else -> emptyList()
                    }
                    data to Color(0xFF4CAF50) // Green color for income
                }
                "Budgets" -> {
                    // For budgets, we just show the category comparison
                    // (The time toggles won't change this dummy data)
                    dummyBudgetsData to Color(0xFF2196F3) // Blue color for budgets
                }
                else -> emptyList<Pair<String,Float>>() to Color.Gray
            }

            // --- 4. THE CHART ---
            // We call our custom component with the selected data
            DummyBarChart(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                data = dataToDisplay,
                color = chartColor
            )
        }
    }
}