package com.geogrind.geogrindbackend.services.registration

import com.geogrind.geogrindbackend.dto.registration.*
import com.geogrind.geogrindbackend.dto.sendgrid.DeleteUserAccountConfirmationDto
import com.geogrind.geogrindbackend.dto.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.dto.sendgrid.UpdatePasswordConfirmationDto
import com.geogrind.geogrindbackend.dto.sendgrid.VerifyEmailUserAccountDto
import com.geogrind.geogrindbackend.dto.profile.CreateUserProfileDto
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountBadRequestException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountConflictException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountUnauthorizedException
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.services.profile.UserProfileService
import com.geogrind.geogrindbackend.utils.AutoGenerate.GenerateRandomHelper
import com.geogrind.geogrindbackend.utils.AutoGenerate.GenerateRandomHelperImpl
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelperImpl
import com.geogrind.geogrindbackend.utils.Twilio.user_account.EmailServiceImpl
import com.geogrind.geogrindbackend.utils.Validation.registration.UserAccountValidationHelper
import com.geogrind.geogrindbackend.utils.Validation.registration.UserAccountValidationHelperImpl
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

@Service
@CacheConfig(cacheNames = ["userAccountCache"])
class UserAccountServiceImpl(
    private val userAccoutRepository: UserAccountRepository,
    private val userProfileService: UserProfileService,
) : UserAccountService {

    private val validationObj: UserAccountValidationHelper = UserAccountValidationHelperImpl()

    private val BCryptObj: BcryptHashPasswordHelper = BcryptHashPasswordHelperImpl()

    private val generateRandomHelper: GenerateRandomHelper = GenerateRandomHelperImpl()

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val sendGridApiKey = dotenv["SENDGRID_API_KEY"]

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    // get all user accounts
    @Cacheable(cacheNames = ["userAccounts"])
    @Transactional(readOnly = true)
    override suspend fun getAllUserAccounts(): List<UserAccount> {
        waitSomeTime()
        return userAccoutRepository.findAll()
    }

    // get the user account by an user_id
    @Cacheable(cacheNames = ["userAccounts"], key = " '#requestDto.user_id' ", unless = " #result == null ")
    @Transactional(readOnly = true)
    override suspend fun getUserAccountById(@Valid requestDto: GetUserAccountByIdDto): UserAccount {
        try {
            waitSomeTime() // waiting for Redis
            val user_account: Optional<UserAccount> = userAccoutRepository.findById(requestDto.user_id)
            return user_account.get()
        } catch (e: UserAccountNotFoundException) {
            throw e
        } catch (e: RuntimeException) {
            val error_map: MutableMap<String, String?> = mutableMapOf()
            error_map["error"] = e.message
            throw UserAccountBadRequestException(error_map as MutableMap<String, String>)
        }
    }

    // create new user account in the database
    @CacheEvict(cacheNames = ["userAccounts"], allEntries = true)
    @Transactional
    override suspend fun createUserAccount(@Valid requestDto: CreateUserAccountDto): SendGridResponseDto {
        var email: String = requestDto.email
        var username: String = requestDto.username
        var password: String = requestDto.password
        var confirm_password: String = requestDto.confirm_password

        // validate the user email, username and password
        var registration_form_validation: MutableMap<String, String> = HashMap()

        validationObj.validateEmail(
            email,
            registration_form_validation
        )

        validationObj.validateUsername(
            username,
            registration_form_validation
        )

        validationObj.validatePassword(
            password,
            username,
            registration_form_validation
        )

        validationObj.validateConfirmPassword(
            password,
            confirm_password,
            registration_form_validation
        )

        // check if there is any error
        if(registration_form_validation.isNotEmpty()) {
            throw UserAccountBadRequestException(registration_form_validation)
        }

        // check if the email has already been used
        val find_user_with_email = userAccoutRepository.findUserAccountByEmail(email).orElse(null)
        val find_user_with_username = userAccoutRepository.findUserAccountByUsername(username).orElse(null)

        if(find_user_with_email != null || find_user_with_username != null) {
            val conflictingField: String = find_user_with_email?.email ?: find_user_with_username?.username ?: ""
            throw UserAccountConflictException(conflictingField)
        }

        // hash the password
        val hashed_password = BCryptObj.hashPassword(password)

        // Procceed with the user creation
        val newUserAccount = UserAccount(
            email = email,
            username = username,
            hashed_password = hashed_password,
            createdAt = Date(),
            permissions = mutableSetOf(),
            updatedAt = Date(),
        )

        val savedUserAccount = userAccoutRepository.save(newUserAccount)

        val otp_code: String = generateRandomHelper.generateOTP(6)

        // send the request to the sendgrid server to send the confirmation email to the user
        val sendgrid_response: SendGridResponseDto = EmailServiceImpl(
            sendGridApiKey = sendGridApiKey,
            geogrindSecretKey = geogrindSecretKey,
        ).sendEmailConfirmation(
            user_email = email,
            geogrind_otp_code = otp_code,
            user_id = savedUserAccount.id.toString(),
        )

        val statusCode: Int = sendgrid_response.statusCode
        val sendGridResponse: String = sendgrid_response.sendGridResponse
        val token: String = sendgrid_response.token

        println(statusCode)
        println(sendGridResponse)
        println(token)

        if(statusCode == 202) {
            savedUserAccount.temp_token = token
        }

        return sendgrid_response
    }

    @CacheEvict(cacheNames = ["userAccounts"], allEntries = true)
    @Transactional
    override suspend fun updateUserAccountById(
        user_id: UUID,
        @Valid requestDto: UpdateUserAccountDto
    ): SendGridResponseDto {
        var update_password: String = requestDto.update_password
        var confirm_update_password: String = requestDto.confirm_update_password

        // check whether the user id exists in the database
        var findUserAccount = userAccoutRepository.findById(user_id)

        if(findUserAccount == null) {
            throw UserAccountNotFoundException(user_id.toString())
        }

        var update_registration_form: MutableMap<String, String> = java.util.HashMap()

        validationObj.validatePassword(
            update_password,
            findUserAccount.get().username,
            update_registration_form
        )

        validationObj.validateConfirmPassword(
            confirm_update_password,
            update_password,
            update_registration_form,
        )

        if(update_registration_form.isNotEmpty()) {
            throw UserAccountBadRequestException(update_registration_form)
        }

        // check if the new password is the same as old password
        if(BCryptObj.verifyPassword(update_password, findUserAccount.get().hashed_password)) {
            throw UserAccountConflictException("New password must be different from old password!")
        }

        val otp_code: String = generateRandomHelper.generateOTP(6)

        // send the confirm password email
        val sendgrid_response: SendGridResponseDto = EmailServiceImpl(
            sendGridApiKey = sendGridApiKey,
            geogrindSecretKey = geogrindSecretKey,
        ).sendChangePassword(
            user_email = findUserAccount.get().email,
            geogrind_otp_code = otp_code,
            user_id = user_id.toString(),
            new_password = update_password,
        )

        val statusCode: Int = sendgrid_response.statusCode
        val sendGridResponse: String = sendgrid_response.sendGridResponse
        val token: String = sendgrid_response.token

        println(statusCode)
        println(sendGridResponse)
        println(token)

        if(statusCode == 202) {
            findUserAccount.get().temp_token = token
        }

        userAccoutRepository.save(findUserAccount.get())

        return sendgrid_response
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["userAccounts"], key = " '#requestDto.user_id' "),
        CacheEvict(cacheNames = ["userAccounts"], allEntries = true)
    ])
    @Transactional
    override suspend fun deleteUserAccountById(
        @Valid requestDto: DeleteUserAccountDto
    ): SendGridResponseDto {
        return if (userAccoutRepository.existsById(requestDto.user_id)) {
            val findUserAccount = userAccoutRepository.findById(
                requestDto.user_id
            )

            val otp_code: String = generateRandomHelper.generateOTP(6)

            // send the confirm account deletion email
            val sendgrid_response: SendGridResponseDto = EmailServiceImpl(
                sendGridApiKey = sendGridApiKey,
                geogrindSecretKey = geogrindSecretKey,
            ).sendDeleteAccount(
                user_email = findUserAccount.get().email,
                geogrind_otp_code = otp_code,
                user_id = requestDto.user_id.toString()
            )

            val statusCode: Int = sendgrid_response.statusCode
            val sendGridResponse: String = sendgrid_response.sendGridResponse
            val token: String = sendgrid_response.token

            println("Status code is $statusCode\n The response body is $sendGridResponse\n The token is $token")

            if(statusCode == 202) {
                findUserAccount.get().temp_token = token
            }

            return sendgrid_response
        } else throw UserAccountNotFoundException(requestDto.user_id.toString())
    }

    // sendgrid email verification service
    @Cacheable(cacheNames = ["userAccounts"], key = " '#requestDto.user_account_id' ", unless = " '#result == null' ")
    @Transactional
    override suspend fun getEmailVerification(@Valid requestDto: VerifyEmailUserAccountDto): UserAccount {

        waitSomeTime() // wait for Redis

        val findUserAccount = userAccoutRepository.findById(requestDto.user_account_id)

        // check the temp token
        if(!findUserAccount.get().temp_token.equals(requestDto.token)) {
            throw UserAccountUnauthorizedException("Unauthorized user!")
        }

        // verify the user to the system
        findUserAccount.get().account_verified = true
        findUserAccount.get().updatedAt = Date()

        // create an empty profile for the user
        val empty_user_profile: UserProfile = userProfileService.createEmptyUserProfile(
            CreateUserProfileDto(
                username = findUserAccount.get().username,
                user_account = findUserAccount.get(),
            )
        )

        return findUserAccount.get()
    }

    @Cacheable(cacheNames = ["userAccounts"], key = " 'requestDto.user_account_id' ", unless = " '#result == null' ")
    @Transactional
    override suspend fun getConfirmPasswordChangeVerification(@Valid requestDto: UpdatePasswordConfirmationDto): UserAccount {
        waitSomeTime() // waiting for Redis

        val findUserAccount = userAccoutRepository.findById(requestDto.user_account_id)

        // check the temp token
        if(!findUserAccount.get().temp_token.equals(requestDto.token)) {
            throw UserAccountUnauthorizedException("Unauthorized user!")
        }

        // hash the user new password
        val hashed_password: String = BCryptObj.hashPassword(requestDto.new_password)

        findUserAccount.get().hashed_password = hashed_password

        return findUserAccount.get()
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["userAccounts"], key = " '#requestDto.user_account_id' "),
        CacheEvict(cacheNames = ["userAccounts"], allEntries = true)
    ])
    @Transactional
    override suspend fun getDeleteAccountVerification(@Valid requestDto: DeleteUserAccountConfirmationDto) {

        val findUserAccount = userAccoutRepository.findById(requestDto.user_account_id)
        println("Temp_token: ${findUserAccount.get().temp_token}")

        // check the temp token
        if(!findUserAccount.get().temp_token.equals(requestDto.token)) {
            throw UserAccountUnauthorizedException("Unauthorized user!")
        }

        // delete the user account
        userAccoutRepository.deleteById(requestDto.user_account_id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserAccountService::class.java)
        private fun waitSomeTime() {
            log.info("Long Wait Begin")
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            println("Long Wait End")
        }
    }
}