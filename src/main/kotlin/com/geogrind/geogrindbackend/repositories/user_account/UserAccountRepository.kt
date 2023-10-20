package com.geogrind.geogrindbackend.repositories.user_account

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserAccountRepository : JpaRepository<UserAccount, UUID> {
    fun findUserAccountByEmail(email: String): UserAccount
    fun findUserAccountByUsername(username: String): UserAccount
}