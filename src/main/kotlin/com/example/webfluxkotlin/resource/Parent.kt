package com.example.webfluxkotlin.resource

//sealed class Parent
//data class C1(val x: Int, val y: Int): Parent()
//data class C2(val s: String): Parent()

sealed class Parent {
    data class C1(val x: Int, val y: Int): Parent()
    data class C2(val s: String): Parent()
}
