package com.example.webfluxkotlin.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Collectors

@Component
class ExtractHandler: HandlerFunction<ServerResponse> {
    override fun handle(request: ServerRequest): Mono<ServerResponse> {
        val sets = 2

        val maybeSectionIdList = request.queryParam("s")
            .map { it.split(",") }
            .map { it.distinct() }
            .map { it.map(Integer::valueOf) }
            .map { it.filter { sectionId -> sectionId in 1 until (1 shl sets) } }
            .flatMap { if (it.isEmpty()) Optional.empty() else Optional.of(it) }

        return maybeSectionIdList.map { sectionIdList ->
            val maxSections = (1 shl sets) - 1

            val fluxSuppliers: Flux<Supplier<Flux<String>>> = Flux.just(
                Supplier { Flux.just("hoge", "huga", "foo") },
                Supplier { Flux.just("huga", "foo", "bar") }
            )

            val fixSizedParallel = Schedulers.newParallel("my scheduler", 2)

            val limits = listOf(3L, 2L)

            val result = if (sectionIdList.size == maxSections) {
                fluxSuppliers
                    .zipWithIterable(limits)
                    .flatMap({ pair ->
                        pair.mapT1 { it.get().take(pair.t2) }
                            .t1
                            .subscribeOn(fixSizedParallel)
                    }, 2)
                    .distinct()
            } else {
                fluxSuppliers
                    .zipWithIterable(judgeRequired(sectionIdList))
                    .map { pair -> if (pair.t2) pair.t1 else Supplier { Flux.empty() } }
                    .zipWithIterable(limits)
                    .flatMapSequential({ pair ->
                        pair.mapT1 { it.get().take(pair.t2) }
                            .t1
                            .collect(Collectors.toSet())
                            .subscribeOn(fixSizedParallel)
                            .log()
                        }, 2
                    )
                    .collectList()
                    .flatMapMany(extractingBy(sectionIdList))
            }

            ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result.map { it + "\n" }, String::class.java)

        }.orElse(
            ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(""))
        )
    }

    private fun judgeRequired(sectionIdList: List<Int>): List<Boolean> {
        val size = 2
        val range = 1..size

        val filterList: List<List<Int>> = listOf(listOf(1, 3), listOf(2, 3))

        return if (sectionIdList.size != 2) {
            range.map { true }
        } else if (filterList.any(sectionIdList::containsAll)) {
            val mask = filterList.find(sectionIdList::containsAll)?.reduce(Int::and)!!
            range.map { it and mask != 0 }
        } else {
            range.map { true }
        }
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
