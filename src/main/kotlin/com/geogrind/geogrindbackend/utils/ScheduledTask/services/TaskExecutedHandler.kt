package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface TaskExecutedHandler {
    // Interface to represent tasks
    @TaskType(TaskTypeEnum.DEFAULT)
    fun defaultTaskExecuted(task: ScheduledTaskItem)

    @TaskType(TaskTypeEnum.SESSION_DELETION)
    fun sessionDeletionTaskExecuted(task: ScheduledTaskItem)
}