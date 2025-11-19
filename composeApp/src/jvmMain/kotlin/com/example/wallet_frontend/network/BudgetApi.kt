package com.example.wallet_frontend.network

import com.example.wallet_frontend.models.Budget
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object BudgetApi {

    private const val BASE_URL = "http://127.0.0.1:8080"

    suspend fun getBudgets(userId: Int): List<Budget> {
        return apiClient.get("$BASE_URL/budgets/$userId") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun addBudget(budget: Budget): Boolean {
        return try {
            println(budget)
            val response: HttpResponse = apiClient.post("$BASE_URL/budgets") {
                contentType(ContentType.Application.Json)
                setBody(budget)
            }

            // Print raw HTTP status + body for debugging
            println("POST /budgets → status=${response.status}")
            println("response body = ${response.bodyAsText()}")

            response.status == HttpStatusCode.Created

        } catch (e: Exception) {
            println("Error Posting Budget: ${e}")
            println("Cause: ${e.cause}")
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteBudget(budgetId: Int): Boolean {
        return try {
            val response: HttpResponse = apiClient.delete("$BASE_URL/budgets/$budgetId")
            response.status == HttpStatusCode.NoContent
        } catch (e: Exception) {
            println("Delete budget error: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun updateBudget(id: Int, updated: Budget): Boolean {
        return try {
            val response = apiClient.put("$BASE_URL/budgets/$id") {
                contentType(ContentType.Application.Json)
                setBody(updated)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Update budget error: ${e.message}")
            false
        }
    }


}


