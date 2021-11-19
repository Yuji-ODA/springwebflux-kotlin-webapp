package com.example.webfluxkotlin.resource

import org.junit.jupiter.api.Test

internal class ParentTest {
    @Test
    fun testWhen() {
        val p1: Parent = Parent.C1(10, 20)
        val p2: Parent = Parent.C2("俺はC2だ")
        val p3: Parent = Parent.C1(0, 100)

        when (val p = arrayOf(p1, p2, p3).random()) {
            is Parent.C1 -> {
                if (p.x == 0) arrayOf(p.x, p.y).joinToString(", ")
                else p.y.toString()
            }
            is Parent.C2 -> p.s
        }.let(::println)
    }
}