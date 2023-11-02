package com.geogrind.geogrindbackend.services.user_profile

import com.geogrind.geogrindbackend.dto.user_profile.CreateUserProfileDto
import com.geogrind.geogrindbackend.dto.user_profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.user_profile.UpdateUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileBadRequestException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.Program
import com.geogrind.geogrindbackend.models.user_profile.University
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.utils.Validation.UserAccountValidationHelper
import com.geogrind.geogrindbackend.utils.Validation.UserAccountValidationHelperImpl
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Service
class UserProfileServiceImpl(
    private val userProfileRepository: UserProfileRepository,
    private val userAccountRepository: UserAccountRepository,
) : UserProfileService {

    private val validationObj: UserAccountValidationHelper = UserAccountValidationHelperImpl()

    // get all the users profiles
    @Transactional(readOnly = true)
    override suspend fun getAllUserProfile(): List<UserProfile> {
        return userProfileRepository.findAll()
    }

    // get user profile by user account id
    @Transactional(readOnly = true)
    override suspend fun getUserProfileByUserAccountId(
        @Valid requestDto: GetUserProfileByUserAccountIdDto
    ): UserProfile = userProfileRepository.findById(requestDto.profile_id)
        .orElseThrow { UserProfileNotFoundException("Cannot find user profile with profile id: ${requestDto.profile_id}") }

    // create an empty user profile
    @Transactional
    override suspend fun createEmptyUserProfile(
        @Valid requestDto: CreateUserProfileDto
    ): UserProfile {
        var username: String = requestDto.username
        var user_account: UserAccount = requestDto.user_account

        val new_user_profile: UserProfile = UserProfile(
            username = username,
            user_account = user_account
        )

        return userProfileRepository.save(new_user_profile)
    }

    // update user profile by user account id
    @Transactional
    override suspend fun updateUserProfileByUserAccountId(
        user_account_id: UUID,
        @Valid requestDto: UpdateUserProfileByUserAccountIdDto
    ): UserProfile {
        var username: String? = requestDto.username
        var emoji: String? = requestDto.emoji
        var program: Program? = requestDto.program
        var year_of_graduation: Int? = requestDto.year_of_graduation
        var university: University? = requestDto.university

        // find the user account that is linked to this profile
        var findUserAccount: UserAccount = userAccountRepository.findById(user_account_id).orElse(null)

        if(findUserAccount == null) {
            throw UserAccountNotFoundException(user_account_id.toString())
        }

        // find the user profile
        var findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUser_account(
            user_account = findUserAccount
        )

        var update_profile_errors: MutableMap<String, String> = HashMap()

        if(username != null) {
            validationObj.validateUsername(
                username,
                update_profile_errors,
            )
        }

        if(update_profile_errors.isNotEmpty()) {
            throw UserProfileBadRequestException(update_profile_errors)
        }

        // save the user profile to the database
        findUserProfile.get().apply {
            username = username ?: findUserAccount.username
            emoji = emoji ?: this.emoji
            program = program ?: this.program
            year_of_graduation = year_of_graduation ?: this.year_of_graduation
            university = university ?: this.university
        }

        userProfileRepository.save(findUserProfile.get())

        return findUserProfile.get()
    }
}