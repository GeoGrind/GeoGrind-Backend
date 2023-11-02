package com.geogrind.geogrindbackend.models.message

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.UUID
import java.util.Date

@Entity
@Table(name = "message")
@EntityListeners(AuditingEntityListener::class)
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", referencedColumnName = "id",nullable = false)
    var userAccount: UserAccount,

    @Column(name = "text", nullable = false)
    var text: String,

    @Column(name = "type", nullable = false)
    var type: String,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdat", nullable = false)
    var createdAt: Date
)
