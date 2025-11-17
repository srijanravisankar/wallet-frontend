package com.example.wallet_frontend

import androidx.compose.runtime.mutableStateOf
import com.example.wallet_frontend.models.User

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