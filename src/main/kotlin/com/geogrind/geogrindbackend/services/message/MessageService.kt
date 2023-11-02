package com.geogrind.geogrindbackend.services.message

import com.geogrind.geogrindbackend.dto.registration.CreateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.DeleteUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.GetUserAccountByIdDto
import com.geogrind.geogrindbackend.dto.registration.UpdateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.DeleteUserAccountConfirmationDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.UpdatePasswordConfirmationDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.VerifyEmailUserAccountDto
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import org.springframework.stereotype.Service
import jakarta.validation.Valid
import java.util.UUID

@Service
interface MessageService {
    suspend fun test(): String
}