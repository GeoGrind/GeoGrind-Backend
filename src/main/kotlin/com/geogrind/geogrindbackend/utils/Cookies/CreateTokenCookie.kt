package com.geogrind.geogrindbackend.utils.Cookies

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.servlet.http.Cookie
import java.util.UUID

interface CreateTokenCookie {
    fun generateJwtToken(expirationTime: Long, user_id: UUID, permissionNames: Set<PermissionName>, secret_key: String, bucketName: String): String
    fun createTokenCookie(expirationTime: Long, token: String): Cookie
    fun refreshCookie(
        expirationTime: Long,
        currentUserAccount: UserAccount,
    ): Cookie
}