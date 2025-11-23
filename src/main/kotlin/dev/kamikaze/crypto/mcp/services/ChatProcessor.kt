package dev.kamikaze.crypto.mcp.services

import dev.kamikaze.crypto.mcp.models.ChatResponseItem
import dev.kamikaze.crypto.mcp.models.CoinPrice
import dev.kamikaze.crypto.mcp.models.ContractAddressInfo
import dev.kamikaze.crypto.mcp.models.NewsItem

class ChatProcessor(
    private val coinStatsService: CoinStatsService,
    private val priceTracker: PriceTracker
) {
    private val targetCoins = listOf("BTC", "ETH", "SOL", "ASTER", "HYPE", "ZEC")

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
                it.ticker.equals(symbol, ignoreCase = true) ||
                it.ticker.equals("ASTER", ignoreCase = true)
            } ?: return@mapNotNull null

            val change = priceTracker.getPriceChange1h(coin.ticker)

            CoinPrice(
                symbol = normalizeSymbol(coin.ticker),
                name = normalizeName(coin.ticker),
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

        // Формируем описание с основной информацией
        val descriptionParts = mutableListOf<String>()
        descriptionParts.add("Ранг: ${coin.rank}")
        if (coin.availableSupply != null && coin.totalSupply != null) {
            descriptionParts.add("В обращении: ${formatNumber(coin.availableSupply)} / ${formatNumber(coin.totalSupply)}")
        }
        if (coin.priceChange1h != null) {
            descriptionParts.add("Изменение за 1ч: ${formatPercent(coin.priceChange1h)}%")
        }
        if (coin.priceChange1d != null) {
            descriptionParts.add("Изменение за 24ч: ${formatPercent(coin.priceChange1d)}%")
        }
        if (coin.priceChange1w != null) {
            descriptionParts.add("Изменение за неделю: ${formatPercent(coin.priceChange1w)}%")
        }

        return listOf(
            ChatResponseItem(
                type = "coin",
                symbol = coin.ticker,
                name = coin.name,
                description = descriptionParts.joinToString(" | "),
                icon = coin.icon,
                id = coin.id,
                rank = coin.rank,
                price = coin.price,
                priceBtc = coin.priceBtc,
                volume24h = coin.volume24h,
                marketCap = coin.marketCap,
                availableSupply = coin.availableSupply,
                totalSupply = coin.totalSupply,
                fullyDilutedValuation = coin.fullyDilutedValuation,
                change1h = coin.priceChange1h,
                change24h = coin.priceChange1d,
                change1w = coin.priceChange1w,
                websiteUrl = coin.websiteUrl,
                redditUrl = coin.redditUrl,
                twitterUrl = coin.twitterUrl,
                contractAddress = coin.contractAddress,
                contractAddresses = coin.contractAddresses?.map {
                    ContractAddressInfo(
                        blockchain = it.blockchain,
                        contractAddress = it.contractAddress
                    )
                },
                decimals = coin.decimals,
                explorers = coin.explorers,
                liquidityScore = coin.liquidityScore,
                volatilityScore = coin.volatilityScore,
                marketCapScore = coin.marketCapScore,
                riskScore = coin.riskScore,
                avgChange = coin.avgChange
            )
        )
    }

    private fun formatNumber(value: Double): String {
        return when {
            value >= 1_000_000_000 -> String.format("%.2fB", value / 1_000_000_000)
            value >= 1_000_000 -> String.format("%.2fM", value / 1_000_000)
            value >= 1_000 -> String.format("%.2fK", value / 1_000)
            else -> String.format("%.2f", value)
        }
    }

    private fun formatPercent(value: Double): String {
        val sign = if (value >= 0) "+" else ""
        return String.format("%s%.2f", sign, value)
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

    private fun normalizeName(symbol: String): String {
        return symbol.uppercase()
    }
}