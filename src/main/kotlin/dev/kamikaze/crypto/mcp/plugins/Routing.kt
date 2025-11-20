package com.crypto.mcp.plugins

import com.crypto.mcp.routes.chatRoute
import com.crypto.mcp.routes.summaryRoute
import com.crypto.mcp.routes.toolsRoute
import com.crypto.mcp.services.CoinStatsService
import com.crypto.mcp.services.ChatProcessor
import com.crypto.mcp.services.PriceTracker
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val coinStatsService = CoinStatsService()
    val priceTracker = PriceTracker(coinStatsService)
    val chatProcessor = ChatProcessor(coinStatsService, priceTracker)

    routing {
        get("/") {
            call.respondText("MCP Crypto Server is running!")
        }

        get("/health") {
            call.respond(mapOf("status" to "ok", "timestamp" to System.currentTimeMillis()))
        }

        toolsRoute()
        chatRoute(chatProcessor)
        summaryRoute(chatProcessor)
    }
}