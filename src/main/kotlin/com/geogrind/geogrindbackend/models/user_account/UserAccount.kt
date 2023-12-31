package com.geogrind.geogrindbackend.models.user_account

import com.fasterxml.jackson.annotation.JsonIgnore
import com.geogrind.geogrindbackend.dto.registration.SuccessUserAccountResponse
import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    val id: UUID? = null,

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

    @Column(name = "temp_token", length = 100000, unique = true, nullable = true)
    @Size(min = 3)
    var temp_token: String? = null,

    @OneToMany
    @JoinColumn(name = "id")
    @JsonIgnore
    var permissions: MutableSet<Permissions>? = HashSet(),

    // one-to-one relationship with the user_profile table
    @OneToOne(mappedBy = "userAccount")
    @JsonIgnore
    var userProfile: UserProfile? = null,

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