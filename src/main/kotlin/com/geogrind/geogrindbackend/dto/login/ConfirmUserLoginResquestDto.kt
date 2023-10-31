package com.geogrind.geogrindbackend.dto.login

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ConfirmUserLoginResquestDto(
    @get:Size(min = 3, max = 1000) @NotNull var token: String
)
