package com.example.webfluxkotlin.lib

import reactor.core.publisher.Mono
import java.util.stream.Stream

object FunctionalUtils {

    fun <T, R> traverse(stream: Stream<T>, mapper: (T) -> Mono<R>): Mono<Stream<R>> =
        stream.map(mapper)
            .reduce(Mono.fromCallable { Stream.empty() }, lift(::cons), lift { s1, s2 -> Stream.concat(s1, s2) })

    fun <T> sequence(stream: Stream<Mono<T>>): Mono<Stream<T>> = traverse(stream) { it }

    fun <T, R> lift(f: (T) -> R): (Mono<T>) -> Mono<R> = { it.map(f) }

    fun <T, R> apply(mf: Mono<(T) -> R>, mt: Mono<T>): Mono<R> = mf.flatMap { f -> mt.map { f(it) } }

    inline fun <T1, T2, R> map2(m1: Mono<T1>, m2: Mono<T2>, crossinline f: (T1, T2) -> R): Mono<R> =
        m1.flatMap { t1 -> m2.map { f(t1, it) } }

    fun <T1, T2, R> lift(f: (T1, T2) -> R): (Mono<T1>, Mono<T2>) -> Mono<R> =
        { m1, m2 -> map2(m1, m2, f) }

    inline fun <T1, T2, T3, R> map3(mt1: Mono<T1>, mt2: Mono<T2>, mt3: Mono<T3>, crossinline f: (T1, T2, T3) -> R): Mono<R> =
        apply(map2(mt1, mt2) { t1, t2 -> { t3 -> f(t1, t2, t3) } }, mt3)

    fun <T1, T2, T3, R> lift(f: (T1, T2, T3) -> R): (Mono<T1>, Mono<T2>, Mono<T3>) -> Mono<R> =
        { m1, m2, m3 -> map3(m1, m2, m3, f) }

    fun <T> cons(stream: Stream<out T>, t: T): Stream<T> = Stream.concat(stream, Stream.of(t))

    fun <A, B, C> ((B) -> C).compose(before: (A) -> B): (A) -> C = { this(before(it)) }

    fun <A, B, C> ((A) -> B).andThen(after: (B) -> C): (A) -> C = { after(this(it)) }

    fun hoge() {
        val sum = { a: Int, b: Int -> a + b }
        val double = fun(i: Int) = sum(i, i)
        val incr: Int.() -> Int = { this + 1 }
        val decr: Int.() -> Int = { this - 1 }

        1.incr()
        2.decr()

        val incrBeforeDouble = double.compose(incr)
        val incrAfterDouble = double.andThen(incr)
    }
 }
