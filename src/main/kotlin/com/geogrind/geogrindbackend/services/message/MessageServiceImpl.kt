package com.geogrind.geogrindbackend.services.message

import com.geogrind.geogrindbackend.repositories.user_account.MessageRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(private val messageRepository: MessageRepository) : MessageService {
    @Transactional
    override suspend fun test(): String {
        return "Test"
    }
}