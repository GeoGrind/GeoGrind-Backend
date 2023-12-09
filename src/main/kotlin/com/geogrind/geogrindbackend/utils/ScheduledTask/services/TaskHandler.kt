package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import java.time.LocalDateTime
import java.util.concurrent.ScheduledFuture

interface TaskHandler {
    // Interface to represent tasks
    @TaskType(TaskTypeEnum.DEFAULT)
    fun scheduleDefaultTask(executionTime: LocalDateTime): ScheduledFuture<*>
    @TaskType(TaskTypeEnum.SESSION_DELETION)
    fun scheduleSessionTask(executionTime: LocalDateTime) : ScheduledFuture<*>
}