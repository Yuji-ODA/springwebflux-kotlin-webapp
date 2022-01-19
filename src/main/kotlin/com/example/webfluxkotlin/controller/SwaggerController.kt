package com.example.webfluxkotlin.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("swagger-test")
class SwaggerController {
    @GetMapping
    fun swaggerTest() = "すわがーのてすとです。"
}