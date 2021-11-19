package com.example.webfluxkotlin.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class MyAuthenticationConverter: ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> {
        return Mono.just(
            UserToken("user", listOf(SimpleGrantedAuthority("ROLE_USER")))
        )
    }
}