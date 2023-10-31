package com.geogrind.geogrindbackend.utils.Twilio.user_account

import com.geogrind.geogrindbackend.dto.registration.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
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
        new_password: String,
    ): SendGridResponseDto

    suspend fun sendDeleteAccount(
        user_email: String,
        geogrind_otp_code: String,
        user_id: String,
    ): SendGridResponseDto

    suspend fun sendEmailOTP(
        user_email: String,
        geogrind_otp_code: String,
        permission_lists: Set<Permission>,
        user_id: String,
    ): SendGridResponseDto
}