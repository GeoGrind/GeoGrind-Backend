package com.geogrind.geogrindbackend.repositories.sessions

import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface SessionsRepository : JpaRepository<Sessions, UUID> {
    fun findByProfile(userProfile: UserProfile): Optional<Sessions>
}