package com.geogrind.geogrindbackend.utils.BCrypt

import com.geogrind.geogrindbackend.utils.BCrypt.BcryptHashPasswordHelper
import org.mindrot.jbcrypt.BCrypt

class BcryptHashPasswordHelperImpl : BcryptHashPasswordHelper {
    override fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    override fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}