package com.geogrind.geogrindbackend.models.user_account

import com.geogrind.geogrindbackend.dto.registration.SuccessUserAccountResponse
import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import java.util.UUID

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date

@Entity
@Table(name = "user_account")
@EntityListeners(AuditingEntityListener::class)
data class UserAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @Size(min = 5)
    var id: UUID? = null,

    @Column(name = "email", length = 100, unique = true, nullable = false)
    @Size(min = 5)
    var email: String,

    @Column(name = "username", length = 50, unique = true, nullable = false)
    @Size(min = 3)
    var username: String,

    @Column(name = "hashed_password", length = 100, unique = true, nullable = false)
    @Size(min = 8)
    var hashed_password: String,

    @Column(name = "account_verified", unique = false, nullable = false)
    var account_verified: Boolean? = false,

    @Column(name = "temp_token", length = 1000, unique = true, nullable = true)
    @Size(min = 3)
    var temp_token: String? = null,

    // TO-DO: permissions whether user can go into a certain resource
    @OneToMany(targetEntity = Permission::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "fk_user_account_id", referencedColumnName = "id")
    var permissions: Set<Permission> = emptySet(),

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
        if(other !is UserAccount) return false

        other as UserAccount

        if(id != other.id) return false
        if(username != other.username) return false
        if(email != other.email) return false
        if(hashed_password != other.hashed_password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (hashed_password?.hashCode() ?: 0)

        return result
    }

    override fun toString(): String {
        return "User(id=${this.id}, email=${this.email}, username=${this.username})"
    }
}

fun UserAccount.toSuccessHttpResponse(): SuccessUserAccountResponse {
    return SuccessUserAccountResponse(
        id = this.id,
        username = this.username,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

fun List<UserAccount>.toSuccessHttpResponseList(): List<SuccessUserAccountResponse> {
    return this.map {
        SuccessUserAccountResponse(
            id = it.id,
            username = it.username,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
        )
    }
}