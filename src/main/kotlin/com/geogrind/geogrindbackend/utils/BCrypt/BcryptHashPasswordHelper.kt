package com.geogrind.geogrindbackend.utils.BCrypt

interface BcryptHashPasswordHelper {
    fun hashPassword(password: String): String

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean
}