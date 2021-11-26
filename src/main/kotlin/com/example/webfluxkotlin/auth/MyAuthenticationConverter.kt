package com.example.webfluxkotlin.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class MyAuthenticationConverter(private val authenticated: Boolean = false, vararg roles: String)
    : ServerAuthenticationConverter {

    private val authorities = roles.map { SimpleGrantedAuthority("ROLE_${it}") }

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val roleToken = exchange.request.headers["Yahoo-Role-Auth"]?.getOrNull(0)
        return Mono.just(
            UserToken("user", authorities)
                .apply { isAuthenticated = authenticated }
        )
    }
}