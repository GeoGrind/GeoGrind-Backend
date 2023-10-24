package com.geogrind.geogrindbackend.utils.Twilio.user_account

import com.geogrind.geogrindbackend.dto.registration.JwtSendGridEmail
import com.geogrind.geogrindbackend.dto.registration.SendGridResponseDto
import com.sendgrid.Method
import com.sendgrid.Response
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.time.Instant

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.io.IOException
import java.util.*

@Service
class EmailServiceImpl(
    @Value("\${SENDGRID_API_KEY}") private val sendGridApiKey: String,
    @Value("\${GEOGRIND_SECRET_KEY}") private val geogrindSecretKey: String,
): EmailService {

    // send the email confirmation using SendGrid
    override suspend fun sendEmailConfirmation(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String
    ): SendGridResponseDto {
        val subject: String = "Verify email address with GeoGrind"
        val templatePath = "/Users/kenttran/Desktop/Desktop_Folders/side_projects/GeoGrind-Backend/src/main/kotlin/com/geogrind/geogrindbackend/utils/Twilio/templates/Twilio_confirm_email_template.html"

        val template = File(templatePath).readText()

        // allow up to 10 minutes of expiration time
        val expirationTime = Instant.now().plusSeconds(600)

        // create the jwt token
        val key = Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray())

        val jwtEncodeData: JwtSendGridEmail = JwtSendGridEmail(
            user_id = user_id,
            geogrind_otp_code = geogrind_otp_code,
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

    override suspend fun sendChangePassword(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String
    ): SendGridResponseDto {
        val subject: String = "Confirm password change with GeoGrind"
        val templatePath: String = "/Users/kenttran/Desktop/Desktop_Folders/side_projects/GeoGrind-Backend/src/main/kotlin/com/geogrind/geogrindbackend/utils/Twilio/templates/Twilio_update_password_template.html"

        val template = File(templatePath).readText()

        val expirationTime = Instant.now().plusSeconds(600)

        val key = Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray())

        val jwtEncodeData : JwtSendGridEmail = JwtSendGridEmail(
            user_id = user_id,
            geogrind_otp_code = geogrind_otp_code,
            exp = expirationTime
        )

        val token = Jwts.builder()
            .claim("user_id", jwtEncodeData.user_id)
            .claim("geogrind_otp_code", jwtEncodeData)
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
        val templatePath = "/Users/kenttran/Desktop/Desktop_Folders/side_projects/GeoGrind-Backend/src/main/kotlin/com/geogrind/geogrindbackend/utils/Twilio/templates/Twilio_delete_account_template.html"

        val template = File(templatePath).readText()

        // allow up to 10 minutes of expiration time
        val expirationTime = Instant.now().plusSeconds(600)

        // create the jwt token
        val key = Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray())

        val jwtEncodeData: JwtSendGridEmail = JwtSendGridEmail(
            user_id = user_id,
            geogrind_otp_code = geogrind_otp_code,
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
}