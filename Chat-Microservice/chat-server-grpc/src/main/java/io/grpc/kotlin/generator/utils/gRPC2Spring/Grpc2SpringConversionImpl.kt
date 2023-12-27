package io.grpc.kotlin.generator.utils.gRPC2Spring

import io.grpc.kotlin.generator.*
import io.grpc.kotlin.generator.models.message.Message
import io.grpc.kotlin.generator.sharedUtils.models.*
import io.grpc.kotlin.generator.sharedUtils.models.user_account.UserAccount
import java.text.DateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.HashSet

class Grpc2SpringConversionImpl : Grpc2SpringConversion {
    override fun convertAttachment(attachments: List<Attachment>): List<io.grpc.kotlin.generator.models.attachment.Attachment> {
        val springAttachments: MutableSet<io.grpc.kotlin.generator.models.attachment.Attachment> = HashSet()
        attachments.forEach { attachment: Attachment ->
            val springAttachment = io.grpc.kotlin.generator.models.attachment.Attachment(
                attachmentId = UUID.fromString(attachment.attachmentId),
                url = attachment.url,
                message = convertMessages(listOf(attachment.message!!))[0],
                attachmentType = enumValueOf(attachment.attachmentType),
                createdAt = Date(attachment.createdAt),
                updatedAt = Date(attachment.updatedAt),
            )
            springAttachments.add(springAttachment)
        }
        return springAttachments.toList()
    }

    override fun convertReaction(reactions: List<Reaction>): List<io.grpc.kotlin.generator.models.reactions.Reaction> {
        val springReactions: HashSet<io.grpc.kotlin.generator.models.reactions.Reaction> = HashSet()
        reactions.forEach { reaction: Reaction ->
            val springReaction = io.grpc.kotlin.generator.models.reactions.Reaction(
                reactionId = UUID.fromString(reaction.reactionId),
                reactionType = enumValueOf(reaction.reactionType),
                message = convertMessages(listOf(reaction.message!!))[0],
                createdAt = Date(reaction.createdAt),
                updatedAt = Date(reaction.updatedAt),
            )
            springReactions.add(springReaction)
        }
        return springReactions.toList()
    }

    override fun convertTheme(themes: List<Theme>): List<io.grpc.kotlin.generator.models.theme.Theme> {
        val springThemes: HashSet<io.grpc.kotlin.generator.models.theme.Theme> = HashSet()
        themes.forEach { theme: Theme ->
            val springTheme = io.grpc.kotlin.generator.models.theme.Theme(
                themeId = UUID.fromString(theme.themeId),
                chatRoom = convertChatRoom(listOf(theme.chatRoom!!))[0],
                themeType = enumValueOf(theme.themeType),
                themeDescription = theme.themeDescription,
                createdAt = Date(theme.createdAt),
                updatedAt = Date(theme.updatedAt),
            )
            springThemes.add(springTheme)
        }
        return springThemes.toList()
    }

    override fun convertPermission(permissions: List<Permissions>): List<io.grpc.kotlin.generator.sharedUtils.models.scheduling.Permissions> {
        val springPermissions: HashSet<io.grpc.kotlin.generator.sharedUtils.models.scheduling.Permissions> = HashSet()
        permissions.forEach { gRPCPermission ->
            val springPermission = io.grpc.kotlin.generator.sharedUtils.models.scheduling.Permissions(
                permission_id = UUID.fromString(gRPCPermission.permissionId),
                permission_name = enumValueOf(gRPCPermission.permissionName),
                userAccount = convertUserAccount(listOf(gRPCPermission.userAccount!!))[0],
                createdAt = Date(gRPCPermission.createdAt),
                updatedAt = Date(gRPCPermission.updatedAt),
            )
            springPermissions.add(springPermission)
        }
        return springPermissions.toList()
    }

    override fun convertUserAccount(userAccounts: List<io.grpc.kotlin.generator.UserAccount>): List<UserAccount> {
        val springUserAccounts: HashSet<io.grpc.kotlin.generator.sharedUtils.models.user_account.UserAccount> = HashSet()
        userAccounts.forEach { gRPCUserAccount ->
            val springUserAccount = io.grpc.kotlin.generator.sharedUtils.models.user_account.UserAccount(
                id = UUID.fromString(gRPCUserAccount.accountId),
                email = gRPCUserAccount.email,
                username = gRPCUserAccount.username,
                hashed_password = gRPCUserAccount.hashedPassword,
                account_verified = gRPCUserAccount.accountVerified,
                temp_token = gRPCUserAccount.tempToken,
                permissions = convertPermission(gRPCUserAccount.permissions).toHashSet(),
                userProfile = convertUserProfile(listOf(gRPCUserAccount.userProfile!!))[0],
                createdAt = Date(gRPCUserAccount.createdAt),
                updatedAt = Date(gRPCUserAccount.updatedAt),
            )
            springUserAccounts.add(springUserAccount)
        }
        return springUserAccounts.toList()
    }

    override fun convertUserProfile(userProfiles: List<UserProfile>): List<io.grpc.kotlin.generator.sharedUtils.models.user_profile.UserProfile> {
        val springUserProfiles: HashSet<io.grpc.kotlin.generator.sharedUtils.models.user_profile.UserProfile> = HashSet()
        userProfiles.forEach { gRPCUserProfile ->
            val springUserProfile = io.grpc.kotlin.generator.sharedUtils.models.user_profile.UserProfile(
                profile_id = UUID.fromString(gRPCUserProfile.profileId),
                profileImage = gRPCUserProfile.profileImage,
                username = gRPCUserProfile.username,
                emoji = gRPCUserProfile.emoji,
                program = gRPCUserProfile.program,
                year_of_graduation = gRPCUserProfile.yearOfGrad,
                university = gRPCUserProfile.university,
                userAccount = convertUserAccount(listOf(gRPCUserProfile.userAccount!!))[0],
                courses = convertCourse(gRPCUserProfile.course).toHashSet()
            )
            springUserProfiles.add(springUserProfile)
        }
        return springUserProfiles.toList()
    }

    override fun convertCourse(courses: List<Courses>): List<io.grpc.kotlin.generator.sharedUtils.models.courses.Courses> {
        val springCourses: HashSet<io.grpc.kotlin.generator.sharedUtils.models.courses.Courses> = HashSet()
        courses.forEach { gRPCCourse ->
            val springCourse = io.grpc.kotlin.generator.sharedUtils.models.courses.Courses(
                courseId = UUID.fromString(gRPCCourse.courseId),
                profile = convertUserProfile(listOf(gRPCCourse.userProfile!!))[0],
                courseCode = gRPCCourse.courseCode,
                courseName = gRPCCourse.courseName,
                createdAt = Date(gRPCCourse.createdAt),
                updatedAt = Date(gRPCCourse.updatedAt),
            )
            springCourses.add(springCourse)
        }
        return springCourses.toList()
    }

    override fun convertSession(sessions: List<Sessions>): List<io.grpc.kotlin.generator.sharedUtils.models.sessions.Sessions> {
        val springSessions: HashSet<io.grpc.kotlin.generator.sharedUtils.models.sessions.Sessions> = HashSet()
        sessions.forEach { gRPCSession ->
            val springSession = io.grpc.kotlin.generator.sharedUtils.models.sessions.Sessions(
                sessionId = UUID.fromString(gRPCSession.sessionId),
                course = convertCourse(listOf(gRPCSession.course!!))[0],
                profile = convertUserProfile(listOf(gRPCSession.userProfile!!))[0],
                startTime = Instant.ofEpochSecond(gRPCSession.startTime),
                numberOfLikers = gRPCSession.numberOfLikers,
                stopTime = Instant.ofEpochSecond(gRPCSession.stopTime),
                description = gRPCSession.description,
                createAt = Date(gRPCSession.createdAt),
                updatedAt = Date(gRPCSession.updatedAt),
            )
            springSessions.add(springSession)
        }
        return springSessions.toList()
    }

    override fun convertMessages(messages: List<Messages>): List<Message> {
        val springMessages: HashSet<Message> = HashSet()
        messages.forEach { gRPCMessage ->
            val springMessage = Message(
                messageId = UUID.fromString(gRPCMessage.messageId),
                messageSender = convertUserProfile(listOf(gRPCMessage.messageSender!!))[0],
                messageContent = gRPCMessage.messageContent,
                attachments = convertAttachment(gRPCMessage.attachments),
                reaction = convertReaction(gRPCMessage.reaction),
                readBy = convertUserProfile(gRPCMessage.messageReadBy).toHashSet(),
                createdAt = Date(gRPCMessage.createdAt),
                updatedAt = Date(gRPCMessage.updatedAt),
            )
            springMessages.add(springMessage)
        }
        return springMessages.toList()
    }

    override fun convertChatRoom(chatRooms: List<ChatRoom>): List<io.grpc.kotlin.generator.models.chatroom.ChatRoom> {
        val springChatRooms: HashSet<io.grpc.kotlin.generator.models.chatroom.ChatRoom> = HashSet()
        chatRooms.forEach { gRPCChatRoom ->
            val springChatRoom = io.grpc.kotlin.generator.models.chatroom.ChatRoom(
                chatRoomId = UUID.fromString(gRPCChatRoom.chatRoomId),
                chatRoomDescription = gRPCChatRoom.chatRoomDescription,
                chatRoomOwners = convertUserProfile(gRPCChatRoom.chatRoomOwners).toHashSet(),
                chatRoomMembers = convertUserProfile(gRPCChatRoom.chatRoomMembers).toHashSet(),
                chatRoomMessages = convertMessages(gRPCChatRoom.chatRoomMessages).toHashSet(),
                chatRoomTheme = convertTheme(listOf(gRPCChatRoom.chatRoomTheme!!))[0],
                createdAt = Date(gRPCChatRoom.createdAt),
                updatedAt = Date(gRPCChatRoom.updatedAt),
            )
            springChatRooms.add(springChatRoom)
        }
        return springChatRooms.toList()
    }
}