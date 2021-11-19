package com.example.webfluxkotlin

import com.example.webfluxkotlin.config.ObjectMapperConfig
import com.example.webfluxkotlin.resource.Greeting
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig(classes = [JacksonAutoConfiguration::class, ObjectMapperConfig::class])
class JacksonTest {
    @Autowired
    var mapper: ObjectMapper? = null

    @Test
    fun testSerialize() {
        val given = Greeting("おは", 298)

        val actual = mapper?.writeValueAsString(given)

        val expected = "{\"message\":\"おは\",\"my_age\":298}"

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testDeserialize() {
        val given = "{\"message\":\"おは\",\"my_age\":298}"

        val actual = mapper?.readValue(given, Greeting::class.java)

        val expected = Greeting("おは", 298)

        assertThat(actual).isEqualTo(expected)
    }

}