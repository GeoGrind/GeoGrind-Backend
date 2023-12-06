package com.geogrind.geogrindbackend.advice.sessions

import com.geogrind.geogrindbackend.exceptions.sessions.SessionNotFoundException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice
class SessionNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(SessionNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun sessionNotFoundHandler(ex: SessionNotFoundException) : String {
        return ex.message ?: "Current session not found!"
    }

}