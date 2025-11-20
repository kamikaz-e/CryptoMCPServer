package com.crypto.mcp.routes

import dev.kamikaze.crypto.mcp.models.ChatRequest
import dev.kamikaze.crypto.mcp.models.ChatResponse
import com.crypto.mcp.services.ChatProcessor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.chatRoute(chatProcessor: ChatProcessor) {
    post("/chat") {
        val request = call.receive<ChatRequest>()

        if (request.message.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Message cannot be empty"))
            return@post
        }

        val items = chatProcessor.processMessage(request.message)
        call.respond(ChatResponse(items = items))
    }
}