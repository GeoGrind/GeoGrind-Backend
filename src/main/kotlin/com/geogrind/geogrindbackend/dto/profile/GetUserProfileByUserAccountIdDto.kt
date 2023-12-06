package com.geogrind.geogrindbackend.dto.profile

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class GetUserProfileByUserAccountIdDto(
    @field:org.jetbrains.annotations.NotNull
    @field:NotBlank
    val user_account_id: UUID
)
