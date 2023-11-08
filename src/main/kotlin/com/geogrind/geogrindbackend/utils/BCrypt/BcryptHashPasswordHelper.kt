package com.geogrind.geogrindbackend.utils.BCrypt

import org.springframework.stereotype.Service

@Service
interface BcryptHashPasswordHelper {
    fun hashPassword(password: String): String

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean
}