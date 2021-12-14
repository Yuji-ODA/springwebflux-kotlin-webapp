package com.example.webfluxkotlin.lib

import reactor.core.publisher.Mono
import java.util.stream.Stream

object FunctionalUtils {

    fun <T, R> traverse(stream: Stream<T>, mapper: (T) -> Mono<R>): Mono<Stream<R>> =
        stream.map(mapper)
            .reduce(Mono.just(Stream.empty()), liftMono2(::cons), liftMono2 { s1, s2 -> Stream.concat(s1, s2) })

    fun <T> sequence(stream: Stream<Mono<T>>): Mono<Stream<T>> = traverse(stream) { it }

    fun <T, R> liftMono(f: (T) -> R): (Mono<T>) -> Mono<R> = { it.map(f) }

    inline fun <T1, T2, R> liftMono2(crossinline f: (T1, T2) -> R): (Mono<T1>, Mono<T2>) -> Mono<R> =
        { mt1, mt2 -> mt1.flatMap { t1 -> mt2.map { f(t1, it) } } }

    inline fun <T1, T2, R> map2(m1: Mono<T1>, m2: Mono<T2>, crossinline f: (T1, T2) -> R): Mono<R> =
        liftMono2(f)(m1, m2)

    inline fun <T1, T2, T3, R> liftMono3(crossinline f: (T1, T2, T3) -> R): (Mono<T1>, Mono<T2>, Mono<T3>) -> Mono<R> =
        { mt1, mt2, mt3 -> mt1.flatMap { t1 -> map2(mt2, mt3) { t2, t3 -> f(t1, t2, t3) } } }

    inline fun <T1, T2, T3, R> map3(m1: Mono<T1>, m2: Mono<T2>, m3: Mono<T3>, crossinline f: (T1, T2, T3) -> R): Mono<R> =
        liftMono3(f)(m1, m2, m3)

    fun <T> cons(stream: Stream<out T>, t: T): Stream<T> = Stream.concat(stream, Stream.of(t))
 }
