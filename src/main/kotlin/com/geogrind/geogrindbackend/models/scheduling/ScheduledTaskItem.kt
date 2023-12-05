package com.geogrind.geogrindbackend.models.scheduling

import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ScheduledFuture

data class ScheduledTaskItem(
    val taskId: UUID,
    val scheduledTask: ScheduledFuture<*>,
    val executionTime: LocalDateTime,
    val dependencies: Set<String> = emptySet(),
    val priority: Int = 0,
)
