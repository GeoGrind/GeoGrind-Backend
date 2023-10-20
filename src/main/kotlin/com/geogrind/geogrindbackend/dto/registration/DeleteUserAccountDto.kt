package com.geogrind.geogrindbackend.dto.registration

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class DeleteUserAccountDto(
    @get:Size(min = 5) @NotNull var user_id: UUID
)