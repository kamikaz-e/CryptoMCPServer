package dev.kamikaze.crypto.mcp.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val message: String,
    val context: ChatContext? = null
)

@Serializable
data class ChatContext(
    val locale: String = "ru-RU",
    val timezone: String = "Europe/Moscow"
)