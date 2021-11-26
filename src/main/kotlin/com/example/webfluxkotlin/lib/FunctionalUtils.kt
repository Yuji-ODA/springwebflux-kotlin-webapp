package com.example.webfluxkotlin.lib

import reactor.core.publisher.Mono
import java.util.stream.Stream

object FunctionalUtils {

    fun <T> sequence(stream: Stream<Mono<T>>): Mono<Stream<T>> =
        stream.reduce(Mono.just(Stream.empty()),
            liftMono { acc, s -> Stream.concat(acc, Stream.of(s)) },
            liftMono { s1, s2 -> Stream.concat(s1, s2) })

    fun <T, R> liftMono(f: (T) -> R): (Mono<T>) -> Mono<R> = { it.map(f) }

    fun <T1, T2, R> liftMono(f: (T1, T2) -> R): (Mono<T1>, Mono<T2>) -> Mono<R> =
        { mt1, mt2 -> mt1.flatMap { t1 -> liftMono { t2: T2 -> f(t1, t2) }(mt2) } }

    fun <T1, T2, T3, R> liftMono(f: (T1, T2, T3) -> R): (Mono<T1>, Mono<T2>, Mono<T3>) -> Mono<R> =
        { mt1, mt2, mt3 -> mt1.flatMap { t1 -> liftMono { t2: T2, t3: T3 -> f(t1, t2, t3) }(mt2, mt3) } }
}