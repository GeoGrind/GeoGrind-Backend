package com.geogrind.geogrindbackend.dto.sendgrid

import jakarta.validation.constraints.Size

data class DeleteUserAccountConfirmationDto(
    @get:Size(min = 3, max = 1000) var token: String,
)