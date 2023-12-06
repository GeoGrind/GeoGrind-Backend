package com.geogrind.geogrindbackend.advice.sessions

import com.geogrind.geogrindbackend.exceptions.sessions.SessionBadRequestException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice
class SessionsBadRequestAdvice {
    @ResponseBody
    @ExceptionHandler(SessionBadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun sessionBadRequestHandler(ex: SessionBadRequestException): String {
        return ex.message ?: "Bad request with session creation!"
    }
}