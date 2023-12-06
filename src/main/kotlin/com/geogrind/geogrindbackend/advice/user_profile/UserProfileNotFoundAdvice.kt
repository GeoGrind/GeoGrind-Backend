package com.geogrind.geogrindbackend.advice.user_profile

import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(2)
@ControllerAdvice
class UserProfileNotFoundAdvice {
    @ExceptionHandler(UserProfileNotFoundException::class)
    fun userProfileNotFoundHandler(ex: UserProfileNotFoundException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                ex.message
            )
    }
}