package com.geogrind.geogrindbackend.utils.AutoGenerate

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelperImpl
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.pow
import kotlin.random.Random

@Service
class GenerateRandomHelperImpl : GenerateRandomHelper {
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
            hashed_password = hashedPassword,
            createdAt = Date(),
            updatedAt = Date(),
            permissions = mutableSetOf(),
        )
    }

    override fun generateOTP(length: Int): String {
        require(length > 0) { "Number of digits must be greater than 0" }

        val min = 10.0.pow(length-1).toInt()
        val max = 10.0.pow(length).toInt()

        val randomOTP = Random.nextInt(min, max)
        return String.format("%0${length}d", randomOTP)
    }
}

