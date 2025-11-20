package dev.kamikaze.crypto.mcp.services

import dev.kamikaze.crypto.mcp.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CoinStatsService {
    private val baseUrl = "https://openapiv1.coinstats.app"
    private val apiKey = "nDvmJSRCI8fhexEJd5TbKZx307Ua8KxV7w38y2ABv0c="

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    suspend fun getCoins(limit: Int = 100): List<CoinStatsCoin> {
        return try {
            val response: CoinStatsCoinsResponse = client.get("$baseUrl/coins") {
                parameter("limit", limit)
                header("X-API-KEY", apiKey)
            }.body()
            response.result
        } catch (e: Exception) {
            println("Error fetching coins: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCoinByTicker(ticker: String): CoinStatsCoin? {
        return getCoins(100).find {
            it.ticker.equals(ticker, ignoreCase = true) ||
            it.name.equals(ticker, ignoreCase = true)
        }
    }

    suspend fun getCoinById(coinId: String): CoinStatsCoin? {
        return try {
            client.get("$baseUrl/coins/$coinId") {
                header("X-API-KEY", apiKey)
            }.body<CoinStatsCoin>()
        } catch (e: Exception) {
            println("Error fetching coin by id '$coinId': ${e.message}")
            null
        }
    }

    suspend fun getNews(limit: Int = 10): List<CoinStatsNews> {
        return try {
            val response: CoinStatsNewsResponse = client.get("$baseUrl/news") {
                parameter("limit", limit)
                header("X-API-KEY", apiKey)
            }.body()
            response.result
        } catch (e: Exception) {
            println("Error fetching news: ${e.message}")
            emptyList()
        }
    }

    suspend fun getFearGreedIndex(): FearGreedData? {
        return try {
            client.get("$baseUrl/insights/fear-and-greed") {
                header("X-API-KEY", apiKey)
            }.body()
        } catch (e: Exception) {
            println("Error fetching fear-greed: ${e.message}")
            null
        }
    }
}