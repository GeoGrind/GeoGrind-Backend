package com.geogrind.geogrindbackend.services.login

import com.geogrind.geogrindbackend.dto.login.ConfirmUserLoginResquestDto
import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.dto.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountForbiddenException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountUnauthorizedException
import com.geogrind.geogrindbackend.models.courses.Courses
import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.utils.AutoGenerate.GenerateRandomHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelperImpl
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookie
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookieImpl
import com.geogrind.geogrindbackend.utils.GrantPermissions.GrantPermissionHelper
import com.geogrind.geogrindbackend.utils.Twilio.user_account.EmailService
import com.geogrind.geogrindbackend.utils.Twilio.user_account.EmailServiceImpl
import io.github.cdimascio.dotenv.Dotenv
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.HashSet

@Service
class LoginAccountServiceImpl(
    private val userAccountRepository: UserAccountRepository,
    private val permissionRepository: PermissionRepository,
    private val generateRandomHelper: GenerateRandomHelper,
    private val grantPermissionHelper: GrantPermissionHelper,
    private val bcryptObj: BcryptHashPasswordHelper,
    private val emailService: EmailService,
    private val generateCookieHelper: CreateTokenCookie,
) : LoginAccountService {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    private val s3BucketName = dotenv["AWS_PFP_BUCKET_NAME"]

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

        if(!bcryptObj.verifyPassword(password, findUserAccount.hashed_password)) {
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
        val permissions: Set<Permissions> = setOf(
            Permissions(
                permission_name = PermissionName.CAN_VERIFY_OTP,
                userAccount = findUserAccount,
                createdAt = Date(),
                updatedAt = Date(),
            )
        )

        grantPermissionHelper.grant_permission_helper(
            newPermissions = permissions,
            currentUserAccount = findUserAccount,
        )

        val savedUser: UserAccount = userAccountRepository.save(findUserAccount)

        // send the email that contains the OTP Code to verify the user
        val otp_code: String = generateRandomHelper.generateOTP(6)

        val sendgrid_response: SendGridResponseDto = emailService.sendEmailOTP(
            user_email = requestDto.email,
            geogrind_otp_code = otp_code,
            permission_lists = savedUser.permissions!!.toSet(),
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

    @Transactional
    override suspend fun confirmLoginHandler(@Valid requestDto: ConfirmUserLoginResquestDto): Pair<UserAccount, Cookie> {
        val findUserAccount = userAccountRepository.findById(requestDto.user_account_id)

        // verify the temp token
        if(!findUserAccount.get().temp_token.equals(requestDto.token)) {
            throw UserAccountUnauthorizedException("Unauthorized user!")
        }

        // if the user is verified -> give the user more permissions
        val newPermissions: MutableSet<Permissions> = mutableSetOf(
            Permissions(
                permission_name = PermissionName.CAN_VIEW_PROFILE,
                userAccount = findUserAccount.get(),
                createdAt = Date(),
                updatedAt = Date(),
            ),
            Permissions(
                permission_name = PermissionName.CAN_EDIT_PROFILE,
                userAccount = findUserAccount.get(),
                createdAt = Date(),
                updatedAt = Date(),
            ),
            Permissions(
                permission_name = PermissionName.CAN_VIEW_FILES,
                userAccount = findUserAccount.get(),
                createdAt = Date(),
                updatedAt = Date(),
            ),
            Permissions(
                permission_name = PermissionName.CAN_DELETE_FILES,
                userAccount = findUserAccount.get(),
                createdAt = Date(),
                updatedAt = Date(),
            ),
            Permissions(
                permission_name = PermissionName.CAN_UPLOAD_FILES,
                userAccount = findUserAccount.get(),
                createdAt = Date(),
                updatedAt = Date(),
            ),
            Permissions(
                permission_name = PermissionName.CAN_VIEW_SESSION,
                userAccount = findUserAccount.get(),
                createdAt = Date(),
                updatedAt = Date(),
            ),
        )

        // check if the user profile has all the courses in there -> if yes, give permission to create session
        val findUserProfile: UserProfile? = findUserAccount.get().userProfile
        if(findUserProfile != null) {
            val courses: MutableSet<Courses>? = findUserProfile.courses
            if(courses != null) {
                newPermissions.add(
                    Permissions(
                        permission_name = PermissionName.CAN_CREATE_SESSION,
                        userAccount = findUserAccount.get(),
                        createdAt = Date(),
                        updatedAt = Date(),
                    )
                )
            }
        }

        grantPermissionHelper.grant_permission_helper(
            newPermissions = newPermissions,
            currentUserAccount = findUserAccount.get()
        )

        val allCurrentPermissions: MutableSet<Permissions>? = findUserAccount.get().permissions
        val allCurrentPermissionsName: MutableSet<PermissionName> = HashSet()
        allCurrentPermissions!!.forEach {permissions ->
            allCurrentPermissionsName.add(permissions.permission_name)
        }

        // generate the new jwt token for the user's new session
        val jwt_token: String = generateCookieHelper.generateJwtToken(
            3600,
            findUserAccount.get().id as UUID,
            allCurrentPermissionsName,
            geogrindSecretKey,
            bucketName = s3BucketName
        )

        // store the token into cookies
        val cookie: Cookie = generateCookieHelper.createTokenCookie(3600, jwt_token)
        log.info("Cookie in login: $cookie, ${cookie.name}, ${cookie.path}")
        return Pair(
            first = findUserAccount.get(),
            second = cookie,
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoginAccountServiceImpl::class.java)
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