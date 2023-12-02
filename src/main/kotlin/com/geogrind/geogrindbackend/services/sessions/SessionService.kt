package com.geogrind.geogrindbackend.services.sessions

import com.geogrind.geogrindbackend.dto.session.CreateSessionDto
import com.geogrind.geogrindbackend.dto.session.DeleteSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.GetSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.UpdateSessionByIdDto
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
interface SessionService {
    suspend fun getAllSessions(): List<Sessions>
    suspend fun getSessionById(@Valid requestDto: GetSessionByIdDto): Sessions
    suspend fun createSession(@Valid requestDto: CreateSessionDto): Pair<Sessions, Cookie>
    suspend fun updateSessionById(@Valid requestDto: UpdateSessionByIdDto): Pair<Sessions, Cookie>
    suspend fun deleteSessionById(@Valid requestDto: DeleteSessionByIdDto) : Cookie
}