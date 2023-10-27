package com.geogrind.geogrindbackend.services.login

import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountForbiddenException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountUnauthorizedException
import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.utils.AutoGenerate.GenerateRandomHelper
import com.geogrind.geogrindbackend.utils.AutoGenerate.GenerateRandomHelperImpl
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelperImpl
import com.geogrind.geogrindbackend.utils.Twilio.user_account.EmailServiceImpl
import io.github.cdimascio.dotenv.Dotenv
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class LoginAccountServiceImpl(
    private val userAccountRepository: UserAccountRepository,
    private val permissionRepository: PermissionRepository,
) : LoginAccountService {

    private val BcryptObj: BcryptHashPasswordHelper = BcryptHashPasswordHelperImpl()

    private val generateRandomHelper: GenerateRandomHelper = GenerateRandomHelperImpl()

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory("/Users/kenttran/Desktop/Desktop_Folders/side_projects/GeoGrind-Backend/.env").load()

    private val sendGridApiKey = dotenv["SENDGRID_API_KEY"]

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    @Transactional
    override suspend fun login(@Valid requestDto: UserLoginRequestDto): SendGridResponseDto {

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

        // give the user the permission to verify the otp code if the user didn't have this permission
        val permissions = Permission(
            permission_name = PermissionName.CAN_VERIFY_OTP,
            fk_user_account_id = findUserAccount.id as UUID,
            createdAt = Date(),
            updatedAt = Date(),
        )

        permissionRepository.save(permissions)

        findUserAccount.permissions.plus(permissions)

        val savedUser: UserAccount = userAccountRepository.save(findUserAccount)

        // send the email that contains the OTP Code to verify the user
        val otp_code: String = generateRandomHelper.generateOTP(6)

        val sendgrid_response: SendGridResponseDto = EmailServiceImpl(
            sendGridApiKey = sendGridApiKey,
            geogrindSecretKey = geogrindSecretKey,
        ).sendEmailOTP(
            user_email = requestDto.email,
            geogrind_otp_code = otp_code,
            permission_lists = savedUser.permissions,
            user_id = savedUser.id.toString(),
        )

        val statusCode: Int = sendgrid_response.statusCode
        val sendGridResponse: String = sendgrid_response.sendGridResponse
        val token: String = sendgrid_response.token

        println("The status code is: $statusCode \n The sendGridResponse is: $sendGridResponse \n The token is: $token")

        if(statusCode == 202) {
            findUserAccount.temp_token = token
        }

        return sendgrid_response
    }
}