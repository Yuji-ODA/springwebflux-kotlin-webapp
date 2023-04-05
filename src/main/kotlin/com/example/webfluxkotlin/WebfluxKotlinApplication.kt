package com.example.webfluxkotlin

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "APIs", version = "1.0", description = "Documentation APIs v1.0"))
class WebfluxKotlinApplication

fun main(args: Array<String>) {
	runApplication<WebfluxKotlinApplication>(*args)
}
