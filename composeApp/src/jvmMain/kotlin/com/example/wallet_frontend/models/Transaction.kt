// In composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/models/Transaction.kt

package com.example.wallet_frontend.models

import kotlinx.serialization.Serializable

// This is the "shape" of our data.
// The frontend needs to know this so it can
// understand the data it will (later) get from your backend.
@Serializable
data class Transaction(
    val transactionId: Int? = null,
    val userId: Int,
    val title: String,
    val category: String,
    val subCategory: String? = null,
    val transactionType: String, // "expense" or "income"
    val amount: String,
    val date: String,
    val description: String? = null,
    val location: String? = null,
    val createdAt: String? = null
)