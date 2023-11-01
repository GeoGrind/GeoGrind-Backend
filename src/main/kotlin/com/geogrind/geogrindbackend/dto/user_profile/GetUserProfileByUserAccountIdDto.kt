package com.geogrind.geogrindbackend.dto.user_profile

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class GetUserProfileByUserAccountIdDto(
    @get:Size(min = 5) @NotNull val profile_id: UUID
)
