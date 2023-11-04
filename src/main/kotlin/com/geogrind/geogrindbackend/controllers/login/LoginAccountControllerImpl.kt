package com.geogrind.geogrindbackend.controllers.login

import com.geogrind.geogrindbackend.dto.login.ConfirmUserLoginResquestDto
import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.dto.registration.SuccessUserAccountResponse
import com.geogrind.geogrindbackend.dto.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_account.toSuccessHttpResponse
import com.geogrind.geogrindbackend.services.login.LoginAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping(path = ["verify-login/{token}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Confirm user login account",
        operationId = "confirmUserLoginAccount",
        description = "Confirm user login account"
    )
    override suspend fun confirmUserLoginAccount(
        @PathVariable(required = true) token: String,
        response: HttpServletResponse
    ): ResponseEntity<SuccessUserAccountResponse> = withTimeout(timeOutMillis) {
        // generate the new jwt token for the user
        val service_response: Pair<UserAccount, Cookie> = loginAccountService.confirmLoginHandler(
            ConfirmUserLoginResquestDto(
                token = token
            )
        )

        log.info("Cookie in loginAccountController: ${service_response.second}, ${service_response.second.name}, ${service_response.second.path}")

        // inject the cookie into the response
        response.addCookie(service_response.second)

        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                service_response.first.toSuccessHttpResponse()
            )
            .also { log.info("User is successfully logged in!") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoginAccountController::class.java)
        private const val timeOutMillis = 5000L
    }
}