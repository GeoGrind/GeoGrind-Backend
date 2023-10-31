package com.geogrind.geogrindbackend.advice.registration

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountInternalServerException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice
class UserAccountInternalServerAdvice {
    @ResponseBody
    @ExceptionHandler(UserAccountInternalServerException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerErrorException(ex: UserAccountInternalServerException): String {
        return ex.message ?: "Internal server errors"
    }
}