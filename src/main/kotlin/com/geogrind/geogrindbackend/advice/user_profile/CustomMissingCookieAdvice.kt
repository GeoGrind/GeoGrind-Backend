package com.geogrind.geogrindbackend.advice.user_profile

import com.geogrind.geogrindbackend.exceptions.user_profile.CustomMissingCookieException
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(2)
@ControllerAdvice
class CustomMissingCookieAdvice {
    @ExceptionHandler(CustomMissingCookieException::class)
    fun missingCookieHandler(ex: MissingRequestCookieException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                ex.message
            )
            .also { log.info("The cookie is missing!") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomMissingCookieAdvice::class.java)
    }
}