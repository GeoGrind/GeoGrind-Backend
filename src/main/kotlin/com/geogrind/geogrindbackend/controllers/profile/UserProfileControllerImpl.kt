package com.geogrind.geogrindbackend.controllers.profile

import com.geogrind.geogrindbackend.dto.profile.DeleteCoursesDto
import com.geogrind.geogrindbackend.dto.profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.profile.SuccessUserProfileResponse
import com.geogrind.geogrindbackend.dto.profile.UpdateUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.models.user_profile.toSuccessHttpResponse
import com.geogrind.geogrindbackend.models.user_profile.toSuccessHttpResponseList
import com.geogrind.geogrindbackend.services.profile.UserProfileService
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookie
import com.geogrind.geogrindbackend.utils.Middleware.JwtAuthenticationFilterImpl
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Claims
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val userProfileService: UserProfileService,
    private val jwtTokenMiddleWare : JwtAuthenticationFilterImpl,
    private val generateCookieHelper: CreateTokenCookie,
) : UserProfileController {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    private val s3BucketName = dotenv["AWS_PFP_BUCKET_NAME"]

    @GetMapping(path = ["/get_all_profiles"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user profiles exist in the database",
        operationId = "findAllUserProfiles",
        description = "Find all user profiles"
    )
    override suspend fun getAllUserProfiles(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<List<SuccessUserProfileResponse>> = withTimeout(timeOutMillis) {

        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val user_account_id = decoded_token["user_id"] as String
        val oldPermissionNames = decoded_token["permissionNames"] as Set<PermissionName>

        // user is still active when calling this endpoint -> more time in the token
        val newJwtToken: String = generateCookieHelper.generateJwtToken(
            expirationTime = 3600,
            user_id = UUID.fromString(user_account_id),
            permissionNames = oldPermissionNames,
            secret_key = geogrindSecretKey,
            bucketName = s3BucketName,
        )

        val cookie: Cookie = generateCookieHelper.createTokenCookie(
            expirationTime = 3600,
            token = newJwtToken,
        )

        // inject the new cookie with new jwt token
        response.addCookie(cookie)
        log.info("Response: $response")

        // get all user profiles
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                userProfileService.getAllUserProfile().toSuccessHttpResponseList()
            )
            .also { log.info("Successfully get all user accounts: $it") }
    }

    @GetMapping(path = ["/get_profile"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get user profile by id",
        operationId = "getUserProfileById",
        description = "Get user profile by given user account id"
    )
    override suspend fun getUserProfileByUserAccountId(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<SuccessUserProfileResponse> = withTimeout(timeOutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val user_account_id = decoded_token["user_id"] as String
        val oldPermissionNames = decoded_token["permissionNames"] as Set<PermissionName>

        // user is still active when calling this endpoint -> more time in the token
        val newJwtToken: String = generateCookieHelper.generateJwtToken(
            expirationTime = 3600,
            user_id = UUID.fromString(user_account_id),
            permissionNames = oldPermissionNames,
            secret_key = geogrindSecretKey,
            bucketName = s3BucketName,
        )

        val cookie: Cookie = generateCookieHelper.createTokenCookie(
            expirationTime = 3600,
            token = newJwtToken,
        )

        // inject the new cookie with new jwt token
        response.addCookie(cookie)
        log.info("Response: $response")

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

    @PatchMapping(path = ["/update_profile"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "PATCH",
        summary = "Update user profile by id",
        operationId = "updateUserProfileById",
        description = "Update user profile by given user account id"
    )
    override suspend fun updateUserProfileByUserAccountId(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @Valid
        @RequestBody
        updateUserProfileDto: UpdateUserProfileByUserAccountIdDto
    ): ResponseEntity<SuccessUserProfileResponse> = withTimeout(timeOutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val user_account_id = decoded_token["user_id"] as String
        val oldPermissionNames = decoded_token["permissionNames"] as Set<PermissionName>
        log.info(user_account_id)

        val serviceResponse: Pair<UserProfile, Cookie> = userProfileService.updateUserProfileByUserAccountId(
            requestDto = UpdateUserProfileByUserAccountIdDto(
                user_account_id = UUID.fromString(user_account_id),
                username = updateUserProfileDto.username,
                emoji = updateUserProfileDto.emoji,
                program = updateUserProfileDto.program,
                courseCodes = updateUserProfileDto.courseCodes,
                year_of_graduation = updateUserProfileDto.year_of_graduation,
                university = updateUserProfileDto.university
            )
        )

        // inject the new cookie into the response
        response.addCookie(serviceResponse.second)
        log.info("Response: $response")

        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                serviceResponse.first.toSuccessHttpResponse()
            )
            .also { log.info("Successfully update the user profile with user_id: ${user_account_id}: $it") }
    }

    // delete the courses from the user profile
    @DeleteMapping(path = ["/delete_courses"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete the courses from the database",
        operationId = "deleteCourse",
        description = "Delete courses from the user profile"
    )
    override suspend fun deleteCoursesFromUserProfiles(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @Valid
        @RequestBody
        requestDto: DeleteCoursesDto
    ) = withTimeout(timeOutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val user_account_id = decoded_token["user_id"] as String

        val serviceResponse: Cookie? = userProfileService.deleteCourseFromProfile(
            requestDto = DeleteCoursesDto(
                user_account_id = UUID.fromString(user_account_id),
                coursesDelete = requestDto.coursesDelete,
            )
        )

        // inject the cookie into the response
        if(serviceResponse != null) {
            response.addCookie(serviceResponse)
            log.info("Response: $response")
        } else {
            val oldPermissionNames = decoded_token["permissionNames"] as Set<PermissionName>

            // user is still active when calling this endpoint -> more time in the token
            val newJwtToken: String = generateCookieHelper.generateJwtToken(
                expirationTime = 3600,
                user_id = UUID.fromString(user_account_id),
                permissionNames = oldPermissionNames,
                secret_key = geogrindSecretKey,
                bucketName = s3BucketName,
            )

            val cookie: Cookie = generateCookieHelper.createTokenCookie(
                expirationTime = 3600,
                token = newJwtToken,
            )

            // inject the new cookie with new jwt token
            response.addCookie(cookie)
            log.info("Response: $response")
        }

        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                serviceResponse
            )
            .also { log.info("Successfully delete the courses from the user profile: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserProfileControllerImpl::class.java)
        private const val timeOutMillis = 5000L
    }
}