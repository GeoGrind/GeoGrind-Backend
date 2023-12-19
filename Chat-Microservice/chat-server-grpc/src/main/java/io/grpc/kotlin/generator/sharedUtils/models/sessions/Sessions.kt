package io.grpc.kotlin.generator.sharedUtils.models.sessions

import com.fasterxml.jackson.annotation.JsonIgnore
import io.grpc.kotlin.generator.sharedUtils.models.courses.Courses
import io.grpc.kotlin.generator.sharedUtils.models.scheduling.UUIDSerializer
import io.grpc.kotlin.generator.sharedUtils.models.user_profile.UserProfile
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@Table(name = "sessions")
@EntityListeners(AuditingEntityListener::class)
@Serializable
data class Sessions (
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "session_id", columnDefinition = "uuid", nullable = false, updatable = false, unique = true)
    @Serializable(with = UUIDSerializer::class)
    val sessionId: UUID? = null,

    // One-To-One relationship with the course id
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JsonIgnore
    @JoinColumn(name = "fk_course_id", referencedColumnName = "course_id", nullable = false)
    @kotlinx.serialization.Transient
    var course: Courses? = null,

    // One-To-One relationship with the user profile
    @OneToOne(fetch = FetchType.EAGER, targetEntity = UserProfile::class, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "fk_user_profile_id", referencedColumnName = "profile_id")
    @JsonIgnore
    @kotlinx.serialization.Transient
    var profile: UserProfile? = null,

    @Column(name = "startTime", nullable = false)
    @JsonIgnore
    @kotlinx.serialization.Transient
    var startTime: Instant ?= Instant.now(),

    @Column(name = "numberOfLikers", nullable = false)
    @Size(min = 0)
    @kotlinx.serialization.Transient
    var numberOfLikers: Int? = 0,

    @Column(name = "stopTime", nullable = false)
    @JsonIgnore
    @kotlinx.serialization.Transient
    var stopTime: Instant ?= Instant.now(),

    @Column(name = "description", nullable = true)
    @kotlinx.serialization.Transient
    var description: String? = null,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    @kotlinx.serialization.Transient
    var createAt: Date? = Date(),

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @kotlinx.serialization.Transient
    var updatedAt: Date? = Date(),
) {
    override fun hashCode(): Int {
        var result = sessionId?.hashCode() ?: 0
        result = 31 * result + (course?.hashCode() ?: 0)
        result = 31 * result + (startTime?.hashCode() ?: 0)
        result = 31 * result + (numberOfLikers?.hashCode() ?: 0)
        result = 31 * result + (stopTime?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)

        return result
    }

    override fun toString(): String {
        return "Session(sessionId=$sessionId, course=$course, startTime=$startTime, stopTime=$stopTime, description=$description"
    }
}