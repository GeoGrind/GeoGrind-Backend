package com.geogrind.geogrindbackend.controllers.registration

import com.geogrind.geogrindbackend.dto.registration.*
import com.geogrind.geogrindbackend.models.user_account.toSuccessHttpResponse
import com.geogrind.geogrindbackend.models.user_account.toSuccessHttpResponseList
import com.geogrind.geogrindbackend.services.registration.UserAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@Tag(name = "UserAccount", description = "User Account REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/user_account/"])
class UserAccountControllerImpl(
    private val userAccountService: UserAccountService
) : UserAccountController {

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
    override suspend fun createUserAccount(@Valid @RequestBody req: CreateUserAccountDto) : ResponseEntity<SuccessUserAccountResponse> = withTimeout(
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
                )).toSuccessHttpResponse()
            )
            .also { log.info("Successfully registered the user with the system: $it") }
    }

    @PatchMapping(path = ["/change_password/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "PATCH",
        summary = "Update user account's password",
        operationId = "updateUserAccount",
        description = "Update user account's password"
    )
    override suspend fun updateUserAccountById(@PathVariable(required = true) user_id: String, @Valid @RequestBody req: UpdateUserAccountDto): ResponseEntity<SuccessUserAccountResponse> = withTimeout(
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
                ).toSuccessHttpResponse()
            )
            .also { log.info("Successfully updated the user's password with the system: $it") }
    }

    @DeleteMapping(path = ["/delete_account/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete user account",
        operationId = "deleteUserAccount",
        description = "Delete user account"
    )
    override suspend fun deleteUserAccount(@PathVariable(required = true) user_id: String): ResponseEntity<Unit> = withTimeout(timeOutMillis) {
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
            .also { log.info("Successfully deleted the user account with the system: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserAccountController::class.java)
        private const val timeOutMillis = 5000L
    }
}