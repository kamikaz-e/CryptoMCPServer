package com.crypto.mcp.routes

import dev.kamikaze.crypto.mcp.models.ChatResponse
import com.crypto.mcp.services.ChatProcessor
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.summaryRoute(chatProcessor: ChatProcessor) {
    get("/summary") {
        val period = call.request.queryParameters["period"] ?: "1h"

        val items = chatProcessor.getSummary()
        call.respond(ChatResponse(items = items))
    }
}