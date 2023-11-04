package com.geogrind.geogrindbackend.advice.user_account

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice
class UserAccountNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(UserAccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun userNotFoundHandler(ex: UserAccountNotFoundException): String {
        return ex.message ?: "User account not found!"
    }
}