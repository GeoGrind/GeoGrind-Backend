package com.geogrind.geogrindbackend.models.sessions

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.geogrind.geogrindbackend.dto.session.SuccessSessionResponse
import com.geogrind.geogrindbackend.models.courses.Courses
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@Table(name = "sessions")
@EntityListeners(AuditingEntityListener::class)
data class Sessions (
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "session_id", columnDefinition = "uuid", nullable = false, updatable = false, unique = true)
    val sessionId: UUID? = null,

    // One-To-One relationship with the course id
    @OneToOne(fetch = FetchType.EAGER, targetEntity = Courses::class, cascade = [CascadeType.MERGE])
    @JsonIgnore
    @JoinColumn(name = "fk_course_id", referencedColumnName = "course_id")
    var course: Courses,

    // One-To-One relationship with the user profile
    @OneToOne(fetch = FetchType.EAGER, targetEntity = UserProfile::class, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "fk_user_profile_id", referencedColumnName = "profile_id")
    @JsonIgnore
    var profile: UserProfile? = null,

    @Column(name = "startTime", nullable = false)
    @JsonIgnore
    var startTime: Instant ?= Instant.now(),

    @Column(name = "numberOfLikers", nullable = false)
    @Size(min = 0)
    var numberOfLikers: Int? = 0,

    @Column(name = "stopTime", nullable = false)
    @JsonIgnore
    var stopTime: Instant ?= Instant.now(),

    @Column(name = "description", nullable = true)
    var description: String? = null,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    var createAt: Date? = Date(),

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
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

fun Sessions.toSuccessHttpResponse(): SuccessSessionResponse {
    return SuccessSessionResponse(
        sessionId = this.sessionId,
        course = this.course,
        startTime = this.startTime,
        stopTime = this.stopTime,
        numberOfLikers = this.numberOfLikers,
        description = this.description,
    )
}

fun List<Sessions>.toSuccessHttpResponseList(): List<SuccessSessionResponse> {
    return this.map {
        SuccessSessionResponse(
            sessionId = it.sessionId,
            course = it.course,
            startTime = it.startTime,
            stopTime = it.stopTime,
            numberOfLikers = it.numberOfLikers,
            description = it.description,
        )
    }
}