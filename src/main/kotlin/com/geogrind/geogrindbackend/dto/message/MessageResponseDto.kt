package com.geogrind.geogrindbackend.dto.message

import java.util.Date
import java.util.UUID

data class MessageResponseDto(
    var id: UUID?,
    var email: String,
    var createdAt: Date?,
    var updatedAt: Date?
)
