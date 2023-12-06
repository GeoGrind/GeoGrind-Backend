package com.geogrind.geogrindbackend.dto.profile

import com.geogrind.geogrindbackend.models.courses.Courses
import java.util.Date
import java.util.UUID

data class SuccessUserProfileResponse(
    val profile_id: UUID?,
    val username: String?,
    val emoji: String?,
    val program: String?,
    val courses: MutableSet<Courses>?,
    val year_of_graduation: Int?,
    val university: String?,
    val createdAt: Date?,
    val updatedAt: Date?,
)