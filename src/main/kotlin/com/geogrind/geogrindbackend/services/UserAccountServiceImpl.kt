package com.geogrind.geogrindbackend.services

import com.geogrind.geogrindbackend.models.UserAccount
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAccountServiceImpl : UserAccountService {

    // get all user accounts
    override fun getAllUserAccounts(): List<UserAccount> {
        TODO("Not yet implemented")
    }

    override fun getUserAccountById(user_id: UUID): UserAccount? {
        TODO("Not yet implemented")
    }

    override fun createUserAccount(user_account: UserAccount, confirm_password: String): UserAccount {
        TODO("Not yet implemented")
    }

    override fun updateUserAccountById(
        user_id: UUID,
        update_password: String,
        confirm_update_password: String
    ): UserAccount {
        TODO("Not yet implemented")
    }

    override fun deleteUserAccountById(user_id: UUID) {
        TODO("Not yet implemented")
    }
}