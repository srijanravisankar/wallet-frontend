// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/models/Budget.kt

package com.example.wallet_frontend.models

import kotlinx.serialization.Serializable

// This data class defines the "shape" of a Budget object
// for our frontend UI.
@Serializable
data class Budget(
    val budgetId: Int? = null,
    val userId: Int,
    val category: String,
    val subCategory: String? = null,
    val budgetLimit: String,
    val periodType: String,
    val startDate: String,
    val endDate: String,
    val description: String? = null,
    val createdAt: String? = null
)