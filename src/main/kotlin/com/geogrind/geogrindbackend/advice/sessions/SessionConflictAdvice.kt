package com.geogrind.geogrindbackend.advice.sessions

import com.geogrind.geogrindbackend.exceptions.sessions.SessionConflictException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice
class SessionConflictAdvice {
    @ResponseBody
    @ExceptionHandler(SessionConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun sessionConflictHandler(ex: SessionConflictException): String {
        return ex.message ?: "Session has conflicted!"
    }
}