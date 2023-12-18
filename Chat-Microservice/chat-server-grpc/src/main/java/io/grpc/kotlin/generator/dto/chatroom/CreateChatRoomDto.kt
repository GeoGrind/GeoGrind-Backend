package io.grpc.kotlin.generator.dto.chatroom

import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import io.grpc.kotlin.generator.models.message.Message
import io.grpc.kotlin.generator.models.theme.Theme
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateChatRoomDto (
    @get:Size(min = 3, max = 100) @NotNull var chatRoomDescription: String? = null,
    var chatRoomTheme: Theme,
    var chatRoomOwners: MutableSet<UserProfile>,
    var chatRoomMembers: MutableSet<UserProfile>,
    var chatRoomMessages: MutableSet<Message> = HashSet(),
)