package com.geogrind.geogrindbackend.services.registration

import com.geogrind.geogrindbackend.dto.registration.CreateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.DeleteUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.GetUserAccountByIdDto
import com.geogrind.geogrindbackend.dto.registration.UpdateUserAccountDto
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import org.springframework.stereotype.Service
import jakarta.validation.Valid
import java.util.UUID

@Service
interface UserAccountService {
    suspend fun getAllUserAccounts(): List<UserAccount>
    suspend fun getUserAccountById(@Valid requestDto: GetUserAccountByIdDto): UserAccount
    suspend fun createUserAccount(@Valid requestDto: CreateUserAccountDto): UserAccount
    suspend fun updateUserAccountById(user_id: UUID, @Valid requestDto: UpdateUserAccountDto): UserAccount
    suspend fun deleteUserAccountById(@Valid requestDto: DeleteUserAccountDto): Unit

    // email verification
    suspend fun getEmailVerification(token: String): UserAccount
    suspend fun getConfirmPasswordChangeVerification(token: String): UserAccount
    suspend fun getDeleteAccountVerification(token: String): UserAccount
}