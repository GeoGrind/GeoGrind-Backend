package io.grpc.kotlin.generator.sharedUtils.models.scheduling

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class ScheduledTaskItem (
    @Serializable(with = UUIDSerializer::class)
    @SerialName("taskId")
    var taskId: UUID,
    @SerialName("scheduledTask")
    var scheduledTask: Task ?= null,
    @Contextual
    @SerialName("executionTime")
    var executionTime: LocalDateTime,
    @SerialName("dependencies")
    var dependencies: Set<String> = emptySet(),
    @SerialName("priority")
    var priority: Int = 0,
)

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}
