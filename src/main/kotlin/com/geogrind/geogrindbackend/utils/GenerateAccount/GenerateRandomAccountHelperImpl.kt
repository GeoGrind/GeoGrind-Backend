package com.geogrind.geogrindbackend.utils.GenerateAccount

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelperImpl

class GenerateRandomAccountHelperImpl : GenerateRandomAccountHelper {
    override fun generateRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")

    }

    override fun createFakeUserAccount(): UserAccount {
        val email = "${generateRandomString(10)}@example.com"
        val username = generateRandomString(8)
        val plainPassword = generateRandomString(12)

        // hash the password using BCrypt
        val BcyptHashPassword: BcryptHashPasswordHelper = BcryptHashPasswordHelperImpl()
        val hashedPassword: String = BcyptHashPassword.hashPassword(plainPassword)

        return UserAccount(
            email = email,
            username = username,
            hashed_password = hashedPassword
        )
    }
}

