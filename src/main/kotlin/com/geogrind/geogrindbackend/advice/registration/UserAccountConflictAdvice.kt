package com.geogrind.geogrindbackend.advice.registration

import com.geogrind.geogrindbackend.exceptions.registration.UserAccountConflictException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice
class UserAccountConflictAdvice {
    @ResponseBody
    @ExceptionHandler(UserAccountConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun userConflictHandler(ex: UserAccountConflictException): String {
        return ex.message ?: "User account has conflicted!"
    }
}