package com.example.calculator

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*

data class Calculation(
    val expression: String,
    val result: Double,
    val deviceId: String? = null
)

class CalculatorApiService {
    private val client = HttpClient {
        install(JsonFeature)
    }

    private val baseUrl = "http://your-backend-url/api"

    suspend fun saveCalculation(calculation: Calculation) {
        try {
            client.post<Unit>("$baseUrl/calculations") {
                contentType(ContentType.Application.Json)
                body = calculation
            }
        } catch (e: Exception) {
            // Handle error appropriately
            e.printStackTrace()
        }
    }

    suspend fun getCalculationHistory(): List<Calculation> {
        return try {
            client.get("$baseUrl/calculations")
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}