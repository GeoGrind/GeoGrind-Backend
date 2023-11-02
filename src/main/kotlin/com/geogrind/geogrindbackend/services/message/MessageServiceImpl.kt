package com.geogrind.geogrindbackend.services.message

import com.geogrind.geogrindbackend.models.message.Message
import com.geogrind.geogrindbackend.repositories.user_account.MessageRepository
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional

@Service
class MessageServiceImpl(private val messageRepository: MessageRepository) : MessageService {

    @Transactional
    override suspend fun findAllMessages(): List<Message> {
        return messageRepository.findAll()
    }
}
