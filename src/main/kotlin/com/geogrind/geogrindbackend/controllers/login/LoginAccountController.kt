package com.geogrind.geogrindbackend.controllers.login

import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.SendGridResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "LoginAccount", description = "Login Account REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/user_account/"])
interface LoginAccountController {
    @PostMapping(path = ["/login"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Login user account",
        operationId = "loginUserAccount",
        description = "Login user account"
    )
    suspend fun loginUserAccount(@Valid @RequestBody req: UserLoginRequestDto): ResponseEntity<SendGridResponseDto>
}