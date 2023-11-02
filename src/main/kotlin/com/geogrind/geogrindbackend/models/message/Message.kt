package com.geogrind.geogrindbackend.models.message

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import java.util.UUID

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date

@Entity
@Table(name = "message")
@EntityListeners(AuditingEntityListener::class)
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @Size(min = 5)
    var id: UUID? = null,

    @Column(name = "email", length = 100, unique = true, nullable = false)
    @Size(min = 5)
    var email: String,

    // Every table has this
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = null,

    // Every table has this
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
) {


}

