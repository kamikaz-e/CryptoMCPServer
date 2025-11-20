package dev.kamikaze.crypto.mcp.plugins

import dev.kamikaze.crypto.mcp.routes.chatRoute
import dev.kamikaze.crypto.mcp.routes.summaryRoute
import dev.kamikaze.crypto.mcp.routes.toolsRoute
import dev.kamikaze.crypto.mcp.services.CoinStatsService
import dev.kamikaze.crypto.mcp.services.ChatProcessor
import dev.kamikaze.crypto.mcp.services.PriceTracker
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