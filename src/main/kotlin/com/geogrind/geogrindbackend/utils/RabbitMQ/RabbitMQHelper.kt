package com.geogrind.geogrindbackend.utils.RabbitMQ

import com.geogrind.geogrindbackend.dto.session.DeleteSessionByIdDto
import com.geogrind.geogrindbackend.models.sessions.Sessions
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
interface RabbitMQHelper {
    suspend fun sendSessionDeletionMessage(@Valid sessionToDelete: Sessions)

}