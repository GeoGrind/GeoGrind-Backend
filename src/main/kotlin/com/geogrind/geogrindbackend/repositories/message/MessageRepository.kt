package com.geogrind.geogrindbackend.repositories.user_account

import com.geogrind.geogrindbackend.models.message.Message
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface MessageRepository : JpaRepository<Message, UUID> {

}