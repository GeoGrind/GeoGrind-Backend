package com.geogrind.geogrindbackend.dto.registration

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class UpdateUserAccountDto(
    @get:Size(min = 8, max = 100) @NotNull var update_password: String,
    @get:Size(min = 8, max = 100) @NotNull var confirm_update_password: String,
)