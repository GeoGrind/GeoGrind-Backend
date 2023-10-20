package com.geogrind.geogrindbackend.services.registration

import com.geogrind.geogrindbackend.dto.registration.CreateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.DeleteUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.GetUserAccountByIdDto
import com.geogrind.geogrindbackend.dto.registration.UpdateUserAccountDto
import com.geogrind.geogrindbackend.exceptions.registration.UserAccountBadRequestException
import com.geogrind.geogrindbackend.exceptions.registration.UserAccountConflictException
import com.geogrind.geogrindbackend.exceptions.registration.UserAccountNotFoundException
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelperImpl
import com.geogrind.geogrindbackend.utils.Validation.UserAccountValidationHelper
import com.geogrind.geogrindbackend.utils.Validation.UserAccountValidationHelperImpl
import jakarta.validation.Valid
import org.apache.catalina.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.MethodArgumentNotValidException
import java.util.UUID

@Service
class UserAccountServiceImpl(private val userAccoutRepository: UserAccountRepository) : UserAccountService {

    private val validationObj: UserAccountValidationHelper = UserAccountValidationHelperImpl()

    private val BCryptObj: BcryptHashPasswordHelper = BcryptHashPasswordHelperImpl()

    // get all user accounts
    @Transactional(readOnly = true)
    override suspend fun getAllUserAccounts(): List<UserAccount> = userAccoutRepository.findAll()

    // get the user account by an user_id
    @Transactional(readOnly = true)
    override suspend fun getUserAccountById(@Valid requestDto: GetUserAccountByIdDto): UserAccount = userAccoutRepository.findById(
         requestDto.user_id
    )
        .orElseThrow { UserAccountNotFoundException(
            requestDto.user_id.toString()
        ) }

    // create new user account in the database
    @Transactional
    override suspend fun createUserAccount(@Valid requestDto: CreateUserAccountDto): UserAccount {
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
            username,
            password,
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
        val find_user_with_email = userAccoutRepository.findUserAccountByEmail(email)

        val find_user_with_username = userAccoutRepository.findUserAccountByUsername(username)

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
            hashed_password = hashed_password
        )

        val savedUserAccount = userAccoutRepository.save(newUserAccount)
        return savedUserAccount
    }

    @Transactional
    override suspend fun updateUserAccountById(
        @Valid requestDto: UpdateUserAccountDto
    ): UserAccount {
        var user_id: UUID = requestDto.user_id
        var update_password: String = requestDto.update_password
        var confirm_update_password: String = requestDto.confirm_update_password

        // check whether the user id exists in the database
        var findUserAccount = getUserAccountById(GetUserAccountByIdDto(user_id))

        var update_registration_form: MutableMap<String, String> = java.util.HashMap()

        validationObj.validatePassword(
            findUserAccount.username,
            update_password,
            update_registration_form
        )

        if(update_registration_form.isNotEmpty()) {
            throw UserAccountBadRequestException(update_registration_form)
        }

        // check if the new password is the same as old password
        if(BCryptObj.verifyPassword(update_password, findUserAccount.hashed_password)) {
            throw UserAccountConflictException("New password must be different from old password!")
        }

        // hash the new password
        val hash_new_password = BCryptObj.hashPassword(
            password = update_password
        )
        findUserAccount.hashed_password = hash_new_password

        return userAccoutRepository.save(findUserAccount)
    }

    @Transactional
    override suspend fun deleteUserAccountById(
        @Valid requestDto: DeleteUserAccountDto
    ) {
        return if (userAccoutRepository.existsById(requestDto.user_id)) {
            userAccoutRepository.deleteById(requestDto.user_id)
        } else throw UserAccountNotFoundException(requestDto.user_id.toString())
    }
}