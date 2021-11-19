package com.example.webfluxkotlin.resource

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Greeting(val message: String, val myAge: Int) {
    companion object {
        fun of(message: String, myAge: Int) = Greeting(message, myAge)
    }
}