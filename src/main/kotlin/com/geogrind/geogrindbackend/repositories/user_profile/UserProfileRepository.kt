package com.geogrind.geogrindbackend.repositories.user_profile

import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
}