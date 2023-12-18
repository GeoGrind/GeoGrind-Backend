package io.grpc.kotlin.generator.repositories.chatroom

import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, UUID> {
}