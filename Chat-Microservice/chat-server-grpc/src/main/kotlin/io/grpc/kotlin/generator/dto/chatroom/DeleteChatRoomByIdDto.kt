package io.grpc.kotlin.generator.dto.chatroom

import jakarta.validation.constraints.NotBlank
import java.util.*

data class DeleteChatRoomByIdDto(
    @field:org.jetbrains.annotations.NotNull
    @field:NotBlank
    val chatRoomId: UUID,
)