package com.geogrind.geogrindbackend.dto.login

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class ConfirmUserLoginResquestDto(
    @field:org.jetbrains.annotations.NotNull
    @field:NotBlank
    val user_account_id: UUID,
    @get:Size(min = 3, max = 1000) @NotNull var token: String,
)
