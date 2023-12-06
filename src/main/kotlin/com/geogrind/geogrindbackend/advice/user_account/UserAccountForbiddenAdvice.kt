package com.geogrind.geogrindbackend.advice.user_account

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountForbiddenException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(2)
@ControllerAdvice
class UserAccountForbiddenAdvice {
    @ExceptionHandler(UserAccountForbiddenException::class)
    fun userForbiddenHandler(ex: UserAccountForbiddenException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                ex.message
            )
    }
}