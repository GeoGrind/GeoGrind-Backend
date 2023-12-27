package io.grpc.kotlin.generator

import io.grpc.kotlin.generator.dto.chatroom.CreateChatRoomDto
import io.grpc.kotlin.generator.dto.chatroom.DeleteChatRoomByIdDto
import io.grpc.kotlin.generator.dto.chatroom.GetChatRoomByIdDto
import io.grpc.kotlin.generator.dto.chatroom.UpdateChatRoomByIdDto
import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import io.grpc.kotlin.generator.services.chatroom.ChatRoomService
import io.grpc.kotlin.generator.utils.gRPC2Spring.Grpc2SpringConversion
import io.grpc.kotlin.generator.utils.gRPC2Spring.Spring2GrpcConversion
import java.util.*

// ******************* IMPLEMENT CHAT ROOM GRPC SERVICES FROM PROTOBUF *********************
class ChatGrpcServerApplication(
    private val springService: ChatRoomService,
    private val spring2GrpcConversion: Spring2GrpcConversion,
    private val gRPC2SpringConversion: Grpc2SpringConversion,
) : ChatRoomServiceSuffix {
    override suspend fun GetAllChatRooms(request: EmptyRequest): GetAllChatRoomsResponse {
        // call the spring service to get all the chat rooms
        val allCurrentChatRooms: List<ChatRoom> = springService.getAllChatRooms()
        val currentChatRoomsGrpc: List<io.grpc.kotlin.generator.ChatRoom> = spring2GrpcConversion.convertChatRoom(
            chatRooms = allCurrentChatRooms,
        )

        return GetAllChatRoomsResponse(
            chatRooms = currentChatRoomsGrpc,
        )
    }

    override suspend fun GetChatRoomById(request: GetChatRoomByIdRequest): GetChatRoomByIdResponse {
        val findChatRoomId = request.id
        val findChatRoom: ChatRoom = springService.getChatRoomById(
            GetChatRoomByIdDto(
                chatRoomId = UUID.fromString(findChatRoomId)
            )
        )
        val gRPCFindChatRoom: io.grpc.kotlin.generator.ChatRoom = spring2GrpcConversion.convertChatRoom(listOf(findChatRoom))[0]

        return GetChatRoomByIdResponse(
            chatRoom = gRPCFindChatRoom
        )
    }

    override suspend fun CreateChatRoom(request: CreateChatRoomRequest): CreateChatRoomResponse {
        val gRPCChatRoomDescription = request.chatRoomDescription
        val gRPCChatRoomTheme = request.chatRoomTheme
        val gRPCChatRoomName = request.chatRoomName
        val gRPCChatRoomOwners = request.chatRoomOwners
        val gRPCChatRoomMembers = request.chatRoomMembers
        val gRPCChatRoomMessages = request.chatRoomMessages

        val newChatRoom = springService.createChatRoom(
            requestDto = CreateChatRoomDto(
                chatRoomDescription = gRPCChatRoomDescription,
                chatRoomTheme = gRPC2SpringConversion.convertTheme(listOf(gRPCChatRoomTheme!!))[0],
                chatRoomName = gRPCChatRoomName,
                chatRoomOwners = gRPC2SpringConversion.convertUserProfile(gRPCChatRoomOwners).toHashSet(),
                chatRoomMembers = gRPC2SpringConversion.convertUserProfile(gRPCChatRoomMembers).toHashSet(),
                chatRoomMessages = gRPC2SpringConversion.convertMessages(gRPCChatRoomMessages).toHashSet()
            )
        )

        return CreateChatRoomResponse(
            newChatRoom = spring2GrpcConversion.convertChatRoom(listOf(newChatRoom))[0]
        )
    }

    override suspend fun UpdateChatRoomById(request: UpdateChatRoomByIdRequest): UpdateChatRoomByIdResponse {
        val gRPCChatRoomId = request.chatRoomId
        val gRPCChatRoomDescription = request.updateChatRoomDescription
        val gRPCChatRoomTheme = request.updateChatRoomTheme
        val gRPCChatRoomName = request.updateChatRoomName
        val gRPCChatRoomOwners = request.updateChatRoomOwners
        val gRPCChatRoomMembers = request.updateChatRoomMembers
        val gRPCChatRoomMessages = request.updateChatRoomMessages

        val updateChatRoom = springService.updateChatRoomById(
            requestDto = UpdateChatRoomByIdDto(
                chatRoomId = UUID.fromString(gRPCChatRoomId),
                updateChatRoomTheme = gRPC2SpringConversion.convertTheme(listOf(gRPCChatRoomTheme!!))[0],
                updateChatRoomDescription = gRPCChatRoomDescription,
                updateChatRoomOwners = gRPC2SpringConversion.convertUserProfile(gRPCChatRoomOwners).toHashSet(),
                updateChatRoomMembers = gRPC2SpringConversion.convertUserProfile(gRPCChatRoomMembers).toHashSet(),
                updateChatRoomMessages = gRPC2SpringConversion.convertMessages(gRPCChatRoomMessages).toHashSet(),
                updateChatRoomName = gRPCChatRoomName,
            )
        )

        return UpdateChatRoomByIdResponse(
            updateChatRoom = spring2GrpcConversion.convertChatRoom(listOf(updateChatRoom))[0],
        )
    }

    override suspend fun DeleteChatRoomById(request: DeleteChatRoomByIdRequest): DeleteChatRoomByIdResponse {
        val gRPCId = request.id
        springService.deleteChatRoomById(
            requestDto = DeleteChatRoomByIdDto(
                chatRoomId = UUID.fromString(gRPCId)
            )
        )
        return DeleteChatRoomByIdResponse(
            success = true
        )
    }
}