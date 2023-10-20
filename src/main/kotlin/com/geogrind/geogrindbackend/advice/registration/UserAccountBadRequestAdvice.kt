package com.geogrind.geogrindbackend.advice.registration

import com.geogrind.geogrindbackend.exceptions.registration.UserAccountBadRequestException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class UserAccountBadRequestAdvice {
    @ResponseBody
    @ExceptionHandler(UserAccountBadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun userBadRequestHandler(ex: UserAccountBadRequestException): String {
        return ex.message ?: "User registration failed with bad request!"
    }
}