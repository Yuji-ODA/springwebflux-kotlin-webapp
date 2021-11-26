package com.example.webfluxkotlin.auth

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class UserToken(private val username: String, authorities: Collection<GrantedAuthority>)
    : AbstractAuthenticationToken(authorities) {

    override fun getCredentials() = Unit
    override fun getPrincipal() = username
}