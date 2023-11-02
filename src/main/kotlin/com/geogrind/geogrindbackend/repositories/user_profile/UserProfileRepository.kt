package com.geogrind.geogrindbackend.repositories.user_profile

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
    // find by user account
    fun findUserProfileByUserAccount(user_account: UserAccount): Optional<UserProfile>
}