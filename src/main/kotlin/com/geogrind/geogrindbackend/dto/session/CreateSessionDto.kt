package com.geogrind.geogrindbackend.dto.session

import com.geogrind.geogrindbackend.models.courses.Courses
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.Date
import java.util.UUID

data class CreateSessionDto (
    val userAccountId: UUID? = null,
    @get:Valid val courseCode: String,
    @get:Valid var startTime: Instant? = Instant.now(),
    @get:Min(value = 0, message = "Value must be greater than or equal to 0")
    @get:Max(value = 86400000, message = "Value must be less than or equal to 24 hours")
    var duration: Long, // the duration of the session in milliseconds
    @get:Min(value = 0, message = "Value must be greater than or equal to 0")
    @get:Max(value = 100, message = "Value must be less than or equal to 100")
    var numberOfLikers: Int, // the number of people like the session
    @get:Size(min = 0, max = 1000000) var description: String? = null
)