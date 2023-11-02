package com.geogrind.geogrindbackend.dto.message

import java.util.Date
import java.util.UUID

data class MessageResponseDto(
    var id: UUID,
    var authorId: UUID,
    var text: String,
    var type: String,
    var createdAt: Date
)
