package com.geogrind.geogrindbackend.dto.sendgrid

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import java.time.Instant

data class JwtSendGridEmail(
    val user_id: String,
    val geogrind_otp_code: String,
    val new_password: String?,
    val permission: Set<Permissions>?,
    val exp: Instant
)