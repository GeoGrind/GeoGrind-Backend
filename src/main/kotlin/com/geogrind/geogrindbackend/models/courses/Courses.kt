package com.geogrind.geogrindbackend.models.courses

import com.fasterxml.jackson.annotation.JsonIgnore
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "courses")
@EntityListeners(AuditingEntityListener::class)
data class Courses (
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "course_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    var courseId: UUID? = null,

    // Many-To-One relationship with the user profile entity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    @JsonIgnore
    var profile: UserProfile,

    @Column(name = "course_code", length = 10, nullable = false)
    @Size(min = 5)
    var courseCode: String,

    @Column(name = "course_name", length = 50, nullable = false)
    @Size(min = 5)
    var courseName: String,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
) {
    override fun hashCode(): Int {
        var result = courseId?.hashCode() ?: 0
        result = 31 * result + (courseCode?.hashCode() ?: 0)
        result = 31 * result + (courseName?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Course(id=${this.courseId}, name=${this.courseName}, code=${this.courseCode}"
    }
}