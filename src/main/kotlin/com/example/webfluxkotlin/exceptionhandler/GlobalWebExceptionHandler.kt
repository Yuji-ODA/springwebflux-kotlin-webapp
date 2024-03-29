package com.example.webfluxkotlin.exceptionhandler

import jakarta.validation.ConstraintViolationException
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Component
@Order(-2)
class GlobalWebExceptionHandler(
    errorAttributes: ErrorAttributes?,
    webProperties: WebProperties,
    applicationContext: ApplicationContext?,
    configurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(errorAttributes, webProperties.resources, applicationContext) {

    init {
        super.setMessageWriters(configurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?) =
        RouterFunctions.route(RequestPredicates.all()) { req ->
            when (val ex = errorAttributes?.getError(req)) {
                is ConstraintViolationException ->
                    ServerResponse.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(BodyInserters.fromValue(
                            ex.constraintViolations.joinToString(",") {
                                it.invalidValue.toString() + ": " + it.message
                            }))
                else ->
                    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(BodyInserters.fromValue(ex?.localizedMessage ?: "NG"))
            }
        }
}
