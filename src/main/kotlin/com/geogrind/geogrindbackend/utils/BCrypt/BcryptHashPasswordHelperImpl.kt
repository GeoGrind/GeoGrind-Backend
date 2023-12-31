package com.geogrind.geogrindbackend.utils.BCrypt

import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class BcryptHashPasswordHelperImpl : BcryptHashPasswordHelper {
    override fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    override fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}