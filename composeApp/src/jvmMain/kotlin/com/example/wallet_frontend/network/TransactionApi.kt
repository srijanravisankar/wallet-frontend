package com.example.wallet_frontend.network

import com.example.wallet_frontend.models.Transaction
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object TransactionApi {

    private const val BASE_URL = "http://127.0.0.1:8080"

    suspend fun getTransactions(userId: Int): List<Transaction> {
        return apiClient.get("$BASE_URL/transactions/$userId") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun addTransaction(transaction: Transaction): Boolean {
        return try {
            println(transaction)
            val response: HttpResponse = apiClient.post("$BASE_URL/transactions") {
                contentType(ContentType.Application.Json)
                setBody(transaction)
            }

            // Print raw HTTP status + body for debugging
            println("POST /transactions → status=${response.status}")
            println("response body = ${response.bodyAsText()}")

            response.status == HttpStatusCode.Created

        } catch (e: Exception) {
            println("Error Posting Transaction: ${e}")
            println("Cause: ${e.cause}")
            e.printStackTrace()
            false
        }
    }

}


