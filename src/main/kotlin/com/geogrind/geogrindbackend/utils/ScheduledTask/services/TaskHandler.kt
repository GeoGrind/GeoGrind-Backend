package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import java.time.LocalDateTime
import java.util.concurrent.ScheduledFuture

interface TaskHandler {
    // Interface to represent tasks
    interface SessionDeletionTask {
        @TaskType(TaskTypeEnum.SESSION_DELETION)
        fun scheduleTask(executionTime: LocalDateTime): ScheduledFuture<*>
    }

    interface DefaultDeletionTask {
        @TaskType(TaskTypeEnum.DEFAULT)
        fun scheduleTask(executionTime: LocalDateTime): ScheduledFuture<*>
    }
}