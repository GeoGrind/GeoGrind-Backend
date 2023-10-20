package com.geogrind.geogrindbackend.advice.registration

import com.geogrind.geogrindbackend.exceptions.registration.UserAccountNewPasswordNotValidException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class UserAccountNewPasswordNotValidAdvice {
    @ResponseBody
    @ExceptionHandler(UserAccountNewPasswordNotValidException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun newPasswordConflictHandler(ex: UserAccountNewPasswordNotValidException): String {
        return ex.message ?: "Update password resulted in conflict!"
    }
}