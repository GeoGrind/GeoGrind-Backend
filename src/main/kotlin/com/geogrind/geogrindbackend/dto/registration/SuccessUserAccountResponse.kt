package com.geogrind.geogrindbackend.dto.registration

import java.util.Date
import java.util.UUID

data class SuccessUserAccountResponse(
    val id: UUID?,
    val username: String?,
    val createdAt: Date?,
    val updatedAt: Date?,
)