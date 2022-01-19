package com.example.webfluxkotlin.controller.response


class Response<CONTENT>(val successful: Boolean, val reason: String?, val content: CONTENT?) {

    companion object {
        fun <CONTENT> of(content: CONTENT) = Response(true, null, content)
        fun <CONTENT> failed(reason: String) = Response(false, reason, null)
    }
}