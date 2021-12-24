package com.example.webfluxkotlin.config

import com.fasterxml.jackson.databind.Module
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfig {

    @Bean
    fun kotlinModule(): Module = com.fasterxml.jackson.module.kotlin.kotlinModule()
}
