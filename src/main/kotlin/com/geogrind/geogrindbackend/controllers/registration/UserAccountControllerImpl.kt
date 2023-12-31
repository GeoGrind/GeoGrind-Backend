package com.geogrind.geogrindbackend.controllers.registration

import com.geogrind.geogrindbackend.dto.registration.*
import com.geogrind.geogrindbackend.dto.sendgrid.DeleteUserAccountConfirmationDto
import com.geogrind.geogrindbackend.dto.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.dto.sendgrid.UpdatePasswordConfirmationDto
import com.geogrind.geogrindbackend.dto.sendgrid.VerifyEmailUserAccountDto
import com.geogrind.geogrindbackend.models.user_account.toSuccessHttpResponse
import com.geogrind.geogrindbackend.models.user_account.toSuccessHttpResponseList
import com.geogrind.geogrindbackend.services.registration.UserAccountService
import com.geogrind.geogrindbackend.services.registration.UserAccountServiceImpl
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.Instant

@Tag(name = "UserAccount", description = "User Account REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/user_account/"])
@CrossOrigin(origins = ["http://localhost:5173"])
class UserAccountControllerImpl @Autowired constructor(
    private val userAccountService: UserAccountService
) : UserAccountController {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    @GetMapping(path = ["/all"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user accounts exist in the database",
        operationId = "findAllUserAccounts",
        description = "Find all user accounts"
    )
    override suspend fun getAllUserAccounts(): ResponseEntity<List<SuccessUserAccountResponse>> = withTimeout(timeOutMillis) {
        // get all the user accounts
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.getAllUserAccounts().toSuccessHttpResponseList()
            )
            .also { log.info("Successfully get all user accounts: $it") }
    }

    @GetMapping(path = ["/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get user account by id",
        operationId = "getUserAccountById",
        description = "Get user account by given id"
    )
    override suspend fun getUserAccountById(@PathVariable(required = true) user_id: String): ResponseEntity<SuccessUserAccountResponse> = withTimeout(
        timeOutMillis) {
        // get the user account by id
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.getUserAccountById(requestDto = GetUserAccountByIdDto(UUID.fromString(user_id))).toSuccessHttpResponse()
            )
            .also { log.info("Successfully get the user account with ${user_id}: $it") }
    }

    @PostMapping(path = ["/register"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Create new user account",
        operationId = "createUserAccount",
        description = "Create new user account for user"
    )
    override suspend fun createUserAccount(
        @Valid
        @RequestBody
        req: CreateUserAccountDto) : ResponseEntity<SendGridResponseDto> = withTimeout(
        timeOutMillis) {
        // create a new user
        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.createUserAccount(requestDto = CreateUserAccountDto(
                    email = req.email,
                    username = req.username,
                    password = req.password,
                    confirm_password = req.confirm_password,
                ))
            )
            .also { log.info("Send the confirmation email successfully: $it") }
    }

    @PatchMapping(path = ["/change_password/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "PATCH",
        summary = "Update user account's password",
        operationId = "updateUserAccount",
        description = "Update user account's password"
    )
    override suspend fun updateUserAccountById(@PathVariable(required = true) user_id: String, @Valid @RequestBody req: UpdateUserAccountDto): ResponseEntity<SendGridResponseDto> = withTimeout(
        timeOutMillis) {
        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.updateUserAccountById(
                    user_id = UUID.fromString(user_id),
                    requestDto = UpdateUserAccountDto(
                        update_password = req.update_password,
                        confirm_update_password = req.confirm_update_password,
                    )
                )
            )
            .also { log.info("Send the confirmation email successfully: $it") }
    }

    @DeleteMapping(path = ["/delete_account/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete user account",
        operationId = "deleteUserAccount",
        description = "Delete user account"
    )
    override suspend fun deleteUserAccount(@PathVariable(required = true) user_id: String): ResponseEntity<SendGridResponseDto> = withTimeout(timeOutMillis) {
        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.deleteUserAccountById(
                    requestDto = DeleteUserAccountDto(
                        user_id = UUID.fromString(user_id)
                    )
                )
            )
            .also { log.info("Send the confirmation email successfully: $it") }
    }

    // send the email confirmation
    @GetMapping(path = ["/confirm-email/{token}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Verify user account",
        operationId = "verifyUserEmail",
        description = "Verify user email"
    )
    override suspend fun verifyUserEmail(@PathVariable(required = true) token: String): ResponseEntity<SuccessUserAccountResponse> = withTimeout(
        timeOutMillis) {

        // decode the jwt token
        val decoded_token: Claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

        // check if the expiration time is more than the current time
        val exp_timestamp = decoded_token["exp"] as Long
        val exp_time = Instant.ofEpochSecond(exp_timestamp)
        val current_time = Instant.now()

        if(current_time.isAfter(exp_time)) {
            throw ExpiredJwtException(null, decoded_token, "The token provided has expired!")
        }

        val user_id: String = decoded_token["user_id"] as String

        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.getEmailVerification(
                    VerifyEmailUserAccountDto(
                        user_account_id = UUID.fromString(user_id),
                        token = token
                    )
                ).toSuccessHttpResponse()
            )
            .also { log.info("Successfully register the user's account with the system: $it") }
    }

    @GetMapping(path = ["/confirm-password-change/{token}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Update password confirm",
        operationId = "updatePasswordConfirmation",
        description = "Update password confirmation"
    )
    override suspend fun updatePasswordConfirmation(@PathVariable(required = true) token: String): ResponseEntity<SuccessUserAccountResponse> = withTimeout(
        timeOutMillis) {
        val decoded_token: Claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

        // check if the token is still valid
        val exp_timestamp = decoded_token["exp"] as Long
        val exp_time = Instant.ofEpochSecond(exp_timestamp)
        val current_time = Instant.now()

        if(current_time.isAfter(exp_time)) {
            throw ExpiredJwtException(null, decoded_token, "The token provided has expired!")
        }

        val user_id: String = decoded_token["user_id"] as String
        val new_password: String = decoded_token["new_password"] as String

        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.getConfirmPasswordChangeVerification(
                    UpdatePasswordConfirmationDto(
                        user_account_id = UUID.fromString(user_id),
                        new_password = new_password,
                        token = token,
                    )
                ).toSuccessHttpResponse()
            )
            .also { log.info("Successfully update the user's password with the system, $it") }
    }

    @GetMapping(path = ["/confirm-account-deletion/{token}"])
    @Operation(
        method = "GET",
        summary = "Delete user account confirm",
        operationId = "deleteAccountConfirmation",
        description = "Delete user account confirmation"
    )
    override suspend fun deleteAccountConfirmation(@PathVariable(required = true) token: String): ResponseEntity<Unit> = withTimeout(
        timeOutMillis) {
        // decode the jwt token
        val decoded_token: Claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

        // check if the token is still valid
        val exp_timestamp = decoded_token["exp"] as Long
        val exp_time = Instant.ofEpochSecond(exp_timestamp)
        val current_time = Instant.now()

        if(current_time.isAfter(exp_time)) {
            throw ExpiredJwtException(null, decoded_token, "The token provided has expired!")
        }

        val user_id: String = decoded_token["user_id"] as String

        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userAccountService.getDeleteAccountVerification(
                    DeleteUserAccountConfirmationDto(
                        user_account_id = UUID.fromString(user_id),
                        token = token
                    )
                )
            )
            .also { log.info("Successfully delete the user's account with the system, $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserAccountController::class.java)
        private const val timeOutMillis = 5000L
    }
}