package io.grpc.kotlin.generator.models.theme

import com.fasterxml.jackson.annotation.JsonIgnore
import io.grpc.kotlin.generator.models.chatroom.ChatRoom
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@Table(name = "theme")
@EntityListeners(AuditingEntityListener::class)
data class Theme (
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "chatroom_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    val themeId: UUID? = null,

    @OneToOne(targetEntity = ChatRoom::class, cascade = [CascadeType.ALL])
    @JsonIgnore
    @JoinColumn(name = "fk_chatroom_id", referencedColumnName = "chatroom_id")
    var chatRoom: ChatRoom,

    @Column(name = "theme_types", unique = false, nullable = false)
    @Size(min = 3)
    @Enumerated(EnumType.STRING)
    val themeType: ThemeTypes,

    @Column(name = "theme_descriptions", length = 10000, unique = false, nullable = false)
    @Size(min = 3)
    val themeDescription: String? = null,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
)
