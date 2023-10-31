package com.geogrind.geogrindbackend.utils.Cookies

import com.geogrind.geogrindbackend.models.permissions.Permission
import jakarta.servlet.http.Cookie
import java.util.UUID

interface CreateTokenCookie {
    fun generateJwtToken(expirationTime: Long, user_id: UUID, permissions: Set<Permission>, secret_key: String): String
    fun createTokenCookie(expirationTime: Int, token: String): Cookie
}