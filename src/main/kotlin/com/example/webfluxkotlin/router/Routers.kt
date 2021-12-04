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
        GET("/lambda").and(accept(MediaType.TEXT_PLAIN))) { req ->

        val sectionId = req.queryParam("s").map(Integer::valueOf).orElse(0)

        val result: Flux<String> = Flux.just(Flux.just("hoge", "huga", "foo"), Flux.just("huga", "foo", "bar"))
            .flatMap { it.collect(Collectors.toSet()) }
            .reduce(mutableListOf<Set<String>>()) { acc, set -> acc.apply { add(set) } }
            .flatMapMany(extracting(sectionId))
            .map { it + "\n" }

        ServerResponse.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(result, String::class.java)
    }

    fun <T> extracting(sectionId: Int): (List<Set<T>>) -> Flux<T> = { setList ->
        val set1 = setList[0]
        val set2 = setList[1]

        val predicate: (T) -> Boolean = when(sectionId) {
            1 -> {
                { set1.contains(it) && !set2.contains(it) }
            }
            2 -> {
                { !set1.contains(it) && set2.contains(it)  }
            }
            3 -> {
                { setList.all { set -> set.contains(it) } }
            }
            else -> {
                { false }
            }
        }

        Flux.fromIterable(set1.union(set2))
            .parallel()
            .filter(predicate)
            .sequential()
    }
}
