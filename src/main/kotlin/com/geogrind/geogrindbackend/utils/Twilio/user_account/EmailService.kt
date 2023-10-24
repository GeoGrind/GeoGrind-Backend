package com.geogrind.geogrindbackend.utils.Twilio.user_account

import com.geogrind.geogrindbackend.dto.registration.SendGridResponseDto
import com.sendgrid.SendGrid
import org.springframework.stereotype.Service

@Service
interface EmailService {
    suspend fun sendEmailConfirmation(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String
    ): SendGridResponseDto

    suspend fun sendChangePassword(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String,
    ): SendGridResponseDto

    suspend fun sendDeleteAccount(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String,
    ): SendGridResponseDto
}