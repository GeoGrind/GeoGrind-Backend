package com.geogrind.geogrindbackend.services.user_profile

import com.geogrind.geogrindbackend.dto.user_profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.user_profile.UpdateUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileServiceImpl(
    private val userProfileRepository: UserProfileRepository,
) : UserProfileService {

    // get all the users profiles
    @Transactional(readOnly = true)
    override suspend fun getAllUserProfile(): List<UserProfile> {
        return userProfileRepository.findAll()
    }

    // get user profile by id
    @Transactional(readOnly = true)
    override suspend fun getUserProfileByUserAccountId(
        @Valid requestDto: GetUserProfileByUserAccountIdDto
    ): UserProfile = userProfileRepository.findById(requestDto.profile_id)
        .orElseThrow { UserProfileNotFoundException("Cannot find user profile with profile id: ${requestDto.profile_id}") }

    // update user account by id
    @Transactional
    override suspend fun updateUserProfileByUserAccountId(
        user_account_id: UUID,
        @Valid requestDto: UpdateUserProfileByUserAccountIdDto
    ): UserProfile {

    }
}