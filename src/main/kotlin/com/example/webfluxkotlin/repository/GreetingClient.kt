package com.example.webfluxkotlin.repository

import com.example.webfluxkotlin.resource.Greeting
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient

@Repository
class GreetingClient(val client: WebClient) {

    @Autowired
    constructor(builder: WebClient.Builder): this(builder.baseUrl("http://localhost:8080").build())

    fun getMessage() = client.get()
        .uri("/hello").accept(MediaType.APPLICATION_JSON).retrieve()
        .bodyToMono(Greeting::class.java)
}
