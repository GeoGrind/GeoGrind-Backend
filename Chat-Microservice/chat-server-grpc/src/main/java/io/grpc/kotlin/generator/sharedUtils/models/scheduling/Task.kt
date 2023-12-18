package com.geogrind.geogrindbackend.models.scheduling

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Task(
    @Serializable(with = UUIDSerializer::class)
    var sessionId: UUID,
)
