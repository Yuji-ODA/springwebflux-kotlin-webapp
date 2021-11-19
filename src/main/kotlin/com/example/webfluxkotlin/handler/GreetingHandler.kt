package com.example.webfluxkotlin.handler

import com.example.webfluxkotlin.resource.Greeting
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class GreetingHandler {
    fun handleRequest(request: ServerRequest) = ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(Greeting.of("Hello Webflux!!", 12)))
}