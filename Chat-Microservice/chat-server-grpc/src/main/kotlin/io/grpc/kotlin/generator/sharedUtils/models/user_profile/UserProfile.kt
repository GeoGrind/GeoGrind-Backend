package io.grpc.kotlin.generator.sharedUtils.models.user_profile

import com.fasterxml.jackson.annotation.JsonIgnore
import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import io.grpc.kotlin.generator.models.message.Message
import io.grpc.kotlin.generator.sharedUtils.models.courses.Courses
import io.grpc.kotlin.generator.sharedUtils.models.sessions.Sessions
import io.grpc.kotlin.generator.sharedUtils.models.user_account.UserAccount
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date
import java.util.UUID

// user profile model
@Entity
@Table(name = "user_profile")
@EntityListeners(AuditingEntityListener::class)
data class UserProfile(

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "profile_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    var profile_id: UUID? = null,

    @Column(name = "profile_image", unique = false, nullable = false)
    @Size(min = 5)
    var profileImage: String? = "thumb_15951118880user.webp",

    @Column(name = "username", length = 50, unique = true, nullable = false)
    @Size(min = 3)
    var username: String,

    @Column(name = "emoji", length = 1000, nullable = false)
    @Size(min = 3)
    var emoji: String? = "No emoji set",

    @Column(name = "program", length = 100, nullable = true)
    @Size(min = 5)
    var program: String? = null,

    @Column(name = "year_of_graduation", length = 4, nullable = true)
    @Size(min = 4)
    var year_of_graduation: Int? = null,

    @Column(name = "university", length = 100, nullable = false)
    @Size(min = 5)
    var university: String? = "University of Waterloo",

    // One to one relationship with the user account
    @OneToOne(targetEntity = UserAccount::class, cascade = [CascadeType.ALL])
    @JsonIgnore
    @JoinColumn(name = "fk_user_account_id", referencedColumnName = "id")
    var userAccount: UserAccount,

    // One to many relationship with the Courses
    @OneToMany(fetch = FetchType.EAGER)
//    @JsonIgnore
    @JoinColumn(name = "profile_id")
    var courses: MutableSet<Courses>? = HashSet(),

    // One to one relationship with the session table
    @OneToOne(mappedBy = "profile")
    @JsonIgnore
    var session: Sessions? = null,

    @ManyToOne
    @JoinColumn(name = "chatroom_id", insertable = false, updatable = false)
    var chatRoom: ChatRoom? = null,

    @OneToOne(mappedBy = "messageSender")
    @JsonIgnore
    var messageSender: Message? = null,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "UserProfile_Message",
        joinColumns = [JoinColumn(name = "message_id")],
        inverseJoinColumns = [JoinColumn(name = "profile_id")],
    )
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    var messageRead: MutableSet<Message>? = HashSet(),

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
) {

    // Custom methods
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is UserProfile) return false

        other as UserProfile

        if(profile_id != other.profile_id) return false
        if(username != other.username) return false
        if(emoji != other.emoji) return false
        if(program != other.program) return false
        if(year_of_graduation != other.year_of_graduation) return false
        if(university != other.university) return false
        return userAccount == other.userAccount
    }

    override fun hashCode(): Int {
        var result = profile_id?.hashCode() ?: 0
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (emoji?.hashCode() ?: 0)
        result = 31 * result + (program?.hashCode() ?: 0)
        result = 31 * result + (year_of_graduation?.hashCode() ?: 0)
        result = 31 * result + (university?.hashCode() ?: 0)

        return result
    }

    override fun toString(): String {
        return "UserProfile(profile_id=${this.profile_id}, username=${this.username}, emoji=${this.emoji}, program=${this.program}, year_of_graduation=${this.year_of_graduation}, university=${this.university}, user_account=${this.userAccount}, courses=${this.courses}, session=${this.session}"
    }
}
