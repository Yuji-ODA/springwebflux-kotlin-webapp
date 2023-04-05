package com.example.webfluxkotlin.controller

import com.example.webfluxkotlin.controller.form.PersonRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("person")
class PersonController {

    @GetMapping
    fun person(@Valid person: PersonRequest, bindingResult: BindingResult) =
        if (bindingResult.hasErrors())
            ResponseEntity.badRequest().body(
                bindingResult.fieldErrors
                    .joinToString(", ") { error -> "${error.field}: ${error.defaultMessage}" }
            )
        else ResponseEntity.ok().body("${person.name}さんは${person.age}ちゃいです")
}