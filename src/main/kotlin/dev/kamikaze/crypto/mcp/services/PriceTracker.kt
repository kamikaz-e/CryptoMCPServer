package dev.kamikaze.crypto.mcp.services

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

class PriceTracker(private val coinStatsService: CoinStatsService) {
    private val priceHistory = ConcurrentHashMap<String, MutableList<PriceSnapshot>>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    data class PriceSnapshot(
        val price: Double,
        val timestamp: Long = System.currentTimeMillis()
    )

    init {
        startTracking()
    }

    private fun startTracking() {
        scope.launch {
            while (isActive) {
                trackPrices()
                delay(60_000) // Каждую минуту
            }
        }
    }

    private suspend fun trackPrices() {
        val coins = coinStatsService.getCoins(100)
        coins.forEach { coin ->
            val ticker = normalizeSymbol(coin.ticker)
            priceHistory.getOrPut(ticker) { mutableListOf() }.apply {
                add(PriceSnapshot(coin.price))
                // Храним только последние 120 снимков (2 часа)
                if (size > 120) removeAt(0)
            }
        }
    }

    fun getPriceChange1h(symbol: String): Pair<Double?, Double?>? {
        val normalized = normalizeSymbol(symbol)
        val history = priceHistory[normalized] ?: return null

        if (history.size < 2) return null

        val current = history.last()
        val oneHourAgo = history.findLast {
            current.timestamp - it.timestamp >= 3600_000
        } ?: history.first()

        val absoluteChange = current.price - oneHourAgo.price
        val percentChange = (absoluteChange / oneHourAgo.price) * 100

        return Pair(percentChange, absoluteChange)
    }

    private fun normalizeSymbol(symbol: String): String {
        return symbol.uppercase()
    }
}