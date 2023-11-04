package com.geogrind.geogrindbackend.services.message

import com.geogrind.geogrindbackend.models.message.Message
import org.springframework.stereotype.Service

@Service
interface MessageService {
    suspend fun findAllMessages(): List<Message>
}
