package com.geogrind.geogrindbackend.services.login

import com.geogrind.geogrindbackend.dto.login.ConfirmUserLoginResquestDto
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
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookie
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookieImpl
import com.geogrind.geogrindbackend.utils.GrantPermissions.GrantPermissionHelper
import com.geogrind.geogrindbackend.utils.GrantPermissions.GrantPermissionHelperImpl
import com.geogrind.geogrindbackend.utils.Twilio.user_account.EmailServiceImpl
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import okhttp3.internal.notify
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class LoginAccountServiceImpl(
    private val userAccountRepository: UserAccountRepository,
    private val permissionRepository: PermissionRepository,
) : LoginAccountService {

    private val BcryptObj: BcryptHashPasswordHelper = BcryptHashPasswordHelperImpl()

    private val generateRandomHelper: GenerateRandomHelper = GenerateRandomHelperImpl()

    private val grantPermissionHelper: GrantPermissionHelper = GrantPermissionHelperImpl()

    private val generateCookieHelper: CreateTokenCookie = CreateTokenCookieImpl()

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
        val permissions: Set<Permission> = setOf(
            Permission(
                permission_name = PermissionName.CAN_VERIFY_OTP,
                fkUserAccountId = findUserAccount.id as UUID,
                createdAt = Date(),
                updatedAt = Date(),
            )
        )

        grantPermissionHelper.grant_permission_helper(
            newPermissions = permissions,
            permissionRepository = permissionRepository,
            currentUserAccount = findUserAccount,
        )

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

    @Transactional
    override suspend fun confirmLoginHandler(@Valid requestDto: ConfirmUserLoginResquestDto): Pair<UserAccount, Cookie> {
        // decode the jwt token
        val decoded_token: Claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray()))
            .build()
            .parseSignedClaims(requestDto.token)
            .payload

        // check if the expiration time is more than the current time
        val exp_timestamp = decoded_token["exp"] as Long
        val exp_time = Instant.ofEpochSecond(exp_timestamp)
        val current_time = Instant.now()

        if(current_time.isAfter(exp_time)) {
            throw ExpiredJwtException(null, decoded_token, "The token provided has expired!")
        }

        val user_id: String = decoded_token["user_id"] as String

        val findUserAccount = userAccountRepository.findById(UUID.fromString(user_id))

        // verify the temp token
        if(!findUserAccount.get().temp_token.equals(requestDto.token)) {
            throw UserAccountUnauthorizedException("Unauthorized user!")
        }

        // if the user is verified -> give the user more permissions
        val newPermissions: Set<Permission> = setOf(
            Permission(
                permission_name = PermissionName.CAN_VIEW_PROFILE,
                fkUserAccountId = findUserAccount.get().id as UUID,
                createdAt = Date(),
                updatedAt = Date(),
            ),
            Permission(
                permission_name = PermissionName.CAN_EDIT_PROFILE,
                fkUserAccountId = findUserAccount.get().id as UUID,
                createdAt = Date(),
                updatedAt = Date(),
            )
        )

        grantPermissionHelper.grant_permission_helper(
            newPermissions = newPermissions,
            permissionRepository = permissionRepository,
            currentUserAccount = findUserAccount.get()
        )

        // generate the new jwt token for the user's new session
        val jwt_token: String = generateCookieHelper.generateJwtToken(3600, findUserAccount.get().id as UUID, permissionRepository.findAllByFkUserAccountId(findUserAccount.get().id as UUID), geogrindSecretKey)

        // store the token into cookies
        val cookie: Cookie = generateCookieHelper.createTokenCookie(3600, jwt_token)

        return Pair(
            first = findUserAccount.get(),
            second = cookie,
        )
    }
}