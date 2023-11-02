package com.geogrind.geogrindbackend.controllers.registration

import com.geogrind.geogrindbackend.dto.registration.CreateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.dto.registration.SuccessUserAccountResponse
import com.geogrind.geogrindbackend.dto.registration.UpdateUserAccountDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "UserAccount", description = "User Account REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/user_account/"])
interface UserAccountController {

    @GetMapping(path = ["/all"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user accounts exist in the database",
        operationId = "findAllUserAccounts",
        description = "Find all user accounts"
    )
    suspend fun getAllUserAccounts(): ResponseEntity<List<SuccessUserAccountResponse>>


    @GetMapping(path = ["/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get user account by id",
        operationId = "getUserAccountById",
        description = "Get user account by given id"
    )
    suspend fun getUserAccountById(@PathVariable(required = true) user_id: String): ResponseEntity<SuccessUserAccountResponse>

    @PostMapping(path = ["/register"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Create new user account",
        operationId = "createUserAccount",
        description = "Create new user account for user"
    )
    suspend fun createUserAccount(@Valid @RequestBody req: CreateUserAccountDto): ResponseEntity<SendGridResponseDto>

    @PatchMapping(path = ["/change_password/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "PATCH",
        summary = "Update user account's password",
        operationId = "updateUserAccount",
        description = "Update user account's password"
    )
    suspend fun updateUserAccountById(@PathVariable(required = true) user_id: String, @Valid @RequestBody updateUserAccountDto: UpdateUserAccountDto): ResponseEntity<SendGridResponseDto>

    @DeleteMapping(path = ["/delete_account/{user_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete user account",
        operationId = "deleteUserAccount",
        description = "Delete user account"
    )
    suspend fun deleteUserAccount(@PathVariable(required = true) user_id: String): ResponseEntity<SendGridResponseDto>


    // verify the email address
    @GetMapping(path = ["/confirm-email/{token}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Verify user account",
        operationId = "verifyUserEmail",
        description = "Verify user email"
    )
    suspend fun verifyUserEmail(@PathVariable(required = true) token: String): ResponseEntity<SuccessUserAccountResponse>

    @GetMapping(path = ["/confirm-password-change/{token}"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Update password confirm",
        operationId = "updatePasswordConfirmation",
        description = "Update password confirmation"
    )
    suspend fun updatePasswordConfirmation(@PathVariable(required = true) token: String): ResponseEntity<SuccessUserAccountResponse>

    @GetMapping(path = ["/confirm-account-deletion/{token}"])
    @Operation(
        method = "DELETE",
        summary = "Delete user account confirm",
        operationId = "deleteAccountConfirmation",
        description = "Delete user account confirmation"
    )
    suspend fun deleteAccountConfirmation(@PathVariable(required = true) token: String): ResponseEntity<Unit>
}