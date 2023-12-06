package com.geogrind.geogrindbackend.dto.login

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserLoginRequestDto(
    @get:Email @get:Size(min = 5, max = 100) @NotNull val email: String,
    @get:Size(min = 8, max = 100) @NotNull val password: String,
)