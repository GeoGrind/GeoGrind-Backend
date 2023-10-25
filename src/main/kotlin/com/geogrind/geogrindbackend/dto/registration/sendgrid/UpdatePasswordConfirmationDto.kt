package com.geogrind.geogrindbackend.dto.registration.sendgrid

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdatePasswordConfirmationDto(
    @get:Size(min = 3, max = 1000) @NotNull var token: String,
)
