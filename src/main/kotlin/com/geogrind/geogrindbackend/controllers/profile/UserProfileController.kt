package com.geogrind.geogrindbackend.controllers.profile

import com.geogrind.geogrindbackend.dto.profile.DeleteCoursesDto
import com.geogrind.geogrindbackend.dto.profile.SuccessUserProfileResponse
import com.geogrind.geogrindbackend.dto.profile.UpdateUserProfileByUserAccountIdDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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
    suspend fun getAllUserProfiles(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<List<SuccessUserProfileResponse>>

    @GetMapping(path = ["/get_profile"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get user profile by id",
        operationId = "getUserProfileById",
        description = "Get user profile by given user account id"
    )
    suspend fun getUserProfileByUserAccountId(
        request: HttpServletRequest,
        response: HttpServletResponse,
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
        response: HttpServletResponse,
        @Valid
        @RequestBody
        updateUserProfileDto: UpdateUserProfileByUserAccountIdDto
    ): ResponseEntity<SuccessUserProfileResponse>

    @DeleteMapping(path = ["/delete_courses"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete the courses from the database",
        operationId = "deleteCourse",
        description = "Delete courses from the user profile"
    )
    suspend fun deleteCoursesFromUserProfiles(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @Valid
        @RequestBody
        requestDto: DeleteCoursesDto
    ) : ResponseEntity<Cookie?>
}