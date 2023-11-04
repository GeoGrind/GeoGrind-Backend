package com.geogrind.geogrindbackend.dto.sendgrid

data class SendGridResponseDto(
    val statusCode: Int,
    val sendGridResponse: String,
    val token: String,
)