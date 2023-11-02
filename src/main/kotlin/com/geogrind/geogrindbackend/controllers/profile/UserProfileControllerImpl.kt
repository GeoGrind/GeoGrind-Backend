package com.geogrind.geogrindbackend.controllers.profile

import com.geogrind.geogrindbackend.dto.profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.profile.SuccessUserProfileResponse
import com.geogrind.geogrindbackend.dto.profile.UpdateUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.models.user_profile.toSuccessHttpResponse
import com.geogrind.geogrindbackend.models.user_profile.toSuccessHttpResponseList
import com.geogrind.geogrindbackend.services.profile.UserProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "UserProfile", description = "User Profile REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/user_profile"])
class UserProfileControllerImpl @Autowired constructor(
    private val userProfileService: UserProfileService
) : UserProfileController {

    @GetMapping(path = ["/all"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user profiles exist in the database",
        operationId = "findAllUserProfiles",
        description = "Find all user profiles"
    )
    override suspend fun getAllUserProfiles(): ResponseEntity<List<SuccessUserProfileResponse>> = withTimeout(timeOutMillis) {
        // get all user profiles
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userProfileService.getAllUserProfile().toSuccessHttpResponseList()
            )
            .also { log.info("Successfully get all user accounts: $it") }
    }

    @GetMapping(path = ["/{user_account_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get user profile by id",
        operationId = "getUserProfileById",
        description = "Get user profile by given user account id"
    )
    override suspend fun getUserProfileByUserAccountId(
        @PathVariable(required = true) user_account_id: String
    ): ResponseEntity<SuccessUserProfileResponse> = withTimeout(timeOutMillis) {
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userProfileService.getUserProfileByUserAccountId(
                    requestDto = GetUserProfileByUserAccountIdDto(
                        user_account_id = UUID.fromString(user_account_id),
                    )
                ).toSuccessHttpResponse()
            )
            .also { log.info("Successfully get the user profile with ${user_account_id}: $it") }
    }

    @PatchMapping(path = ["/update_profile/{user_account_id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "PATCH",
        summary = "Update user profile by id",
        operationId = "updateUserProfileById",
        description = "Update user profile by given user account id"
    )
    override suspend fun updateUserProfileByUserAccountId(
        @PathVariable(required = true) user_account_id: String,
        @Valid
        @RequestBody
        updateUserProfileDto: UpdateUserProfileByUserAccountIdDto
    ): ResponseEntity<SuccessUserProfileResponse> = withTimeout(timeOutMillis) {
        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userProfileService.updateUserProfileByUserAccountId(
                    user_account_id = UUID.fromString(user_account_id),
                    requestDto = UpdateUserProfileByUserAccountIdDto(
                        username = updateUserProfileDto.username,
                        emoji = updateUserProfileDto.emoji,
                        program = updateUserProfileDto.program,
                        year_of_graduation = updateUserProfileDto.year_of_graduation,
                        university = updateUserProfileDto.university
                    )
                ).toSuccessHttpResponse()
            )
            .also { log.info("Successfully update the user profile with user_id: ${user_account_id}: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserProfileControllerImpl::class.java)
        private const val timeOutMillis = 5000L
    }
}