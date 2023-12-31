package com.geogrind.geogrindbackend.advice.user_account

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNewPasswordNotValidException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice
class UserAccountNewPasswordNotValidAdvice {
    @ResponseBody
    @ExceptionHandler(UserAccountNewPasswordNotValidException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun newPasswordConflictHandler(ex: UserAccountNewPasswordNotValidException): String {
        return ex.message ?: "Update password resulted in conflict!"
    }
}