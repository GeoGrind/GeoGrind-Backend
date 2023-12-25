package io.grpc.kotlin.generator

import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import io.grpc.kotlin.generator.services.chatroom.ChatRoomService

// ******************* IMPLEMENT CHAT ROOM GRPC SERVICES FROM PROTOBUF *********************
class ChatGrpcServerApplication(
    private val springService: ChatRoomService
) : ChatRoomServiceSuffix {
    override suspend fun GetAllChatRooms(request: EmptyRequest): GetAllChatRoomsResponse {
        // call the spring service to get all the chat rooms
        val allCurrentChatRooms: List<ChatRoom> = springService.getAllChatRooms()
        val currentChatRoomsGrpc: List<io.grpc.kotlin.generator.ChatRoom> = emptyList()

        // Convert from chatRoom in Spring models to chatRoom in gRPC models
        allCurrentChatRooms.forEach { springChatRoom ->
            val newGrpcChatRoom = springChatRoom.chatRoomDescription?.let {
                ChatRoom(
                    chatRoomId = springChatRoom.toString(),
                    chatRoomDescription = it,
                    chatRoomOwners =
                    )
            }
        }

        return GetAllChatRoomsResponse(
            chatRooms = allCurrentChatRooms,

        )
    }

    override suspend fun GetChatRoomById(request: GetChatRoomByIdRequest): GetChatRoomByIdResponse {
        TODO("Not yet implemented")
    }

    override suspend fun CreateChatRoom(request: CreateChatRoomRequest): CreateChatRoomResponse {
        TODO("Not yet implemented")
    }

    override suspend fun UpdateChatRoomById(request: UpdateChatRoomByIdRequest): UpdateChatRoomByIdResponse {
        TODO("Not yet implemented")
    }

    override suspend fun DeleteChatRoomById(request: DeleteChatRoomByIdRequest): DeleteChatRoomByIdResponse {
        TODO("Not yet implemented")
    }
}