package com.example.webfluxkotlin.router

import com.example.webfluxkotlin.handler.GreetingHandler
import com.example.webfluxkotlin.handler.RootHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

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
    fun extract() = RouterFunctions.route(
        GET("/extract").and(accept(MediaType.TEXT_PLAIN))) { req ->

        val maybeSectionIdList = req.queryParam("s")
            .map { it.split(",") }
            .map { it.distinct() }
            .map { it.map(Integer::valueOf) }
            .map { it.filter { sectionId -> sectionId in 1..3 } }
            .flatMap { if (it.isEmpty()) Optional.empty() else Optional.of(it) }

        if (maybeSectionIdList.isEmpty) {
            ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(fromValue(""))
        } else {
            val result: Flux<String> = Flux.just(Flux.just("hoge", "huga", "foo"), Flux.just("huga", "foo", "bar"))
                .flatMap { it.collect(Collectors.toSet()) }
                .reduce(Stream.empty<Set<String>>()) { acc, set -> Stream.concat(acc, Stream.of(set)) }
                .map { it.collect(Collectors.toList()) }
                .flatMapMany(extracting(maybeSectionIdList.get()))
                .map { it + "\n" }

            ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result, String::class.java)
        }
    }


    private fun <T> extracting(sectionIdList: List<Int>): (List<Set<T>>) -> Flux<T> = {

        val predicate: Predicate<T>  = sectionIdList.map(composing(it[0], it[1]))
            .reduce { p1, p2 -> p1.or(p2) }

        Flux.fromIterable(it.reduce { acc, s -> acc.union(s) })
            .parallel()
            .filter(predicate)
            .sequential()
    }

    private fun <T> composing(set1: Set<T>, set2: Set<T>): (Int) -> Predicate<T> = { sectionId ->
        when (sectionId) {
            1 -> {
                Predicate { set1.contains(it) && !set2.contains(it) }
            }
            2 -> {
                Predicate { !set1.contains(it) && set2.contains(it) }
            }
            3 -> {
                Predicate { listOf(set1, set2).all { set -> set.contains(it) } }
            }
            else -> {
                Predicate { false }
            }
        }
    }
}
