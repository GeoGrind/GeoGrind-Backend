package com.geogrind.geogrindbackend.dto.registration.sendgrid

import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import java.time.Instant

data class JwtSendGridEmail(
    val user_id: String,
    val geogrind_otp_code: String,
    val new_password: String?,
    val permission: Set<Permission>?,
    val exp: Instant
)