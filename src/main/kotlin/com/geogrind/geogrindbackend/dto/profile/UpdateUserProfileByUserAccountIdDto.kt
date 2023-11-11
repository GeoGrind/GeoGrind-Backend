package com.geogrind.geogrindbackend.dto.profile

import com.geogrind.geogrindbackend.models.courses.Courses
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.util.UUID

data class UpdateUserProfileByUserAccountIdDto(
    val user_account_id: UUID? = null,
    @get:Size(min = 0, max = 50) var username: String? = null,
    @get:Size(min = 0, max = 1000) var emoji: String? = null,
    @get:Size(min = 0, max = 100) var program: String? = null,
    @get:Valid var courseCodes: Array<String>? = emptyArray(), // a set of course codes
    @get:Min(value = 0, message = "Value must be greater than or equal to 0")
    @get:Max(value = 2030, message = "Value must be less than or equal to 2030")
    var year_of_graduation: Int? = null,
    @get:Size(min = 0, max = 100) var university: String? = null,
)
