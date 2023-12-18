package com.geogrind.geogrindbackend.models.permissions

import com.fasterxml.jackson.annotation.JsonIgnore
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener::class)
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

    // Many-to-one relationship with the user account entity
    @ManyToOne
    @JoinColumn(name = "id", insertable = false, updatable = false)
    @JsonIgnore
    var userAccount: UserAccount,

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
        try {
            var result = permission_id?.hashCode() ?: 0
            result = 31 * result + (permission_name?.hashCode() ?: 0)

            val userAccountIdHashCode = userAccount.id?.hashCode() ?: 0
            result = 31 * result + userAccountIdHashCode

            return result
        } catch (e: Exception) {
            // Log the exception
            log.info("Exception in hashCode: ${e.message}")
            throw e
        }
    }

    override fun toString(): String {
        return "Permission(permissionId=$permission_id, permissionName=$permission_name, userAccountId=${userAccount.id})"
    }

    companion object {
        private val log = LoggerFactory.getLogger(Permissions::class.java)
    }
}
