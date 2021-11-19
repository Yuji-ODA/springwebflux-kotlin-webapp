package com.example.webfluxkotlin.router

import com.example.webfluxkotlin.resource.Greeting
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingRouterTest {

    @Autowired
    var webTestClient: WebTestClient? = null

    @LocalServerPort
    var port: Int? = null

    @Test
    fun helloTest() {
        println(port)
        webTestClient
            ?.get()?.uri("/hello")
            ?.accept(MediaType.APPLICATION_JSON)
            ?.exchange()
            ?.expectStatus()?.isOk
            ?.expectBody(Greeting::class.java)?.value<WebTestClient.BodySpec<Greeting, *>?> {
                assertThat(it)
                    .hasFieldOrPropertyWithValue("message", "Hello Webflux!!")
                    .hasFieldOrPropertyWithValue("myAge", 12)
            }
    }
}
