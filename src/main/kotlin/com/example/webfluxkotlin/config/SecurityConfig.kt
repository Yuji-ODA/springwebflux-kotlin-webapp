package com.example.webfluxkotlin.config

import com.example.webfluxkotlin.auth.MyAuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono


@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .authorizeExchange {
                it.anyExchange().authenticated()
            }
            .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()

    private fun authenticationWebFilter(): AuthenticationWebFilter =
        AuthenticationWebFilter(ReactiveAuthenticationManager { Mono.just(it.apply { isAuthenticated = true }) })
            .apply { setServerAuthenticationConverter(MyAuthenticationConverter()) }
}
