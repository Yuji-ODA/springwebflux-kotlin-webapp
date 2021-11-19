package com.example.webfluxkotlin.router

import com.example.webfluxkotlin.handler.GreetingHandler
import com.example.webfluxkotlin.handler.RootHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunctions

@Configuration(proxyBeanMethods = false)
class Routers {

    @Bean
    fun root(handler: RootHandler) = RouterFunctions.route(
        GET("/").and(accept(MediaType.TEXT_HTML)),
        handler::handleRequest
    )

    @Bean
    fun greeting(handler: GreetingHandler) = RouterFunctions.route(
        GET("/hello").and(accept(MediaType.APPLICATION_JSON)),
        handler::handleRequest
    )
}