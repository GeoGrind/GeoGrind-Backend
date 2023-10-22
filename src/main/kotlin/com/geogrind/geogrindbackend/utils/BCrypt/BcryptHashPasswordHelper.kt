package com.geogrind.geogrindbackend.utils.BCrypt

import org.mindrot.jbcrypt.BCrypt

interface BcryptHashPasswordHelper {
    fun hashPassword(password: String): String

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean
}