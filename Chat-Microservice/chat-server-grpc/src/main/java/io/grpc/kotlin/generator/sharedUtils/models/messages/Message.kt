package com.geogrind.geogrindbackend.models.messages
//
//import com.fasterxml.jackson.annotation.JsonIgnore
//import com.geogrind.geogrindbackend.models.messages.utils.Attachment
//import com.geogrind.geogrindbackend.models.messages.utils.Reaction
//import com.geogrind.geogrindbackend.models.user_profile.UserProfile
//import jakarta.persistence.*
//import jakarta.validation.constraints.Size
//import org.hibernate.annotations.GenericGenerator
//import org.springframework.data.annotation.CreatedDate
//import org.springframework.data.annotation.LastModifiedDate
//import org.springframework.data.jpa.domain.support.AuditingEntityListener
//import java.util.*
//import kotlin.math.min
//
//@Entity
//@Table(name = "message")
//@EntityListeners(AuditingEntityListener::class)
//data class Message(
//
//    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Column(name = "message_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
//    val messageId: UUID? = null,
//
//    @OneToOne(mappedBy = "message")
//    @JsonIgnore
//    var userProfile: UserProfile,
//
//    @Column(name = "message_content", length = 100000, unique = false, nullable = false)
//    @Size(min = 1)
//    var messageContent: String,
//
//    @OneToMany
//    @JoinColumn(name = "message_id")
//    var attachments: List<Attachment>? = emptyList(),
//
//    @OneToMany
//    @JoinColumn(name = "message_id")
//    var reaction: List<Reaction>? = emptyList(),
//
//    @OneToMany
//    @JoinColumn(name = "message_id")
//    var readBy: List<UserProfile>? = emptyList(),
//
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "created_at")
//    var createdAt: Date? = null,
//
//    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "updated_at")
//    var updatedAt: Date? = null,
//) {
//
//    // CUSTOM METHODS
//    override fun equals(other: Any?): Boolean {
//        if(this === other) return true
//        if(other !is Message) return false
//
//        other as Message
//
//        if(messageId != other.messageId) return false
//        if(userProfile != other.userProfile) return false
//        if(messageContent != other.messageContent) return false
//        if(attachments != other.attachments) return false
//        if(reaction != other.reaction) return false
//        return readBy == other.readBy
//    }
//
//    override fun hashCode(): Int {
//        return super.hashCode()
//    }
//
//    override fun toString(): String {
//        return super.toString()
//    }
//}
