package com.geogrind.geogrindbackend.models.sessions

import com.fasterxml.jackson.annotation.JsonIgnore
import com.geogrind.geogrindbackend.models.courses.Courses
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@Table(name = "sessions")
@EntityListeners(AuditingEntityListener::class)
data class Sessions(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "session_id", columnDefinition = "uuid", nullable = false, updatable = false, unique = true)
    val sessionId: UUID? = null,

    // One-To-One relationship with the course id
    @OneToOne(targetEntity = Courses::class, cascade = [CascadeType.ALL])
    @JsonIgnore
    @JoinColumn(name = "fk_course_id", referencedColumnName = "course_id")
    var course: Courses,

    // Many-To-One relationship with the user profile
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    @JsonIgnore
    var profile: UserProfile,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startTime")
    var startTime: Date? = null,

    @Column(name = "numberOfLikers", nullable = false)
    @Size(min = 0)
    var numberOfLikers: Int? = 0,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "stopTime", nullable = false)
    var stopTime: Date? = null,

    @Column(name = "description", nullable = true)
    var description: String? = null,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    var createAt: Date? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
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