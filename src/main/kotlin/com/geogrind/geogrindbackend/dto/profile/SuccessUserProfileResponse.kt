package com.geogrind.geogrindbackend.dto.profile

import com.geogrind.geogrindbackend.models.user_profile.Program
import com.geogrind.geogrindbackend.models.user_profile.University
import java.util.Date
import java.util.UUID

data class SuccessUserProfileResponse(
    val profile_id: UUID?,
    val username: String?,
    val emoji: String?,
    val program: Program?,
    val year_of_graduation: Int?,
    val university: University?,
    val createdAt: Date?,
    val updatedAt: Date?,
)