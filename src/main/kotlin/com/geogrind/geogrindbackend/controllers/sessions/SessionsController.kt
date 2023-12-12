package com.geogrind.geogrindbackend.controllers.sessions

import com.geogrind.geogrindbackend.dto.session.CreateSessionDto
import com.geogrind.geogrindbackend.dto.session.SuccessSessionResponse
import com.geogrind.geogrindbackend.dto.session.UpdateSessionByIdDto
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Sessions", description = "Session REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/sessions"])
interface SessionsController {

    @GetMapping(path = ["/get_all_sessions"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Find all user current sessions exist in the database",
        operationId = "findAllUserSessions",
        description = "Find all current user sessions"
    )
    suspend fun getAllCurrentSessions(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<List<SuccessSessionResponse>>

    @GetMapping(path = ["/get_session"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get current session by id",
        operationId = "getCurrentSessionById",
        description = "Get current session by a given user account id"
    )
    suspend fun getCurrentSessionByUserAccountId(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<SuccessSessionResponse>

    @PostMapping(path = ["/create_session"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Create new session",
        operationId = "createSession",
        description = "Create new session for user"
    )
    suspend fun createSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @Valid
        @RequestBody
        createSessionDto: CreateSessionDto
    ) : ResponseEntity<SuccessSessionResponse>

    @PatchMapping(path = ["/update_session"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "PATCH",
        summary = "Update current session by id",
        operationId = "updateSessionByUserAccountId",
        description = "Update current session by id"
    )
    suspend fun updateSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @Valid
        @RequestBody
        updateSessionByIdDto: UpdateSessionByIdDto
    ) : ResponseEntity<SuccessSessionResponse>

    @DeleteMapping(path = ["/delete_session"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete session",
        operationId = "deleteSession",
        description = "Delete session"
    )
    fun deleteSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) : ResponseEntity<Cookie>
}