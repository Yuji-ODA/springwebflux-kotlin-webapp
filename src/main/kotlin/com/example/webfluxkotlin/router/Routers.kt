package com.example.webfluxkotlin.router

import com.example.webfluxkotlin.handler.GreetingHandler
import com.example.webfluxkotlin.handler.RootHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Configuration(proxyBeanMethods = false)
class Routers {

    @Bean
    fun root(rootHandler: RootHandler) = RouterFunctions.route(
        GET("/").and(accept(MediaType.TEXT_HTML)),
        rootHandler
    )

    @Bean
    fun greeting(greetingHandler: GreetingHandler) = RouterFunctions.route(
        GET("/hello").and(accept(MediaType.APPLICATION_JSON)),
        greetingHandler
    )

    @Bean
    fun lambda() = RouterFunctions.route(
        GET("/lambda").and(accept(MediaType.TEXT_PLAIN))
    ) {
        val setListMono: Mono<MutableList<Set<String>>> = Flux.just(Flux.just("hoge", "huga", "foo"), Flux.just("huga", "foo", "bar"))
            .flatMap { it.collect(Collectors.toSet()) }
            .reduce(mutableListOf()) { acc, set -> acc.apply { add(set) } }

        val extractor: (List<Set<String>>) -> Set<String> = { it.reduce { acc, set -> acc.intersect(set) } }

        val result: Flux<String> = setListMono
            .map(extractor)
            .flatMapMany { Flux.fromIterable(it) }

        ServerResponse.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(result.map { it + "\n" }, String::class.java)
    }
}