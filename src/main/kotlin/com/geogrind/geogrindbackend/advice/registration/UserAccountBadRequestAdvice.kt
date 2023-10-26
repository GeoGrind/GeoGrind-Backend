package com.geogrind.geogrindbackend.advice.registration

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountBadRequestException
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
    fun userBadRequestHandler(ex: UserAccountBadRequestException): ResponseEntity<Map<String, String>> {
        val errorMap = ex.errors

        // Customize the response body
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                errorMap
            )
    }
}