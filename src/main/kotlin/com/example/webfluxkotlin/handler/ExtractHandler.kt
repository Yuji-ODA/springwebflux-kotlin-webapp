package com.example.webfluxkotlin.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream

@Component
class ExtractHandler: HandlerFunction<ServerResponse> {
    override fun handle(request: ServerRequest): Mono<ServerResponse> {
        val maybeSectionIdList = request.queryParam("s")
            .map { it.split(",") }
            .map { it.distinct() }
            .map { it.map(Integer::valueOf) }
            .map { it.filter { sectionId -> sectionId in 1..3 } }
            .flatMap { if (it.isEmpty()) Optional.empty() else Optional.of(it) }

        return maybeSectionIdList.map { sectionIdList ->
            val bitMasks = listOf(1, 2)
            val bits: Int = sectionIdList.reduce(Int::or)
            val sourceRequired: List<Boolean> = bitMasks.map { mask -> bits and mask }
                .map { mask -> mask != 0 }

            val result: Flux<String> = Flux.just(
                    Supplier { Flux.just("hoge", "huga", "foo") },
                    Supplier { Flux.just("huga", "foo", "bar") }
                )
                .zipWithIterable(sourceRequired)
                .map { pair -> if (pair.t2) pair.t1 else Supplier { Flux.empty() } }
                .map { it.get() }
                .flatMap { it.collect(Collectors.toSet()) }
                .reduce(Stream.empty<Set<String>>()) { acc, set -> Stream.concat(acc, Stream.of(set)) }
                .map { it.collect(Collectors.toList()) }
                .flatMapMany(extractingBy(sectionIdList))
                .map { it + "\n" }

            ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result, String::class.java)
        }.orElse(
            ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(""))
        )
    }

    private fun <T> extractingBy(sectionIdList: List<Int>): (List<Set<T>>) -> Flux<T> = {

        val predicate: Predicate<T> = sectionIdList.map(composingPredicate(it[0], it[1]))
            .reduce { p1, p2 -> p1.or(p2) }

        Flux.fromIterable(it.reduce { acc, s -> acc.union(s) })
            .parallel()
            .filter(predicate)
            .sequential()
    }

    private fun <T> composingPredicate(set1: Set<T>, set2: Set<T>): (Int) -> Predicate<T> = { sectionId ->
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
