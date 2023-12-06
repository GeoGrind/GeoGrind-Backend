package com.geogrind.geogrindbackend.dto.session

import com.geogrind.geogrindbackend.models.courses.Courses
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class UpdateSessionByIdDto(
    var userAccountId: UUID ?= null,
    @get:Valid val updateCourseCode: String ?= null,
    @get:Valid var updateStartTime: Instant? = null,
    @get:Min(value = 0, message = "Value must be greater than or equal to 0")
    @get:Max(value = 86400000, message = "Value must be less than or equal to 24 hours")
    var updateDuration: Long? = null, // the duration of the session in milliseconds
    @get:Min(value = 0, message = "Value must be greater than or equal to 0")
    @get:Max(value = 100, message = "Value must be less than or equal to 100")
    var updateNumberOfLikers: Int ?= null, // the number of people like the session
    @get:Size(min = 0, max = 1000000) var updateDescription: String? = null // the description of the session
)