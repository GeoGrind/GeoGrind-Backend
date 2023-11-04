package com.geogrind.geogrindbackend.services.message

import com.geogrind.geogrindbackend.models.message.Message
import com.geogrind.geogrindbackend.repositories.message.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MessageServiceImpl(private val messageRepository: MessageRepository) : MessageService {
    @Transactional(readOnly = true)
    override suspend fun findAllMessages(): List<Message> = messageRepository.findAll()

    @Transactional(readOnly = true)
    override suspend fun findMessagesByAuthor(authorId: UUID): List<Message> =
        withContext(Dispatchers.IO) {
            messageRepository.findByAuthorId(authorId)
        }
}
