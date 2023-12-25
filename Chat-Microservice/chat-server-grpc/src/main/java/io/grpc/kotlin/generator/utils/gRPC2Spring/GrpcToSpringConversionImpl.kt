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

class GrpcToSpringConversionImpl : GrpcToSpringConversion {
    override fun convertAttachment(attachments: List<Attachment>): List<io.grpc.kotlin.generator.Attachment> {
        val gRPCAttachments: MutableSet<io.grpc.kotlin.generator.Attachment> = HashSet()
        attachments.forEach { attachment: Attachment ->
            val gRPCAttachment = io.grpc.kotlin.generator.Attachment(
                attachmentId = attachment.attachmentId.toString(),
                url = attachment.url,
                message = convertMessages(listOf(attachment.message))[0],
                createdAt = attachment.createdAt.toString(),
                updatedAt = attachment.updatedAt.toString(),
            )
            gRPCAttachments.add(gRPCAttachment)
        }
        return gRPCAttachments.toList()
    }

    override fun convertReaction(reactions: List<Reaction>): List<io.grpc.kotlin.generator.Reaction> {
        val gRPCReactions: HashSet<io.grpc.kotlin.generator.Reaction> = HashSet()
        reactions.forEach { reaction: Reaction ->
            val gRPCReaction = io.grpc.kotlin.generator.Reaction(
                reactionId = reaction.reactionId.toString(),
                message = convertMessages(listOf(reaction.message))[0],
                createdAt = reaction.createdAt.toString(),
                updatedAt = reaction.updatedAt.toString(),
            )
            gRPCReactions.add(gRPCReaction)
        }
        return gRPCReactions.toList()
    }

    override fun convertTheme(themes: List<Theme>): List<io.grpc.kotlin.generator.Theme> {
        val gRPCToThemes: HashSet<io.grpc.kotlin.generator.Theme> = HashSet()
        themes.forEach { theme: Theme ->
            val gRPCTheme: io.grpc.kotlin.generator.Theme = io.grpc.kotlin.generator.Theme(
                themeId = theme.themeId.toString(),
                chatRoom = convertChatRoom(listOf(theme.chatRoom))[0],
                themeDescription = theme.themeDescription.toString(),
                createdAt = theme.createdAt.toString(),
                updatedAt = theme.updatedAt.toString(),
            )
            gRPCToThemes.add(gRPCTheme)
        }
        return gRPCToThemes.toList()
    }

    override fun convertPermission(permissions: List<Permissions>): List<io.grpc.kotlin.generator.Permissions> {
        val gRPCPermissions: HashSet<io.grpc.kotlin.generator.Permissions> = HashSet()
        permissions.forEach { permission ->
            val gRPCPermission = io.grpc.kotlin.generator.Permissions(
                permissionId = permission.permission_id.toString(),
                userAccount = convertUserAccount(listOf(permission.userAccount))[0],
                createdAt = permission.createdAt.toString(),
                updatedAt = permission.updatedAt.toString(),
            )
            gRPCPermissions.add(gRPCPermission)
        }
        return gRPCPermissions.toList()
    }

    override fun convertUserAccount(userAccounts: List<UserAccount>): List<io.grpc.kotlin.generator.UserAccount> {
        val gRPCUserAccounts: HashSet<io.grpc.kotlin.generator.UserAccount> = HashSet()
        userAccounts.forEach { userAccount ->
            val gRPCUserAccount = userAccount.temp_token?.let {
                userAccount.account_verified?.let { it1 ->
                    userAccount.permissions?.let { it2 -> convertPermission(it2.toList()) }?.let { it3 ->
                        io.grpc.kotlin.generator.UserAccount(
                            accountId = userAccount.id.toString(),
                            email = userAccount.email,
                            username = userAccount.username,
                            hashedPassword = userAccount.hashed_password,
                            accountVerified = it1,
                            tempToken = it,
                            permissions = it3,
                            userProfile = convertUserProfile(
                                userProfiles = listOf(userAccount.userProfile!!)
                            )[0],
                            createdAt = userAccount.createdAt.toString(),
                            updatedAt = userAccount.updatedAt.toString(),
                            )
                    }
                }
            }
            gRPCUserAccounts.add(gRPCUserAccount!!)
        }
        return gRPCUserAccounts.toList()
    }

    override fun convertUserProfile(userProfiles: List<UserProfile>): List<io.grpc.kotlin.generator.UserProfile> {
        val gRPCUserProfiles: HashSet<io.grpc.kotlin.generator.UserProfile> = HashSet()
        userProfiles.forEach { userProfile ->
            val gRPCUserProfile = userProfile.profileImage?.let {
                userProfile.emoji?.let { it1 ->
                    userProfile.program?.let { it2 ->
                        userProfile.courses?.let { it3 -> convertCourse(it3.toList()) }?.let { it4 ->
                            userProfile.university?.let { it3 ->
                                userProfile.year_of_graduation?.let { it5 ->
                                    io.grpc.kotlin.generator.UserProfile(
                                        profileId = userProfile.profile_id.toString(),
                                        profileImage = it,
                                        username = userProfile.username,
                                        emoji = it1,
                                        program = it2,
                                        yearOfGrad = it5,
                                        university = it3,
                                        userAccount = convertUserAccount(
                                            listOf(userProfile.userAccount)
                                        )[0],
                                        course = it4,
                                        sessions = convertSession(listOf(userProfile.session!!))[0],
                                        chatroom = convertChatRoom(listOf(userProfile.chatRoom!!))[0],
                                        messageSender = convertMessages(messages = listOf(userProfile.messageSender!!))[0],
                                        messageRead = convertMessages(
                                            messages = userProfile.messageRead!!.toList()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            gRPCUserProfiles.add(gRPCUserProfile!!)
        }

        return gRPCUserProfiles.toList()
    }

    override fun convertCourse(courses: List<Courses>): List<io.grpc.kotlin.generator.Courses> {
        val gRPCCourses: HashSet<io.grpc.kotlin.generator.Courses> = HashSet()
        courses.forEach { course: Courses ->
            val gRPCCourse = io.grpc.kotlin.generator.Courses(
                courseId = course.courseId.toString(),
                userProfile = convertUserProfile(listOf(course.profile))[0],
                courseCode = course.courseCode,
                courseName = course.courseName,
                createdAt = course.createdAt.toString(),
                updatedAt = course.updatedAt.toString(),
            )
            gRPCCourses.add(gRPCCourse)
        }
        return gRPCCourses.toList()
    }

    override fun convertSession(sessions: List<Sessions>): List<io.grpc.kotlin.generator.Sessions> {
        val gRPCSessions: HashSet<io.grpc.kotlin.generator.Sessions> = HashSet()
        sessions.forEach { sessions: Sessions ->
            val gRPCSession = sessions.numberOfLikers?.let {
                sessions.description?.let { it1 ->
                    io.grpc.kotlin.generator.Sessions(
                        sessionId = sessions.sessionId.toString(),
                        course = convertCourse(
                            courses = listOf(sessions.course!!)
                        )[0],
                        userProfile = convertUserProfile(listOf(sessions.profile!!))[0],
                        startTime = sessions.startTime.toString(),
                        numberOfLikers = it,
                        stopTime = sessions.stopTime.toString(),
                        description = it1,
                        createdAt = sessions.createAt.toString(),
                        updatedAt = sessions.updatedAt.toString(),
                    )
                }
            }
            gRPCSessions.add(gRPCSession!!)
        }
        return gRPCSessions.toList()
    }

    override fun convertMessages(messages: List<Message>): List<Messages> {
        val gRPCMessages: HashSet<Messages> = HashSet()
        messages.forEach { message: Message ->
            val gRPCMessage = Messages(
                messageId = message.messageId.toString(),
                messageSender = convertUserProfile(listOf(message.messageSender))[0],
                messageContent = message.messageContent,
                attachments = convertAttachment(message.attachments!!),
                reaction = convertReaction(message.reaction!!),
                messageReadBy = convertUserProfile(message.readBy!!.toList()),
                createdAt = message.createdAt.toString(),
                updatedAt = message.updatedAt.toString(),
            )
            gRPCMessages.add(gRPCMessage)
        }
        return gRPCMessages.toList()
    }

    override fun convertChatRoom(chatRooms: List<ChatRoom>): List<io.grpc.kotlin.generator.ChatRoom> {
        val gRPCChatRooms: HashSet<io.grpc.kotlin.generator.ChatRoom> = HashSet()
        chatRooms.forEach { chatRoom ->
            val gRPCChatRoom = io.grpc.kotlin.generator.ChatRoom(
                chatRoomId = chatRoom.chatRoomId.toString(),
                chatRoomDescription = chatRoom.chatRoomDescription!!,
                chatRoomOwners = convertUserProfile(chatRoom.chatRoomOwners.toList()),
                chatRoomMembers = convertUserProfile(chatRoom.chatRoomMembers.toList()),
                chatRoomMessages = convertMessages(chatRoom.chatRoomMessages.toList()),
                chatRoomTheme = convertTheme(listOf(chatRoom.chatRoomTheme!!))[0],
                createdAt = chatRoom.createdAt.toString(),
                updatedAt = chatRoom.updatedAt.toString(),
                chatRoomName = chatRoom.getChatRoomName(),
            )
            gRPCChatRooms.add(gRPCChatRoom)
        }
        return gRPCChatRooms.toList()
    }
}