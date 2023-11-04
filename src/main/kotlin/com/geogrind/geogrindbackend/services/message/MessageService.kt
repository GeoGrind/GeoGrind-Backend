package com.geogrind.geogrindbackend.services.message

import com.geogrind.geogrindbackend.models.message.Message
import org.springframework.stereotype.Service
import java.util.*

@Service
interface MessageService {
    suspend fun findAllMessages(): List<Message>
    suspend fun findMessagesByAuthor(authorId: UUID): List<Message>
}
