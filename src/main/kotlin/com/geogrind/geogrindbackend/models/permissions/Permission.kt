package com.geogrind.geogrindbackend.models.permissions

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener::class)
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", unique = true, nullable = false)
    @Size(min = 5)
    var permission_id: UUID? = null,

    @Column(name = "permission_name", length = 100, unique = true, nullable = false)
    @Size(min = 5)
    var permission_name: String,

    @Column(name = "user_id", length = 1000, unique = true, nullable = false)
    @Size(min = 5)
    var user_id: UUID,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    val user_account: UserAccount,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var createdAt: Date? = null,

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
) {

    // Custom methods
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Permission) return false

        other as Permission

        if(permission_id != other.permission_id) return false
        if(permission_name != other.permission_name) return false
        if(user_id != other.user_id) return false
        if(user_account != other.user_account) return false

        return true
    }

    override fun hashCode(): Int {
        var result = permission_id?.hashCode() ?: 0
        result = 31 * result + (permission_name?.hashCode() ?: 0)
        result = 31 * result + (user_id?.hashCode() ?: 0)
        result = 31 * result + (user_account?.hashCode() ?: 0)

        return result
    }

    override fun toString(): String {
        return "Permission(id=${this.permission_id}, name=${permission_name}, user_id=${this.user_id}, user_account=${this.user_account})"
    }
}
