package com.geogrind.geogrindbackend.services.sessions

import com.geogrind.geogrindbackend.dto.session.CreateSessionDto
import com.geogrind.geogrindbackend.dto.session.DeleteSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.GetSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.UpdateSessionByIdDto
import com.geogrind.geogrindbackend.models.sessions.Sessions
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
interface SessionService {
    suspend fun getAllSessions(): List<Sessions>
    suspend fun getSessionById(@Valid requestDto: GetSessionByIdDto): Sessions
    suspend fun createSession(@Valid requestDto: CreateSessionDto): Sessions
    suspend fun updateSessionById(@Valid requestDto: UpdateSessionByIdDto): Sessions
    suspend fun deleteSessionById(@Valid requestDto: DeleteSessionByIdDto)
}