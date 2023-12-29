package io.grpc.kotlin.generator.services.chatroom.spring

import io.grpc.kotlin.generator.dto.chatroom.CreateChatRoomDto
import io.grpc.kotlin.generator.dto.chatroom.DeleteChatRoomByIdDto
import io.grpc.kotlin.generator.dto.chatroom.GetChatRoomByIdDto
import io.grpc.kotlin.generator.dto.chatroom.UpdateChatRoomByIdDto
import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
interface ChatRoomService {
    suspend fun getAllChatRooms(): List<ChatRoom>
    suspend fun getChatRoomById(
        @Valid requestDto: GetChatRoomByIdDto
    ): ChatRoom
    suspend fun createChatRoom(
        @Valid requestDto: CreateChatRoomDto
    ): ChatRoom
    suspend fun updateChatRoomById(
        @Valid requestDto: UpdateChatRoomByIdDto
    ): ChatRoom
    suspend fun deleteChatRoomById(
        @Valid requestDto: DeleteChatRoomByIdDto
    )
}