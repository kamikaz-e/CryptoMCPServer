package com.crypto.mcp.services

import dev.kamikaze.crypto.mcp.models.ChatResponseItem
import dev.kamikaze.crypto.mcp.models.CoinPrice
import dev.kamikaze.crypto.mcp.models.NewsItem

class ChatProcessor(
    private val coinStatsService: CoinStatsService,
    private val priceTracker: PriceTracker
) {
    private val targetCoins = listOf("BTC", "ETH", "SOL", "ASTR", "HYPE", "ZEC")

    suspend fun processMessage(message: String): List<ChatResponseItem> {
        val lowerMessage = message.lowercase()

        return when {
            lowerMessage.contains("курс") || lowerMessage.contains("цен") || lowerMessage.contains("стоимость") -> {
                getPricesResponse()
            }
            lowerMessage.contains("страх") || lowerMessage.contains("жадность") || lowerMessage.contains("fear") -> {
                getFearGreedResponse()
            }
            lowerMessage.contains("новост") || lowerMessage.contains("news") -> {
                getNewsResponse()
            }
            lowerMessage.contains("расскажи") || lowerMessage.contains("информацию") -> {
                getCoinInfoResponse(findTicker(message))
            }
            else -> {
                // Универсальный ответ со всей информацией
                listOf(
                    ChatResponseItem(
                        type = "text",
                        text = "Вот актуальная информация по криптовалютам:"
                    )
                ) + getPricesResponse() + getFearGreedResponse()
            }
        }
    }

    suspend fun getSummary(): List<ChatResponseItem> {
        val news = coinStatsService.getNews(5)

        return listOf(
            ChatResponseItem(
                type = "text",
                text = "📊 Минутная сводка по рынку"
            )
        ) + getPricesResponse() + getFearGreedResponse() + listOf(
            ChatResponseItem(
                type = "news",
                items = news.map {
                    NewsItem(
                        title = it.title,
                        source = it.source,
                        time = it.feedDate,
                        url = it.link
                    )
                }
            )
        )
    }

    private suspend fun getPricesResponse(): List<ChatResponseItem> {
        val allCoins = coinStatsService.getCoins(100)

        val targetPrices = targetCoins.mapNotNull { symbol ->
            val coin = allCoins.find {
                it.symbol.equals(symbol, ignoreCase = true) ||
                (symbol == "ASTR" && it.symbol.equals("ASTER", ignoreCase = true))
            } ?: return@mapNotNull null

            val change = priceTracker.getPriceChange1h(coin.symbol)

            CoinPrice(
                symbol = normalizeSymbol(coin.symbol),
                name = normalizeName(coin.name, coin.symbol),
                price = coin.price,
                change1hPct = change?.first,
                change1hAbs = change?.second
            )
        }

        return listOf(
            ChatResponseItem(
                type = "prices",
                base = "USD",
                coins = targetPrices
            )
        )
    }

    private suspend fun getFearGreedResponse(): List<ChatResponseItem> {
        val fearGreed = coinStatsService.getFearGreedIndex() ?: return listOf(
            ChatResponseItem(
                type = "text",
                text = "Не удалось получить индекс страха и жадности"
            )
        )

        return listOf(
            ChatResponseItem(
                type = "fearGreed",
                now = fearGreed.now,
                yesterday = fearGreed.yesterday,
                lastWeek = fearGreed.lastWeek
            )
        )
    }

    private suspend fun getNewsResponse(): List<ChatResponseItem> {
        val news = coinStatsService.getNews(10)

        return listOf(
            ChatResponseItem(
                type = "news",
                items = news.map {
                    NewsItem(
                        title = it.title,
                        source = it.source,
                        time = it.feedDate,
                        url = it.link
                    )
                }
            )
        )
    }

    private suspend fun getCoinInfoResponse(symbol: String?): List<ChatResponseItem> {
        if (symbol == null) {
            return listOf(
                ChatResponseItem(
                    type = "text",
                    text = "Пожалуйста, укажите символ монеты (например, BTC, ETH, SOL)"
                )
            )
        }

        val coin = coinStatsService.getCoinByTicker(symbol) ?: return listOf(
            ChatResponseItem(
                type = "text",
                text = "Монета с символом '$symbol' не найдена"
            )
        )

        return listOf(
            ChatResponseItem(
                type = "coin",
                symbol = coin.symbol,
                name = coin.name,
                description = "Ранг: ${coin.rank}",
                marketCap = coin.marketCap,
                price = coin.price,
                change24h = coin.priceChange1d
            )
        )
    }

  private fun findTicker(text: String): String {
       val regex = """\b[a-zA-Z]{2,5}\b""".toRegex()
       return regex.findAll(text)
           .map { it.value }
           .first()
   }

    private fun normalizeSymbol(symbol: String): String {
        return symbol.uppercase()
    }

    private fun normalizeName(name: String, symbol: String): String {
        return when (symbol.uppercase()) {
            "ASTER", "ASTR" -> "Astar"
            else -> name
        }
    }
}