package com.example.webfluxkotlin.controller.form

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

data class PersonRequest(@get:NotBlank val name: String, @get:PositiveOrZero val age: Int)
