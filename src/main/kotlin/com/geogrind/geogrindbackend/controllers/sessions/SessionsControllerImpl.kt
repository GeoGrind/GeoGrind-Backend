package com.geogrind.geogrindbackend.controllers.sessions

import com.geogrind.geogrindbackend.controllers.profile.UserProfileControllerImpl
import com.geogrind.geogrindbackend.dto.session.*
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.models.sessions.toSuccessHttpResponse
import com.geogrind.geogrindbackend.models.sessions.toSuccessHttpResponseList
import com.geogrind.geogrindbackend.services.sessions.SessionService
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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.HashSet
import java.util.UUID

@Tag(name = "Sessions", description = "Session REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/sessions"])
class SessionsControllerImpl(
    private val sessionService: SessionService,
    private val jwtTokenMiddleWare: JwtAuthenticationFilterImpl,
    private val generateCookieHelper: CreateTokenCookie,
) : SessionsController {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    private val s3BucketName = dotenv["AWS_PFP_BUCKET_NAME"]

    @GetMapping(path = ["/get_all_sessions"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user current sessions exist in the database",
        operationId = "findAllUserSessions",
        description = "Find all current user sessions"
    )
    override suspend fun getAllCurrentSessions(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<List<SuccessSessionResponse>> = withTimeout(
        timeOutMillis) {

        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val userAccountId = decoded_token["user_id"] as String
        val oldPermissionNames = decoded_token["permissionNames"] as ArrayList<String>
        val oldPermissionNamesSet = HashSet<PermissionName>()
        oldPermissionNames.forEach { it
            oldPermissionNamesSet.add(enumValueOf<PermissionName>(it))
        }

        // user is still active when calling this endpoint -> more time in the token
        val newJwtToken: String = generateCookieHelper.generateJwtToken(
            expirationTime = 3600,
            user_id = UUID.fromString(userAccountId),
            permissionNames = oldPermissionNamesSet,
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

        // get all the current sessions
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                sessionService.getAllSessions().toSuccessHttpResponseList()
            )
            .also { log.info("Successfully get all the current sessions: $it") }
    }

    override suspend fun getCurrentSessionByUserAccountId(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<SuccessSessionResponse> = withTimeout(
        timeOutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val userAccountId = decoded_token["user_id"] as String
        val oldPermissionNames = decoded_token["permissionNames"] as ArrayList<String>
        val oldPermissionNamesSet = HashSet<PermissionName>()
        oldPermissionNames.forEach { it
            oldPermissionNamesSet.add(enumValueOf<PermissionName>(it))
        }

        // user is still active when calling this endpoint -> more time in the token
        val newJwtToken: String = generateCookieHelper.generateJwtToken(
            expirationTime = 3600,
            user_id = UUID.fromString(userAccountId),
            permissionNames = oldPermissionNamesSet,
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
                sessionService.getSessionById(
                    requestDto = GetSessionByIdDto(
                        userAccountId = UUID.fromString(userAccountId),
                    )
                ).toSuccessHttpResponse()
            )
            .also { log.info("Successfully get the current session with ${userAccountId}: $it") }
    }

    @PostMapping(path = ["/create_session"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Create new session",
        operationId = "createSession",
        description = "Create new session for user"
    )
    override suspend fun createSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @Valid
        @RequestBody
        createSessionDto: CreateSessionDto
    ): ResponseEntity<SuccessSessionResponse> = withTimeout(timeOutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val userAccountId = decoded_token["user_id"] as String

        // call the helper service
        val serviceResponse: Pair<Sessions, Cookie> = sessionService.createSession(
            requestDto = CreateSessionDto(
                userAccountId = UUID.fromString(userAccountId),
                courseCode = createSessionDto.courseCode,
                startTime = createSessionDto.startTime,
                duration = createSessionDto.duration,
                numberOfLikers = createSessionDto.numberOfLikers,
                description = createSessionDto.description,
            )
        )

        // set the cookie
        response.addCookie(serviceResponse.second)

        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                serviceResponse.first.toSuccessHttpResponse()
            )
            .also { log.info("Successfully create a session for the current user!") }
    }

    override suspend fun updateSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @Valid
        @RequestBody
        updateSessionByIdDto: UpdateSessionByIdDto,
    ): ResponseEntity<SuccessSessionResponse> = withTimeout(timeOutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decodedToken: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!,
        )

        val userAccountId = decodedToken["user_id"] as String

        // call the helper service
        val serviceResponse: Pair<Sessions, Cookie> = sessionService.updateSessionById(
            requestDto = UpdateSessionByIdDto(
                userAccountId = UUID.fromString(userAccountId),
                updateCourseCode = updateSessionByIdDto.updateCourseCode,
                updateStartTime = updateSessionByIdDto.updateStartTime,
                updateDuration = updateSessionByIdDto.updateDuration,
                updateNumberOfLikers = updateSessionByIdDto.updateNumberOfLikers,
                updateDescription = updateSessionByIdDto.updateDescription,
            )
        )

        // add the cookie
        response.addCookie(serviceResponse.second)

        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                serviceResponse.first.toSuccessHttpResponse()
            )
            .also { log.info("Successfully update the session: $it") }
    }

    @DeleteMapping(path = ["/delete_session"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete session",
        operationId = "deleteSession",
        description = "Delete session"
    )
    override fun deleteSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) : ResponseEntity<Cookie> {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decodedToken: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!,
        )

        val userAccountId = decodedToken["user_id"] as String

        val serviceResponse : Cookie = sessionService.deleteSessionById(
            requestDto = DeleteSessionByIdDto(
                userAccountId = UUID.fromString(userAccountId)
            )
        )

        response.addCookie(serviceResponse)

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                serviceResponse
            )
            .also { log.info("Successfully delete the session from the user profile: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionsController::class.java)
        private const val timeOutMillis = 5000L
    }
}