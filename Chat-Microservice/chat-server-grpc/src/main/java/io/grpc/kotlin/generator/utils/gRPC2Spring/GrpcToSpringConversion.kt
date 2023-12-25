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
interface GrpcToSpringConversion {
    fun convertAttachment(attachments: List<Attachment>): List<io.grpc.kotlin.generator.Attachment>

    fun convertReaction(reactions: List<Reaction>): List<io.grpc.kotlin.generator.Reaction>

    fun convertTheme(themes: List<Theme>): List<io.grpc.kotlin.generator.Theme>

    fun convertPermission(permissions: List<Permissions>): List<io.grpc.kotlin.generator.Permissions>

    fun convertUserAccount(userAccounts: List<UserAccount>): List<io.grpc.kotlin.generator.UserAccount>

    fun convertUserProfile(userProfiles: List<UserProfile>): List<io.grpc.kotlin.generator.UserProfile>

    fun convertCourse(courses: List<Courses>): List<io.grpc.kotlin.generator.Courses>

    fun convertSession(sessions: List<Sessions>): List<io.grpc.kotlin.generator.Sessions>

    fun convertMessages(messages: List<Message>): List<Messages>

    fun convertChatRoom(chatRooms: List<ChatRoom>): List<io.grpc.kotlin.generator.ChatRoom>
}