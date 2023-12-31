package com.geogrind.geogrindbackend.advice.user_account

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountBadRequestException
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(2)
@ControllerAdvice
class UserAccountBadRequestAdvice {
    @ExceptionHandler(UserAccountBadRequestException::class)
    fun userAccountBadRequestHandler(ex: UserAccountBadRequestException): ResponseEntity<Map<String, String>> {
        val errorMap = ex.errors

        // Customize the response body
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                errorMap
            )
            .also { log.info("User registration bad request!") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserAccountBadRequestAdvice::class.java)
    }
}