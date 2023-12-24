package io.grpc.kotlin.generator.models.chatroom

import com.fasterxml.jackson.annotation.JsonIgnore
import io.grpc.kotlin.generator.models.message.Message
import io.grpc.kotlin.generator.models.theme.Theme
import io.grpc.kotlin.generator.sharedUtils.models.user_profile.UserProfile
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "chatroom")
@EntityListeners(AuditingEntityListener::class)
data class ChatRoom (

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "chatroom_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    val chatRoomId: UUID? = null,

    @Column(name = "chatroom_description", length = 100, unique = false, nullable = false)
    var chatRoomDescription: String ?= null,

    @OneToMany
    @JoinColumn(name = "chatroom_id")
    var chatRoomOwners: MutableSet<UserProfile>,

    @OneToMany
    @JoinColumn(name = "chatroom_id")
    var chatRoomMembers: MutableSet<UserProfile>,

    @OneToMany
    @JoinColumn(name = "chatroom_id")
    var chatRoomMessages: MutableSet<Message> = HashSet(),

    @OneToOne(mappedBy = "chatRoom")
    @JsonIgnore
    var chatRoomTheme: Theme? = null,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = Date(),

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = Date(),
) {
    @Column(name = "chatroom_name", length = 100, unique = false, nullable = false)
    @Size(min = 3)
    private var chatRoomName: String = chatRoomMembers.joinToString(", ") { it.username }

    fun setChatRoomName(newChatRoomName: String) {
        this.chatRoomName = newChatRoomName
    }

    fun getChatRoomName(): String {
        return this.chatRoomName
    }
}