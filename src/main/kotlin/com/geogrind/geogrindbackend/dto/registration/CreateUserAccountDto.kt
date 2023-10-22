package com.geogrind.geogrindbackend.dto.registration

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class CreateUserAccountDto(
    @get:Email @get:Size(min = 5, max = 100) @NotNull var email: String,
    @get:Size(min = 3, max = 50) @NotNull var username: String,
    @get:Size(min = 8, max = 100) @NotNull var password: String,
    @get:Size(min = 8, max = 100) @NotNull var confirm_password: String,
)