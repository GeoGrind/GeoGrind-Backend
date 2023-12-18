package io.grpc.kotlin.generator.services.chatroom

import io.grpc.kotlin.generator.dto.chatroom.CreateChatRoomDto
import io.grpc.kotlin.generator.dto.chatroom.DeleteChatRoomByIdDto
import io.grpc.kotlin.generator.dto.chatroom.GetChatRoomByIdDto
import io.grpc.kotlin.generator.dto.chatroom.UpdateChatRoomByIdDto
import io.grpc.kotlin.generator.exceptions.chatroom.ChatRoomNotFoundException
import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import io.grpc.kotlin.generator.repositories.chatroom.ChatRoomRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
) : ChatRoomService {
    @Transactional(readOnly = true)
    override suspend fun getAllChatRooms(): List<ChatRoom> {
        return chatRoomRepository.findAll()
    }

    @Transactional(readOnly = true)
    override suspend fun getChatRoomById(requestDto: GetChatRoomByIdDto): ChatRoom {
        val findChatRoom = chatRoomRepository.findById(requestDto.chatRoomId)
        if(findChatRoom.isEmpty) {
            throw ChatRoomNotFoundException("Cannot find chat room with chat room id: ${requestDto.chatRoomId}")
        }
        return findChatRoom.get()
    }

    @Transactional
    override suspend fun createChatRoom(requestDto: CreateChatRoomDto): ChatRoom {
        // create new chatroom for the user profile
        val newChatRoom = ChatRoom(
            chatRoomDescription = requestDto.chatRoomDescription,
            chatRoomTheme = requestDto.chatRoomTheme,
            chatRoomMembers = requestDto.chatRoomMembers,
            chatRoomOwners = requestDto.chatRoomOwners,
            chatRoomMessages = requestDto.chatRoomMessages,
        )

        log.info("New Chat Room is created in the database: $newChatRoom")
        return chatRoomRepository.save(newChatRoom)
    }

    @Transactional
    override suspend fun updateChatRoomById(requestDto: UpdateChatRoomByIdDto): ChatRoom {
        return if (chatRoomRepository.existsById(requestDto.chatRoomId)) {
            val chatRoomFound = chatRoomRepository.findById(requestDto.chatRoomId)
            chatRoomFound.get().apply {
                this.chatRoomOwners = requestDto.updateChatRoomOwners
                this.chatRoomTheme = requestDto.updateChatRoomTheme
                this.chatRoomDescription = requestDto.updateChatRoomDescription
                this.chatRoomMembers = requestDto.updateChatRoomMembers
                this.chatRoomMessages = requestDto.updateChatRoomMessages
            }
            chatRoomRepository.save(chatRoomFound.get())
        } else throw ChatRoomNotFoundException("No matching chatroom was found with chatroom id: ${requestDto.chatRoomId}")
    }

    @Transactional
    override suspend fun deleteChatRoomById(requestDto: DeleteChatRoomByIdDto) {
        return if (chatRoomRepository.existsById(requestDto.chatRoomId)) {
            chatRoomRepository.deleteById(requestDto.chatRoomId)
        } else throw ChatRoomNotFoundException("No matching chatroom was found with chatroom id: ${requestDto.chatRoomId}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChatRoomServiceImpl::class.java)
    }
}