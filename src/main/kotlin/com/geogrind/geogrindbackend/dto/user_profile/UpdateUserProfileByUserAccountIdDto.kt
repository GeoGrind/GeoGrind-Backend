package com.geogrind.geogrindbackend.dto.user_profile

import com.geogrind.geogrindbackend.models.user_profile.Program
import com.geogrind.geogrindbackend.models.user_profile.University
import jakarta.validation.constraints.Size

data class UpdateUserProfileByUserAccountIdDto(
    @get:Size(min = 3, max = 50) var username: String,
    @get:Size(min = 3, max = 1000) var emoji: String,
    @get:Size(min = 3, max = 1000) var program: Program,
    @get:Size(min = 4, max = 4) var year_of_graduation: Int,
    @get:Size(min = 5, max = 100) var university: University,
)
