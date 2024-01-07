package io.grpc.kotlin.generator.dto.chatroom

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class GetChatRoomByIdDto (
    @field:org.jetbrains.annotations.NotNull
    @field:NotBlank
    val chatRoomId: UUID,
)