package com.example.webfluxkotlin.router

import com.example.webfluxkotlin.handler.ExtractHandler
import com.example.webfluxkotlin.handler.GreetingHandler
import com.example.webfluxkotlin.handler.RootHandler
import org.springdoc.core.fn.builders.apiresponse.Builder
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunctions

@Configuration(proxyBeanMethods = false)
class Routers {

    @Bean
    fun router(rootHandler: RootHandler) = SpringdocRouteBuilder.route()
            .GET("/", accept(MediaType.TEXT_HTML), rootHandler) {
                it.operationId("root")
                    .summary("こんばんにゃの人")
                    .description("こんばんにゃの人が語りかけます。")
                    .response(Builder.responseBuilder().responseCode("200").description("normal"))
            }.build()

    @Bean
    fun greeting(greetingHandler: GreetingHandler) = RouterFunctions.route(
        GET("/hello").and(accept(MediaType.APPLICATION_JSON)),
        greetingHandler
    )

    @Bean
    fun extract(extractHandler: ExtractHandler) = RouterFunctions.route(
        GET("/extract").and(accept(MediaType.TEXT_PLAIN)),
        extractHandler
    )
}
