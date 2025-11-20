package dev.kamikaze.crypto.mcp.models

import kotlinx.serialization.Serializable

@Serializable
data class ToolsResponse(
    val tools: List<ToolItem>
)

@Serializable
data class ToolItem(
    val id: String,
    val title: String,
    val description: String,
    val sampleQuery: String
)

@Serializable
data class ChatResponse(
    val items: List<ChatResponseItem>
)

@Serializable
data class ChatResponseItem(
    val type: String, // "text", "prices", "fearGreed", "news", "coin"
    val ts: Long = System.currentTimeMillis(),

    // For type = "text"
    val text: String? = null,

    // For type = "prices"
    val base: String? = null,
    val coins: List<CoinPrice>? = null,

    // For type = "fearGreed"
    val now: PeriodData? = null,
    val yesterday: PeriodData? = null,
    val lastWeek: PeriodData? = null,

    // For type = "news"
    val items: List<NewsItem>? = null,

    // For type = "coin"
    val symbol: String? = null,
    val name: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val id: String? = null,
    val rank: Int? = null,
    val price: Double? = null,
    val priceBtc: Double? = null,
    val volume24h: Double? = null,
    val marketCap: Double? = null,
    val availableSupply: Double? = null,
    val totalSupply: Double? = null,
    val fullyDilutedValuation: Double? = null,
    val change1h: Double? = null,
    val change24h: Double? = null,
    val change1w: Double? = null,
    val websiteUrl: String? = null,
    val redditUrl: String? = null,
    val twitterUrl: String? = null,
    val contractAddress: String? = null,
    val contractAddresses: List<ContractAddressInfo>? = null,
    val decimals: Int? = null,
    val explorers: List<String>? = null,
    val liquidityScore: Double? = null,
    val volatilityScore: Double? = null,
    val marketCapScore: Double? = null,
    val riskScore: Double? = null,
    val avgChange: Double? = null
)

@Serializable
data class CoinPrice(
    val symbol: String,
    val name: String?,
    val price: Double,
    val change1hPct: Double?,
    val change1hAbs: Double?
)

@Serializable
data class NewsItem(
    val title: String,
    val source: String?,
    val time: Long,
    val url: String?
)

@Serializable
data class ContractAddressInfo(
    val blockchain: String,
    val contractAddress: String
)