package com.example.webfluxkotlin.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import java.security.Principal

@Component
class RootHandler {
    fun handleRequest(request: ServerRequest) = ServerResponse.ok()
        .contentType(MediaType.TEXT_HTML)
        .render("root",
            mapOf("title" to "WebFluxの世界へようこそ",
                "user" to request.principal().map(Principal::getName)))
}