package com.geogrind.geogrindbackend.models.scheduling

import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ScheduledFuture

data class ScheduledTaskItem(
    var taskId: UUID,
    var scheduledTask: ScheduledFuture<*>,
    var executionTime: LocalDateTime,
    var dependencies: Set<String> = emptySet(),
    var priority: Int = 0,
)
