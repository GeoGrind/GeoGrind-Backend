package com.geogrind.geogrindbackend.utils.Twilio.user_account

import com.geogrind.geogrindbackend.dto.sendgrid.JwtSendGridEmail
import com.geogrind.geogrindbackend.dto.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.sendgrid.Method
import com.sendgrid.Response
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.time.Instant

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.io.IOException
import java.util.*

@Service
class EmailServiceImpl: EmailService {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val sendGridApiKey = dotenv["SENDGRID_API_KEY"]

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    // send the email confirmation using SendGrid
    override suspend fun sendEmailConfirmation(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String
    ): SendGridResponseDto {
        val subject: String = "Verify email address with GeoGrind"
        val templatePath = "src/main/kotlin/com/geogrind/geogrindbackend/utils/Twilio/templates/Twilio_confirm_email_template.html"
        val template = File(templatePath).readText()

        // allow up to 10 minutes of expiration time
        val expirationTime = Instant.now().plusSeconds(600)

        // create the jwt token
        val key = Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray())

        val jwtEncodeData: JwtSendGridEmail = JwtSendGridEmail(
            user_id = user_id,
            geogrind_otp_code = geogrind_otp_code,
            new_password = null,
            permission = null,
            exp = expirationTime,
        )

        val token = Jwts.builder()
            .claim("user_id", jwtEncodeData.user_id)
            .claim("geogrind_otp_code", jwtEncodeData.geogrind_otp_code)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(jwtEncodeData.exp))
            .signWith(key)
            .compact()

        val content = template.replace("{{token}}", token)

        val from = Email("infogeogrind@gmail.com")
        val to = Email(user_email)
        val mail = Mail(
            from,
            subject,
            to,
            Content("text/html", content)
        )

        // send the request to SENDGRID server for email verification sent
        val sendGrid = SendGrid(sendGridApiKey)
        val request = com.sendgrid.Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val response: Response = sendGrid.api(request)
            return SendGridResponseDto(
                statusCode = response.statusCode,
                sendGridResponse = response.body,
                token = token
            )
        } catch (e: IOException) {
            throw e
        }
    }

    override suspend fun sendChangePassword(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String,
        new_password: String,
    ): SendGridResponseDto {
        val subject: String = "Confirm password change with GeoGrind"
        val templatePath: String = "src/main/kotlin/com/geogrind/geogrindbackend/utils/Twilio/templates/Twilio_update_password_template.html"

        val template = File(templatePath).readText()

        val expirationTime = Instant.now().plusSeconds(600)

        val key = Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray())

        val jwtEncodeData : JwtSendGridEmail = JwtSendGridEmail(
            user_id = user_id,
            geogrind_otp_code = geogrind_otp_code,
            new_password = new_password,
            permission = null,
            exp = expirationTime
        )

        val token = Jwts.builder()
            .claim("user_id", jwtEncodeData.user_id)
            .claim("geogrind_otp_code", jwtEncodeData.geogrind_otp_code)
            .claim("new_password", jwtEncodeData.new_password)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(jwtEncodeData.exp))
            .signWith(key)
            .compact()

        val content = template.replace("{{token}}", token)

        val from = Email("infogeogrind@gmail.com")
        val to = Email(user_email)
        val mail = Mail(
            from,
            subject,
            to,
            Content("text/html", content)
        )

        // send the request to SENDGRID server for email verification sent
        val sendGrid = SendGrid(sendGridApiKey)
        val request = com.sendgrid.Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val response: Response = sendGrid.api(request)
            return SendGridResponseDto(
                statusCode = response.statusCode,
                sendGridResponse = response.body,
                token = token
            )
        } catch (e: IOException) {
            throw e
        }
    }

    override suspend fun sendDeleteAccount(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String): SendGridResponseDto {
        val subject: String = "Confirm account deletion with GeoGrind"
        val templatePath = "src/main/kotlin/com/geogrind/geogrindbackend/utils/Twilio/templates/Twilio_delete_account_template.html"

        val template = File(templatePath).readText()

        // allow up to 10 minutes of expiration time
        val expirationTime = Instant.now().plusSeconds(600)

        // create the jwt token
        val key = Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray())

        val jwtEncodeData: JwtSendGridEmail = JwtSendGridEmail(
            user_id = user_id,
            geogrind_otp_code = geogrind_otp_code,
            new_password = null,
            permission = null,
            exp = expirationTime
        )

        val token = Jwts.builder()
            .claim("user_id", jwtEncodeData.user_id)
            .claim("geogrind_otp_code", jwtEncodeData.geogrind_otp_code)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(jwtEncodeData.exp))
            .signWith(key)
            .compact()

        val content = template.replace("{{token}}", token)

        val from = Email("infogeogrind@gmail.com")
        val to = Email(user_email)
        val mail = Mail(
            from,
            subject,
            to,
            Content("text/html", content)
        )

        // send the request to SENDGRID server for email verification sent
        val sendGrid = SendGrid(sendGridApiKey)
        val request = com.sendgrid.Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val response: Response = sendGrid.api(request)
            return SendGridResponseDto(
                statusCode = response.statusCode,
                sendGridResponse = response.body,
                token = token
            )
        } catch (e: IOException) {
            throw e
        }
    }

    override suspend fun sendEmailOTP(
        user_email: String,
        geogrind_otp_code: String,
        permission_lists: Set<Permission>,
        user_id: String,
        ): SendGridResponseDto {
        val subject: String = "Secure your login with OTP"
        val templatePath: String = "src/main/kotlin/com/geogrind/geogrindbackend/utils/Twilio/templates/Twilio_login_template.html"

        val template = File(templatePath).readText()

        // allow up to 5 minutes of exp time
        val expirationTime = Instant.now().plusSeconds(300)

        val key = Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray())

        val jwtEncodeData: JwtSendGridEmail = JwtSendGridEmail(
            user_id = user_id,
            geogrind_otp_code = geogrind_otp_code,
            new_password = null,
            permission = permission_lists,
            exp = expirationTime,
        )

        val token = Jwts.builder()
            .claim("user_id", jwtEncodeData.user_id)
            .claim("geogrind_otp_code", jwtEncodeData.geogrind_otp_code)
            .claim("permission_lists", jwtEncodeData.permission)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(jwtEncodeData.exp))
            .signWith(key)
            .compact()

        val content = template.replace("{{token}}", token)

        val from = Email("infogeogrind@gmail.com")
        val to = Email(user_email)
        val mail = Mail(
            from,
            subject,
            to,
            Content("text/html", content)
        )

        // send the request to SENDGRID server for email sending
        val sendGrid = SendGrid(sendGridApiKey)
        val request = com.sendgrid.Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val response: Response = sendGrid.api(request)
            return SendGridResponseDto(
                statusCode = response.statusCode,
                sendGridResponse = response.body,
                token = token
            )
        } catch (e: IOException) {
            throw e
        }

    }
}