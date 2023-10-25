package com.geogrind.geogrindbackend.dto.registration.sendgrid

import java.time.Instant

data class JwtSendGridEmail(
    val user_id: String,
    val geogrind_otp_code: String,
    val new_password: String?,
    val exp: Instant
)