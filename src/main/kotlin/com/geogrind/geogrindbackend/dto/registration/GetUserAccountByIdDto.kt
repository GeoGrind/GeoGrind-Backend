package com.geogrind.geogrindbackend.dto.registration

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.util.UUID

data class GetUserAccountByIdDto(
    @field:NotNull
    @field:NotBlank
    val user_id: UUID
)