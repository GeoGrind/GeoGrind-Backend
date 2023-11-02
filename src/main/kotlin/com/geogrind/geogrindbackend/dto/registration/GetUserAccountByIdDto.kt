package com.geogrind.geogrindbackend.dto.registration

import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.util.UUID

data class GetUserAccountByIdDto(
    @get:Size(min = 5, max = 1000) @NotNull val user_id: UUID
)