package com.crypto.mcp.routes

import dev.kamikaze.crypto.mcp.models.ToolItem
import dev.kamikaze.crypto.mcp.models.ToolsResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.toolsRoute() {
    get("/tools") {
        val tools = listOf(
            ToolItem(
                id = "pricesSummary",
                title = "Курсы основных монет",
                description = "BTC, ETH, SOL, ASTR, HYPE, ZEC; либо все валюты",
                sampleQuery = "Покажи курсы BTC, ETH, SOL, ASTR, HYPE, ZEC"
            ),
            ToolItem(
                id = "fearGreed",
                title = "Индекс страха и жадности",
                description = "Текущий уровень рынка",
                sampleQuery = "Что с индексом страха и жадности?"
            ),
            ToolItem(
                id = "news",
                title = "Крипто‑новости",
                description = "Новости за период (по умолчанию 1 день)",
                sampleQuery = "Новости за последний день"
            ),
            ToolItem(
                id = "coinInfo",
                title = "Инфо по монете",
                description = "Детали по конкретной монете",
                sampleQuery = "Расскажи про ASTER"
            )
        )

        call.respond(ToolsResponse(tools = tools))
    }
}