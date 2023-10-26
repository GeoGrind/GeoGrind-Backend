package com.geogrind.geogrindbackend.services.login

import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.exceptions.registration.UserAccountForbiddenException
import com.geogrind.geogrindbackend.exceptions.registration.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.registration.UserAccountUnauthorizedException
import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.permissions.PermissionsRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelperImpl
import jakarta.validation.Valid

class LoginAccountServiceImpl(
    private val userAccountRepository: UserAccountRepository,
    private val permissionsRepository: PermissionsRepository,
) : LoginAccountService {

    private val BcryptObj: BcryptHashPasswordHelper = BcryptHashPasswordHelperImpl()
    override suspend fun login(@Valid requestDto: UserLoginRequestDto): UserAccount {

        val email: String = requestDto.email
        val password: String = requestDto.password

        // check if the user with email exists
        val findUserAccount: UserAccount? = userAccountRepository.findUserAccountByEmail(email = email).orElse(null)

        if(findUserAccount == null) {
            throw UserAccountNotFoundException(
                field = email
            )
        }

        if(!BcryptObj.verifyPassword(password, findUserAccount.hashed_password)) {
            throw UserAccountUnauthorizedException(
                message = "Unauthorized user!"
            )
        }

        if(findUserAccount.account_verified == false) {
            throw UserAccountForbiddenException(
                message = "User is not verified with the system!"
            )
        }

        // grant the permissions for the user to visit a certain resources
        // give the user the permission to verify the otp code
        val permissionsList: Array<Permission> = arrayOf(
            Permission(
                permission_name = PermissionName.CAN_VERIFY_OTP,
                user_id = findUserAccount.id,
                user_account = findUserAccount
            )
        )

        val savedUser: UserAccount = userAccountRepository.save(findUserAccount)

        return savedUser
    }
}