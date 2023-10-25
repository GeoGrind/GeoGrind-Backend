package com.geogrind.geogrindbackend.services.registration

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
interface UserAccountService {
    suspend fun getAllUserAccounts(): List<UserAccount>
    suspend fun getUserAccountById(@Valid requestDto: GetUserAccountByIdDto): UserAccount
    suspend fun createUserAccount(@Valid requestDto: CreateUserAccountDto): SendGridResponseDto
    suspend fun updateUserAccountById(user_id: UUID, @Valid requestDto: UpdateUserAccountDto): SendGridResponseDto
    suspend fun deleteUserAccountById(@Valid requestDto: DeleteUserAccountDto): SendGridResponseDto

    // email verification
    suspend fun getEmailVerification(@Valid requestDto: VerifyEmailUserAccountDto): UserAccount
    suspend fun getConfirmPasswordChangeVerification(@Valid requestDto: UpdatePasswordConfirmationDto): UserAccount
    suspend fun getDeleteAccountVerification(@Valid requestDto: DeleteUserAccountConfirmationDto)
}