package com.geogrind.geogrindbackend.advice.registration

import com.geogrind.geogrindbackend.exceptions.registration.UserAccountNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class UserAccountNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(UserAccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun userNotFoundHandler(ex: UserAccountNotFoundException): String {
        return ex.message ?: "User account not found!"
    }
}