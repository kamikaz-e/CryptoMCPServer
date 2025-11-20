package dev.kamikaze.crypto.mcp.routes

import dev.kamikaze.crypto.mcp.models.ChatResponse
import dev.kamikaze.crypto.mcp.services.ChatProcessor
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.summaryRoute(chatProcessor: ChatProcessor) {
    get("/summary") {
        val items = chatProcessor.getSummary()
        call.respond(ChatResponse(items = items))
    }
}