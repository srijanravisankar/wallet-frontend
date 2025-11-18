package com.example.wallet_frontend.network

import com.example.wallet_frontend.models.User
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object UserApi {
    private const val BASE_URL = "http://127.0.0.1:8080"

    suspend fun login(email: String, password: String): User? {
        return try {
            val response: HttpResponse = apiClient.get("$BASE_URL/users/login") {
                parameter("email", email)
                parameter("password", password)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<User>()
            } else {
                null
            }
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun signUp(user: User): Boolean {
        return try {
            val response: HttpResponse = apiClient.post("$BASE_URL/users") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            println("Sign up error: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}