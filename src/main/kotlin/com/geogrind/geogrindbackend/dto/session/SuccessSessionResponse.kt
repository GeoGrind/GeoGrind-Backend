package com.geogrind.geogrindbackend.dto.session

import com.geogrind.geogrindbackend.models.courses.Courses
import java.time.Instant
import java.util.UUID

data class SuccessSessionResponse(
    val sessionId: UUID?,
    val course: Courses?,
    val startTime: Instant?,
    val stopTime: Instant?,
    val numberOfLikers: Int?,
    val description: String?,
)