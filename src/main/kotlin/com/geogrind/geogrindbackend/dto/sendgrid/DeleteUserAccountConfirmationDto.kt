package com.geogrind.geogrindbackend.dto.sendgrid

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class DeleteUserAccountConfirmationDto(
    @field:org.jetbrains.annotations.NotNull
    @field:NotBlank
    val user_account_id: UUID,
    @get:Size(min = 3, max = 1000) var token: String,
)