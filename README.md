# Wallet Frontend - Personal Finance Management Application

### Please refer for detailed frontend progression: [documentation.md](documentation.md)

## Technology Stack

This is a **Kotlin Multiplatform** project targeting **Desktop (JVM)** with the following technology stack:

### Core Technologies
- **Kotlin** (v2.2.20) - Modern programming language
- **Compose Multiplatform** (v1.9.1) - Declarative UI framework
- **Material Design 3** - UI components and design system
- **Gradle** - Build automation

### Key Libraries
- **Ktor Client** (v2.3.7) - HTTP client for API communication
  - `ktor-client-cio` - Coroutine-based I/O engine
  - `ktor-client-content-negotiation` - Content serialization
  - `ktor-serialization-kotlinx-json` - JSON serialization
- **Kotlinx Serialization** (v1.6.3) - Data serialization
- **Kotlinx Coroutines** (v1.10.2) - Asynchronous programming
- **AndroidX Lifecycle** (v2.9.5) - ViewModel and lifecycle management

## Project Structure

```
composeApp/src/
├── jvmMain/kotlin/com/example/wallet_frontend/
│   ├── App.kt                 # Main application entry point
│   ├── main.kt                # Desktop window configuration
│   ├── UserSession.kt         # Global user session management
│   ├── components/            # Reusable UI components
│   │   ├── SideNavigationPanel.kt
│   │   ├── TransactionRow.kt
│   │   ├── BudgetCard.kt
│   │   └── [Dialog components]
│   ├── models/                # Data models
│   │   ├── Transaction.kt
│   │   ├── Budget.kt
│   │   └── User.kt
│   ├── network/               # API client layer
│   │   ├── ApiClient.kt
│   │   ├── TransactionApi.kt
│   │   ├── BudgetApi.kt
│   │   └── UserApi.kt
│   └── screens/               # Feature screens
│       ├── LoginScreen.kt
│       ├── TransactionsScreen.kt
│       ├── BudgetsScreen.kt
│       ├── PieChartScreen.kt
│       └── SettingsScreen.kt
```

## Key Code Examples

### 1. Application Entry Point & Navigation

The main app uses Compose's state management to handle screen navigation and authentication:

```kotlin
@Composable
fun App() {
    var selectedScreen by remember { mutableStateOf("Transactions") }
    val isLoggedIn by UserSession.currentUser

    MaterialTheme {
        if (isLoggedIn == null) {
            LoginScreen(onLoginSuccess = {})
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                SideNavigationPanel(
                    currentSelectedScreen = selectedScreen,
                    onScreenSelected = { screenName ->
                        selectedScreen = screenName
                    }
                )
                Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                    when (selectedScreen) {
                        "Transactions" -> TransactionsScreen()
                        "Budgets" -> BudgetsScreen()
                        "Pie Chart" -> PieChartScreen()
                        "Settings" -> SettingsScreen()
                    }
                }
            }
        }
    }
}
```

### 2. User Session Management

Global state management using Kotlin's `object` singleton pattern:

```kotlin
object UserSession {
    var currentUser = mutableStateOf<User?>(null)

    fun isLoggedIn(): Boolean = currentUser.value != null

    fun login(user: User) {
        currentUser.value = user
    }

    fun logout() {
        currentUser.value = null
    }
}
```

### 3. HTTP Client Configuration

Ktor client setup with JSON serialization:

```kotlin
val apiClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }
}
```

### 4. API Integration Example

RESTful API calls using Ktor with coroutines:

```kotlin
object TransactionApi {
    private const val BASE_URL = "http://127.0.0.1:8080"

    suspend fun getTransactions(userId: Int): List<Transaction> {
        return apiClient.get("$BASE_URL/transactions/$userId") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun addTransaction(transaction: Transaction): Boolean {
        val response: HttpResponse = apiClient.post("$BASE_URL/transactions") {
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }
        return response.status == HttpStatusCode.Created
    }
}
```

### 5. Data Models with Kotlinx Serialization

```kotlin
@Serializable
data class Transaction(
    val transactionId: Int? = null,
    val userId: Int,
    val title: String,
    val category: String,
    val transactionType: String,
    val amount: String,
    val date: String,
    val description: String? = null
)
```

### 6. Screen with LaunchedEffect & State Management

Example from TransactionsScreen showing async data fetching:

```kotlin
@Composable
fun TransactionsScreen() {
    val transactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val userId = UserSession.currentUser.value?.userId ?: return

    LaunchedEffect(Unit) {
        try {
            transactions.value = TransactionApi.getTransactions(userId)
        } catch (e: Exception) {
            println("Error fetching transactions: ${e.message}")
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transactions") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Add transaction */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(transactions.value) { transaction ->
                TransactionRow(transaction = transaction)
            }
        }
    }
}
```

### 7. Custom UI Components

Navigation panel using Material 3 NavigationRail:

```kotlin
@Composable
fun SideNavigationPanel(
    currentSelectedScreen: String,
    onScreenSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(96.dp)
            .background(Color(0xFFFCEEEE))
    ) {
        NavigationRailItem(
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Transactions") },
            label = { Text("Transactions") },
            selected = (currentSelectedScreen == "Transactions"),
            onClick = { onScreenSelected("Transactions") }
        )
    }
}
```

## Features

- **Authentication**: Login and signup with backend API integration
- **Transaction Management**: Create, read, update, delete transactions
- **Budget Tracking**: Set and monitor category-based budgets
- **Data Visualization**: Pie charts for expense analytics
- **User Settings**: Profile management and preferences

## Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app:

**macOS/Linux:**
```shell
./gradlew :composeApp:run
```

**Windows:**
```shell
.\gradlew.bat :composeApp:run
```

## API Configuration

The application connects to a backend API running at `http://127.0.0.1:8080`. Ensure the backend server is running before starting the frontend.

---

## Programming Paradigms Used in This Frontend

This project demonstrates multiple programming paradigms working together in a modern application:

### 1. **Declarative Programming**

The UI is built using Jetpack Compose, which follows a declarative paradigm where you describe *what* the UI should look like, not *how* to build it.

```kotlin
@Composable
fun BudgetCard(budget: Budget, currentSpent: BigDecimal) {
    val limit = budget.budgetLimit.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val progress = if (limit > BigDecimal.ZERO) {
        (currentSpent / limit).toFloat()
    } else 0f
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = budget.category)
            LinearProgressIndicator(progress = { progress })
            Text(text = "$${currentSpent} / $${limit}")
        }
    }
}
```

**Why it's declarative**: We describe the UI structure (Card contains Column contains Text and Progress), and Compose handles rendering, updates, and recomposition automatically.

### 2. **Functional Programming**

Kotlin's functional features are used extensively throughout the codebase:

#### Higher-Order Functions (Functions as Parameters)
```kotlin
@Composable
fun SideNavigationPanel(
    currentSelectedScreen: String,
    onScreenSelected: (String) -> Unit  // Function as parameter
) {
    NavigationRailItem(
        onClick = { onScreenSelected("Transactions") }  // Lambda expression
    )
}
```

#### Pure Functions & Immutability
```kotlin
// Data transformation using map, filter, groupBy
val spendingByCategory = transactions
    .filter { it.transactionType == "expense" }
    .groupBy { it.category }
    .mapValues { (_, transactionsInCategory) ->
        transactionsInCategory.sumOf { BigDecimal(it.amount) }
    }
```

#### Function Composition
```kotlin
val dataToDisplay = remember(transactions, budgets, selectedChartType) {
    transactions
        .filter { transaction -> isExpense(transaction) }
        .groupBy { it.category }
        .map { (category, txns) -> 
            Triple(category, txns.sumOf { BigDecimal(it.amount) }.toFloat(), getColor(category))
        }
        .sortedByDescending { it.second }
}
```

### 3. **Object-Oriented Programming (OOP)**

Uses classes, objects, encapsulation, and data modeling:

#### Singleton Pattern
```kotlin
object UserSession {
    var currentUser = mutableStateOf<User?>(null)
    
    fun login(user: User) {
        currentUser.value = user
    }
    
    fun logout() {
        currentUser.value = null
    }
}
```

#### Data Classes (Encapsulation)
```kotlin
@Serializable
data class Transaction(
    val transactionId: Int? = null,
    val userId: Int,
    val title: String,
    val category: String,
    val transactionType: String,
    val amount: String
)
```

#### Object-Based API Clients
```kotlin
object TransactionApi {
    private const val BASE_URL = "http://127.0.0.1:8080"
    
    suspend fun getTransactions(userId: Int): List<Transaction> {
        return apiClient.get("$BASE_URL/transactions/$userId").body()
    }
}
```

### 4. **Reactive Programming**

State management with automatic UI updates when data changes:

```kotlin
@Composable
fun TransactionsScreen() {
    // Reactive state - UI automatically recomposes when this changes
    val transactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val showDialog = remember { mutableStateOf(false) }
    
    // Side effect that runs when composable enters composition
    LaunchedEffect(Unit) {
        transactions.value = TransactionApi.getTransactions(userId)
    }
    
    // UI automatically updates when transactions.value changes
    LazyColumn {
        items(transactions.value) { transaction ->
            TransactionRow(transaction = transaction)
        }
    }
}
```

**Key reactive concepts**:
- `remember` - Preserves state across recompositions
- `mutableStateOf` - Observable state that triggers recomposition
- `LaunchedEffect` - Side effects tied to composable lifecycle

### 5. **Asynchronous/Concurrent Programming**

Using Kotlin Coroutines for non-blocking I/O:

```kotlin
val scope = rememberCoroutineScope()

Button(onClick = {
    scope.launch {  // Launch coroutine
        try {
            val success = TransactionApi.addTransaction(newTransaction)
            if (success) {
                transactions.value = TransactionApi.getTransactions(userId)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}) {
    Text("Add Transaction")
}
```

**Suspend functions** for async operations:
```kotlin
suspend fun getTransactions(userId: Int): List<Transaction> {
    return apiClient.get("$BASE_URL/transactions/$userId").body()
}
```

### 6. **Event-Driven Programming**

UI interactions drive application behavior through event handlers:

```kotlin
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    Button(
        onClick = {  // Event handler for click event
            scope.launch {
                val user = UserApi.login(email, password)
                if (user != null) {
                    UserSession.login(user)
                    onLoginSuccess()  // Trigger callback event
                }
            }
        }
    ) {
        Text("Login")
    }
}
```

### Paradigm Integration Summary

This frontend application demonstrates how modern applications integrate multiple paradigms:

| Paradigm | Used For | Key Benefit |
|----------|----------|-------------|
| **Declarative** | UI composition | Simpler reasoning about UI state |
| **Functional** | Data transformations | Immutability, composability |
| **OOP** | Data modeling, API clients | Encapsulation, organization |
| **Reactive** | State management | Automatic UI updates |
| **Async** | Network calls | Non-blocking operations |
| **Event-Driven** | User interactions | Responsive UI |

This **multi-paradigm** approach leverages the strengths of each paradigm to create maintainable, efficient, and user-friendly applications.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)

