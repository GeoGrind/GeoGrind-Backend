package com.geogrind.geogrindbackend.services.profile

import com.geogrind.geogrindbackend.dto.profile.CreateUserProfileDto
import com.geogrind.geogrindbackend.dto.profile.DeleteCoursesDto
import com.geogrind.geogrindbackend.dto.profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.profile.UpdateUserProfileByUserAccountIdDto
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
    suspend fun createEmptyUserProfile(
        @Valid requestDto: CreateUserProfileDto
    ): UserProfile
    suspend fun updateUserProfileByUserAccountId(
        @Valid requestDto: UpdateUserProfileByUserAccountIdDto
    ): UserProfile
    suspend fun deleteCourseFromProfile(
        @Valid requestDto: DeleteCoursesDto
    )
}