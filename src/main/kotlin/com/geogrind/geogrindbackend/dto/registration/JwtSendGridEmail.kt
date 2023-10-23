package com.geogrind.geogrindbackend.dto.registration

import java.time.Instant

data class JwtSendGridEmail(
    val user_id: String,
    val geogrind_otp_code: Int,
    val exp: Instant
)