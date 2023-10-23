package com.geogrind.geogrindbackend.dto.registration

data class SendGridResponseDto(
    val statusCode: Int,
    val sendGridResponse: String,
    val token: String,
)