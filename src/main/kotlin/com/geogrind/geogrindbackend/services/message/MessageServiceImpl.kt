package com.geogrind.geogrindbackend.services.message

import com.geogrind.geogrindbackend.models.message.Message
import com.geogrind.geogrindbackend.repositories.message.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageServiceImpl(private val messageRepository: MessageRepository) : MessageService {
    @Transactional(readOnly = true)
    override suspend fun findAllMessages(): List<Message> = messageRepository.findAll()

}
