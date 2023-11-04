package com.geogrind.geogrindbackend.dto.profile

import java.util.Date
import java.util.UUID

data class SuccessUserProfileResponse(
    val profile_id: UUID?,
    val username: String?,
    val emoji: String?,
    val program: String?,
    val year_of_graduation: Int?,
    val university: String?,
    val createdAt: Date?,
    val updatedAt: Date?,
)