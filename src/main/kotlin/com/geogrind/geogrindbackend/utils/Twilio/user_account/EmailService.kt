package com.geogrind.geogrindbackend.utils.Twilio.user_account

import com.geogrind.geogrindbackend.dto.registration.SendGridResponseDto
import com.sendgrid.SendGrid
import org.springframework.stereotype.Service

@Service
interface EmailService {
    fun sendEmailConfirmation(
        user_email: String,
        geogrind_otp_code: Int,
        user_id: String
    ): SendGridResponseDto

    fun sendChangePassword(
        user_email: String,
        geogrind_otp_code: Int,
        new_password: String,
        user_id: String,
    ): SendGridResponseDto

    fun sendDeleteAccount(
        user_email: String,
        geogrind_otp_code: Int,
        user_id: String,
    ): SendGridResponseDto
}