package com.geogrind.geogrindbackend.dto.profile

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class DeleteCoursesDto (
    val user_account_id: UUID? = null,
    @get:Valid var coursesDelete: Array<String>
)