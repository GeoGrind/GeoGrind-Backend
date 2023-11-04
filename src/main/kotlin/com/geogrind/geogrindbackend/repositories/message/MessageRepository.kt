package com.geogrind.geogrindbackend.repositories.message

import com.geogrind.geogrindbackend.models.message.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessageRepository : JpaRepository<Message, UUID> {
    fun findByAuthorId(authorId: UUID): List<Message>

}