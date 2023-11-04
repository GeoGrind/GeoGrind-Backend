package com.geogrind.geogrindbackend.services.profile

import com.geogrind.geogrindbackend.dto.profile.CreateUserProfileDto
import com.geogrind.geogrindbackend.dto.profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.profile.UpdateUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileBadRequestException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.utils.Validation.registration.UserAccountValidationHelper
import com.geogrind.geogrindbackend.utils.Validation.registration.UserAccountValidationHelperImpl
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.HashMap

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
    ): UserProfile {

        // find the user account
        val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(requestDto.user_account_id)

        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException(requestDto.user_account_id.toString())
        }

        val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(findUserAccount.get())

        if(findUserProfile.isEmpty) {
            throw UserProfileNotFoundException("Cannot find user profile with profile id: ${requestDto.user_account_id}")
        }

        return findUserProfile.get()
    }

    // create an empty user profile
    @Transactional
    override suspend fun createEmptyUserProfile(
        @Valid requestDto: CreateUserProfileDto
    ): UserProfile {
        var username: String = requestDto.username
        var user_account: UserAccount = requestDto.user_account

        // check if this user_account has the profile before


        val new_user_profile: UserProfile = UserProfile(
            username = username,
            userAccount = user_account,
            createdAt = Date(),
            updatedAt = Date(),
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
        var program: String? = requestDto.program
        var year_of_graduation: Int? = requestDto.year_of_graduation
        var university: String? = requestDto.university

        // find the user account that is linked to this profile
        var findUserAccount: UserAccount = userAccountRepository.findById(user_account_id).orElse(null)

        if(findUserAccount == null) {
            throw UserAccountNotFoundException(user_account_id.toString())
        }

        log.info("User id: $user_account_id")

        // find the user profile
        var findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            user_account = findUserAccount
        )

        if(findUserProfile.isEmpty) {
            throw UserAccountNotFoundException(user_account_id.toString())
        }

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

        log.info("Profile: ${findUserProfile.get()}, Username: $username, Emoji: $emoji, Program: $program, Year of grad: $year_of_graduation, University: $university")

        // save the user profile to the database
        findUserProfile.get().apply {
            this.username = username ?: findUserAccount.username
            this.emoji = emoji ?: this.emoji
            this.program = program ?: this.program
            this.year_of_graduation = year_of_graduation ?: this.year_of_graduation
            this.university = university ?: this.university
        }

        findUserProfile.get().updatedAt = Date()

        userProfileRepository.save(findUserProfile.get())

        return findUserProfile.get()
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserProfileServiceImpl::class.java)
    }
}