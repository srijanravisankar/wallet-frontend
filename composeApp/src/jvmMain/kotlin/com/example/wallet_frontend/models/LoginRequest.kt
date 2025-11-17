// Create a new file: composeApp/src/jvmMain/kotlin/com/example/wallet_frontend/models/LoginRequest.kt

package com.example.wallet_frontend.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)