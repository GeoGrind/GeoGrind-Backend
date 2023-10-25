package com.geogrind.geogrindbackend.dto.registration.sendgrid

data class SendGridResponseDto(
    val statusCode: Int,
    val sendGridResponse: String,
    val token: String,
)