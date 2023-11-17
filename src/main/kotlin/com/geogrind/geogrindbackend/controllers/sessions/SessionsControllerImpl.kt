package com.geogrind.geogrindbackend.controllers.sessions

import com.geogrind.geogrindbackend.dto.session.*
import com.geogrind.geogrindbackend.models.sessions.toSuccessHttpResponse
import com.geogrind.geogrindbackend.models.sessions.toSuccessHttpResponseList
import com.geogrind.geogrindbackend.services.sessions.SessionService
import com.geogrind.geogrindbackend.utils.Middleware.JwtAuthenticationFilterImpl
import io.jsonwebtoken.Claims
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Sessions", description = "Session REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/sessions"])
class SessionsControllerImpl(
    private val sessionService: SessionService,
    private val jwtTokenMiddleWare: JwtAuthenticationFilterImpl,
) : SessionsController {
    @GetMapping(path = ["/get_all_sessions"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user current sessions exist in the database",
        operationId = "findAllUserSessions",
        description = "Find all current user sessions"
    )
    override suspend fun getAllCurrentSessions(): ResponseEntity<List<SuccessSessionResponse>> = withTimeout(
        timeOutMillis) {
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
        request: HttpServletRequest
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

        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                sessionService.createSession(
                    requestDto = CreateSessionDto(
                        userAccountId = UUID.fromString(userAccountId),
                        course = createSessionDto.course,
                        startTime = createSessionDto.startTime,
                        duration = createSessionDto.duration,
                        numberOfLikers = createSessionDto.numberOfLikers,
                        description = createSessionDto.description,
                    )
                ).toSuccessHttpResponse()
            )
            .also { log.info("Successfully create a session for the current user!") }
    }

    override suspend fun updateSession(
        request: HttpServletRequest,
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

        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                sessionService.updateSessionById(
                    requestDto = UpdateSessionByIdDto(
                        userAccountId = UUID.fromString(userAccountId),
                        updateCourse = updateSessionByIdDto.updateCourse,
                        updateStartTime = updateSessionByIdDto.updateStartTime,
                        updateDuration = updateSessionByIdDto.updateDuration,
                        updateNumberOfLikers = updateSessionByIdDto.updateNumberOfLikers,
                        updateDescription = updateSessionByIdDto.updateDescription,
                    )
                ).toSuccessHttpResponse()
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
    override suspend fun deleteSession(
        request: HttpServletRequest
    ) = withTimeout(timeOutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decodedToken: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!,
        )

        val userAccountId = decodedToken["user_id"] as String

        ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                sessionService.deleteSessionById(
                    requestDto = DeleteSessionByIdDto(
                        userAccountId = UUID.fromString(userAccountId)
                    )
                )
            )
            .also { log.info("Successfully delete the session from the user profile: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionsController::class.java)
        private const val timeOutMillis = 5000L
    }
}