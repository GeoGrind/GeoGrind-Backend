package com.geogrind.geogrindbackend.utils.ScheduledTask

import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
interface TaskSchedulerQueue {
    suspend fun scheduleTask(task: Runnable, executionTime: LocalDateTime): ScheduledTaskItem
    suspend fun rescheduleTask(taskItem: ScheduledTaskItem, newExecutionTime: LocalDateTime)
    suspend fun cancelTask(taskItem: ScheduledTaskItem)
    suspend fun adjustTaskPriority(taskId: UUID, newPriority: Int)
    suspend fun taskCompleted(taskId: UUID)
    suspend fun adjustPrioritiesForDependencies(dependencies: Set<String>)
    fun getNextScheduledTask(): ScheduledTaskItem?
    fun isEmpty(): Boolean
    fun getEstimatedTaskExecutionTime(taskId: UUID): LocalDateTime
    suspend fun adjustTaskExecutionTime(taskId: UUID, newExecutionTime: LocalDateTime)
}