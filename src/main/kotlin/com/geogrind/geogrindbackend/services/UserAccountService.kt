package com.geogrind.geogrindbackend.services

import com.geogrind.geogrindbackend.models.UserAccount
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface UserAccountService {
    fun getAllUserAccounts(): List<UserAccount>
    fun getUserAccountById(user_id: UUID): UserAccount?
    fun createUserAccount(user_account: UserAccount, confirm_password: String): UserAccount
    fun updateUserAccountById(user_id: UUID, update_password: String, confirm_update_password: String): UserAccount
    fun deleteUserAccountById(user_id: UUID): Unit
}