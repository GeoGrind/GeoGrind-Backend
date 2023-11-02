package com.geogrind.geogrindbackend.dto.user_profile

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.University
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateUserProfileDto(
    @get:Size(min = 3, max = 50) @NotNull var username: String,
    @get:Size(min = 5, max = 100) @NotNull var user_account: UserAccount,
)