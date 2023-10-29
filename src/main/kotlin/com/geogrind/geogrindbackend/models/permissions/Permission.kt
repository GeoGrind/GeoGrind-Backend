package com.geogrind.geogrindbackend.models.permissions

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener::class)
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", unique = true, nullable = false)
    @Size(min = 5)
    var permission_id: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_name", length = 100, nullable = false)
    @Size(min = 5)
    var permission_name: PermissionName,

    @Column(name = "fk_user_account_id", nullable = false)
    @Size(min = 5)
    var fkUserAccountId: UUID,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
)
