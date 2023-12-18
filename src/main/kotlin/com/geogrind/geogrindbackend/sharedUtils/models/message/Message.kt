package io.grpc.kotlin.generator.models.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import io.grpc.kotlin.generator.models.attachment.Attachment
import io.grpc.kotlin.generator.models.reactions.Reaction
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@Table(name = "message")
@EntityListeners(AuditingEntityListener::class)
data class Message(

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "message_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    val messageId: UUID? = null,

    @OneToOne(targetEntity = UserProfile::class, cascade = [CascadeType.ALL])
    @JsonIgnore
    @JoinColumn(name = "fk_user_profile_id", referencedColumnName = "message_id")
    var messageSender: UserProfile,

    @Column(name = "message_content", length = 100000, unique = false, nullable = false)
    @Size(min = 1)
    var messageContent: String,

    @OneToMany
    @JoinColumn(name = "message_id")
    var attachments: List<Attachment>? = emptyList(),

    @OneToMany
    @JoinColumn(name = "message_id")
    var reaction: List<Reaction>? = emptyList(),

    @ManyToMany(mappedBy = "user_profile")
    var readBy: MutableSet<UserProfile>? = HashSet(),

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
    )
// {
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