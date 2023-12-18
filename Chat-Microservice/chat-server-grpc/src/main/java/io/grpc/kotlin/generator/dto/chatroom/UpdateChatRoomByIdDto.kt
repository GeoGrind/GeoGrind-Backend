package io.grpc.kotlin.generator.dto.chatroom

import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import io.grpc.kotlin.generator.models.message.Message
import io.grpc.kotlin.generator.models.theme.Theme
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*
import kotlin.collections.HashSet

data class UpdateChatRoomByIdDto(
    @field:org.jetbrains.annotations.NotNull
    @field:NotBlank
    val chatRoomId: UUID,
    var updateChatRoomTheme: Theme,
    @get:Size(min = 3, max = 100) @NotNull var updateChatRoomDescription: String? = null,
    var updateChatRoomOwners: MutableSet<UserProfile> = HashSet(),
    var updateChatRoomMembers: MutableSet<UserProfile> = HashSet(),
    var updateChatRoomMessages: MutableSet<Message> = HashSet(),
)
