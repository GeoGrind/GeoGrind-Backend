package com.geogrind.geogrindbackend.controllers.login

import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.services.login.LoginAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "LoginAccount", description = "Login Account REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/user_account/"])
class LoginAccountControllerImpl @Autowired constructor(
    private val loginAccountService: LoginAccountService
) : LoginAccountController {

    @PostMapping(path = ["/login"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Login user account",
        operationId = "loginUserAccount",
        description = "Login user account"
    )
    override suspend fun loginUserAccount(@Valid @RequestBody req: UserLoginRequestDto): ResponseEntity<SendGridResponseDto> = withTimeout(timeOutMillis) {
        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                loginAccountService.login(
                    req
                )
            )
            .also { log.info("Send the login confirmation email successfully!") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoginAccountController::class.java)
        private const val timeOutMillis = 5000L
    }
}