package com.geogrind.geogrindbackend.models.permissions

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*

@Entity
@Table(name = "permissions")
//@EntityListeners(AuditingEntityListener::class)
data class Permissions (

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "permission_id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
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
