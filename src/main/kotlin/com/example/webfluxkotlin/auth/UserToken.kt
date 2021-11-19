package com.example.webfluxkotlin.auth

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class UserToken(val username: String, autorities: Collection<GrantedAuthority>)
    : AbstractAuthenticationToken(autorities) {

    override fun getCredentials() = Unit
    override fun getPrincipal() = username
}