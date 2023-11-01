package com.geogrind.geogrindbackend.services.user_profile

import com.geogrind.geogrindbackend.dto.user_profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.user_profile.UpdateUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface UserProfileService {
    suspend fun getAllUserProfile(): List<UserProfile>
    suspend fun getUserProfileByUserAccountId(
        @Valid requestDto: GetUserProfileByUserAccountIdDto
    ): UserProfile
    suspend fun updateUserProfileByUserAccountId(
        user_account_id: UUID,
        @Valid requestDto: UpdateUserProfileByUserAccountIdDto
    ): UserProfile
}