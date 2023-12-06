package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ScheduledFuture

@Service
interface TaskHandler {
    // Interface to represent tasks
    @TaskType(TaskTypeEnum.SESSION_DELETION)
    fun sessionDeletionTaskHandler(executionTime: LocalDateTime): ScheduledFuture<*>
}