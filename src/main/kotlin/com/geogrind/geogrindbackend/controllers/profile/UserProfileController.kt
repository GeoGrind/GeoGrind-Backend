package com.geogrind.geogrindbackend.controllers.profile

import com.geogrind.geogrindbackend.dto.profile.SuccessUserProfileResponse
import com.geogrind.geogrindbackend.dto.profile.UpdateUserProfileByUserAccountIdDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "UserProfile", description = "User Profile REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/user_profile"])
interface UserProfileController {

    @GetMapping(path = ["/get_all_profiles"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user profiles exist in the database",
        operationId = "findAllUserProfiles",
        description = "Find all user profiles"
    )
    suspend fun getAllUserProfiles(): ResponseEntity<List<SuccessUserProfileResponse>>

    @GetMapping(path = ["/get_profile"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get user profile by id",
        operationId = "getUserProfileById",
        description = "Get user profile by given user account id"
    )
    suspend fun getUserProfileByUserAccountId(
        request: HttpServletRequest,
    ): ResponseEntity<SuccessUserProfileResponse>

    @PatchMapping(path = ["/update_profile"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "PATCH",
        summary = "Update user profile by id",
        operationId = "updateUserProfileById",
        description = "Update user profile by given user account id"
    )
    suspend fun updateUserProfileByUserAccountId(
        request: HttpServletRequest,
        @Valid
        @RequestBody
        updateUserProfileDto: UpdateUserProfileByUserAccountIdDto
    ): ResponseEntity<SuccessUserProfileResponse>

}