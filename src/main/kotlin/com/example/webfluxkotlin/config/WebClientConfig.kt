package com.example.webfluxkotlin.config

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {
    @Bean
    fun webClient(): WebClient {
        val sslContext = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()

        val httpClient = HttpClient.create().secure { it.sslContext(sslContext) }

        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(2 * 1024) }
            .build()

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(exchangeStrategies)
            .build()
    }
}
