package com.example.webfluxkotlin.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper =
        builder
            .modules(kotlinModule())
            .build()
}