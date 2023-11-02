package com.geogrind.geogrindbackend.models.user_profile

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date
import java.util.UUID

// user profile model
@Entity
@Table(name = "user_profile")
@EntityListeners(AuditingEntityListener::class)
data class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", unique = true, nullable = false)
    @Size(min = 5)
    var profile_id: UUID? = null,

    @Column(name = "username", length = 50, unique = true, nullable = false)
    @Size(min = 3)
    var username: String,

    @Column(name = "emoji", length = 1000, nullable = false)
    @Size(min = 3)
    var emoji: String? = "No emoji set",

    @Enumerated(EnumType.STRING)
    @Column(name = "program", length = 100, nullable = true)
    @Size(min = 5)
    var program: Program? = null,

    @Column(name = "year_of_graduation", length = 4, nullable = true)
    @Size(min = 4)
    var year_of_graduation: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "university", length = 100, nullable = false)
    @Size(min = 5)
    var university: University? = University.UNIVERSITY_OF_WATERLOO,

    // One to one relationship with the user account
    @OneToOne(targetEntity = UserAccount::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id", referencedColumnName = "profile_id")
    var user_account: UserAccount,

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
        if(other !is UserProfile) return false

        other as UserProfile

        if(profile_id != other.profile_id) return false
        if(username != other.username) return false
        if(emoji != other.emoji) return false
        if(program != other.program) return false
        if(year_of_graduation != other.year_of_graduation) return false
        if(university != other.university) return false
        if(user_account != other.user_account) return false

        return true
    }

    override fun hashCode(): Int {
        var result = profile_id?.hashCode() ?: 0
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (emoji?.hashCode() ?: 0)
        result = 31 * result + (program?.hashCode() ?: 0)
        result = 31 * result + (year_of_graduation?.hashCode() ?: 0)
        result = 31 * result + (university?.hashCode() ?: 0)
        result = 31 * result + (user_account?.hashCode() ?: 0)

        return result
    }

    override fun toString(): String {
        return "UserProfile(profile_id=${this.profile_id}, username=${this.username}, emoji=${this.emoji}, program=${this.program}, year_of_graduation=${this.year_of_graduation}, university=${this.university}, user_account=${this.user_account}"
    }
}
