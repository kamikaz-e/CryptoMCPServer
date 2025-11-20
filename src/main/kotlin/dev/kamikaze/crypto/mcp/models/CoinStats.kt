package dev.kamikaze.crypto.mcp.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinStatsCoinsResponse(
    val result: List<CoinStatsCoin>
)

@Serializable
data class CoinStatsCoin(
    val id: String,
    val icon: String? = null,
    val name: String,
    @SerialName("symbol")
    val ticker: String,
    val rank: Int,
    val price: Double,
    val priceBtc: Double? = null,
    @SerialName("volume") val volume24h: Double? = null,
    val marketCap: Double? = null,
    val availableSupply: Double? = null,
    val totalSupply: Double? = null,
    val fullyDilutedValuation: Double? = null,
    @SerialName("priceChange1h") val priceChange1h: Double? = null,
    @SerialName("priceChange1d") val priceChange1d: Double? = null,
    @SerialName("priceChange1w") val priceChange1w: Double? = null,
    val websiteUrl: String? = null,
    val redditUrl: String? = null,
    val twitterUrl: String? = null,
    val contractAddress: String? = null,
    val contractAddresses: List<ContractAddress>? = null,
    val decimals: Int? = null,
    val explorers: List<String>? = null,
    val liquidityScore: Double? = null,
    val volatilityScore: Double? = null,
    val marketCapScore: Double? = null,
    val riskScore: Double? = null,
    val avgChange: Double? = null
)

@Serializable
data class ContractAddress(
    val blockchain: String,
    val contractAddress: String
)

@Serializable
data class CoinStatsNewsResponse(
    val result: List<CoinStatsNews>
)

@Serializable
data class CoinStatsNews(
    val id: String,
    val title: String,
    val description: String? = null,
    val source: String,
    val link: String,
    val imgUrl: String? = null,
    @SerialName("feedDate") val feedDate: Long
)

@Serializable
data class FearGreedData(
    val name: String = "Fear and Greed Index",
    val now: PeriodData,
    val yesterday: PeriodData,
    val lastWeek: PeriodData
)

@Serializable
data class PeriodData(
    val value: Int,
    @SerialName("value_classification")
    val valueClassification: String,
    val timestamp: Long,
    @SerialName("update_time")
    val updateTime: String? = null
)