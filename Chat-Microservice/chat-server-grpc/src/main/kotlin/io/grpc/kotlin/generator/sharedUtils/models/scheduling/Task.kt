package io.grpc.kotlin.generator.sharedUtils.models.scheduling

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Task(
    @Serializable(with = UUIDSerializer::class)
    var sessionId: UUID,
)
