package com.geogrind.geogrindbackend.utils.Cookies

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import jakarta.servlet.http.Cookie
import java.util.UUID

interface CreateTokenCookie {
    fun generateJwtToken(expirationTime: Long, user_id: UUID, permissionNames: Set<PermissionName>, secret_key: String, bucketName: String): String
    fun createTokenCookie(expirationTime: Int, token: String): Cookie
}