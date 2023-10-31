package com.geogrind.geogrindbackend.advice.registration

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountBadRequestException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountUnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class UserAccountUnauthorizedAdvice {

    @ExceptionHandler(UserAccountBadRequestException::class)
    fun userUnauthorizedHandler(ex: UserAccountUnauthorizedException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                ex.message
            )
    }
}