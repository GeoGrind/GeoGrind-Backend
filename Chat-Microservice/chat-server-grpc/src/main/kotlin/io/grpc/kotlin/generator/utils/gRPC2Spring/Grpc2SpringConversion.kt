package io.grpc.kotlin.generator.utils.gRPC2Spring

import io.grpc.kotlin.generator.Messages
import io.grpc.kotlin.generator.models.attachment.Attachment
import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import io.grpc.kotlin.generator.models.message.Message
import io.grpc.kotlin.generator.models.reactions.Reaction
import io.grpc.kotlin.generator.models.theme.Theme
import io.grpc.kotlin.generator.sharedUtils.models.courses.Courses
import io.grpc.kotlin.generator.sharedUtils.models.scheduling.Permissions
import io.grpc.kotlin.generator.sharedUtils.models.sessions.Sessions
import io.grpc.kotlin.generator.sharedUtils.models.user_account.UserAccount
import io.grpc.kotlin.generator.sharedUtils.models.user_profile.UserProfile
import org.springframework.stereotype.Service

@Service
interface Grpc2SpringConversion {
    fun convertAttachment(attachments: List<io.grpc.kotlin.generator.Attachment>): List<Attachment>

    fun convertReaction(reactions: List<io.grpc.kotlin.generator.Reaction>): List<Reaction>

    fun convertTheme(themes: List<io.grpc.kotlin.generator.Theme>): List<Theme>

    fun convertPermission(permissions: List<io.grpc.kotlin.generator.Permissions>): List<Permissions>

    fun convertUserAccount(userAccounts: List<io.grpc.kotlin.generator.UserAccount>): List<UserAccount>

    fun convertUserProfile(userProfiles: List<io.grpc.kotlin.generator.UserProfile>): List<UserProfile>

    fun convertCourse(courses: List<io.grpc.kotlin.generator.Courses>): List<Courses>

    fun convertSession(sessions: List<io.grpc.kotlin.generator.Sessions>): List<Sessions>

    fun convertMessages(messages: List<Messages>): List<Message>

    fun convertChatRoom(chatRooms: List<io.grpc.kotlin.generator.ChatRoom>): List<ChatRoom>
}