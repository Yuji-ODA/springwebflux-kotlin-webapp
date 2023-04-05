package com.example.webfluxkotlin.config

import com.example.webfluxkotlin.auth.MyAuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import reactor.core.publisher.Mono


@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .authorizeExchange {
                it.pathMatchers("/swagger-ui.html", "/v3/api-docs").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .build()

    @Bean
    fun authenticationWebFilter(): AuthenticationWebFilter =
        AuthenticationWebFilter(ReactiveAuthenticationManager { Mono.just(it) })
            .apply { setServerAuthenticationConverter(MyAuthenticationConverter(true, "USER")) }
}
