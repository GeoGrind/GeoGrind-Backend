package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import java.time.LocalDateTime

interface TaskHandler {
    // Interface to represent tasks
    @TaskType(TaskTypeEnum.DEFAULT)
    fun scheduleDefaultTask(executionTime: LocalDateTime): ScheduledTaskItem
    @TaskType(TaskTypeEnum.SESSION_DELETION)
    fun scheduleSessionTask(session: Sessions, executionTime: LocalDateTime) : ScheduledTaskItem
}