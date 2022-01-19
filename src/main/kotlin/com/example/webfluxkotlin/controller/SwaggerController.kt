package com.example.webfluxkotlin.controller

import com.example.webfluxkotlin.resource.Greeting
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("swagger-test")
class SwaggerController {

    @Operation(summary = "これはすわがーのお試しです。")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "挨拶",
            content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = Greeting::class))
            ]
        )
    ])
    @GetMapping
    fun swaggerTest() = Greeting("すわがーのてすとです。", 109)
}