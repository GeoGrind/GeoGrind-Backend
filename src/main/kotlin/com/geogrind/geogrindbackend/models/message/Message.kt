package com.geogrind.geogrindbackend.models.message

import com.geogrind.geogrindbackend.dto.message.SuccessUserMessageResponse
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.UUID
import java.util.Date

@Entity
@Table(name = "message")
@EntityListeners(AuditingEntityListener::class)
data class Message(

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
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
    @Column(name = "created_at", nullable = false)
    var createdAt: Date,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
)
fun List<Message>.toSuccessHttpResponseList(): List<SuccessUserMessageResponse> {
    return this.map {
        SuccessUserMessageResponse(
            id = it.id,
            authorId = it.userAccount.id as UUID,
            text = it.text,
            type = it.type,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt
        )
    }
}
