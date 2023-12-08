package com.geogrind.geogrindbackend.utils.ScheduledTask.queue
//
//import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
//import com.geogrind.geogrindbackend.utils.ScheduledTask.proxy.TaskProxyHandler
//import org.springframework.stereotype.Service
//import java.time.LocalDateTime
//import java.util.UUID
//
//@Service
//interface TaskSchedulerQueue {
//    suspend fun scheduleTask(executionTime: LocalDateTime, dependencies: Set<String> = emptySet(), priority: Int = 0): ScheduledTaskItem
//    suspend fun rescheduleTask(taskItem: ScheduledTaskItem, newExecutionTime: LocalDateTime)
//    suspend fun cancelTask(taskItem: ScheduledTaskItem)
//    suspend fun adjustTaskPriority(taskId: UUID, newPriority: Int)
//    suspend fun taskCompleted(taskId: UUID)
//    suspend fun adjustPrioritiesForDependencies(dependencies: Set<String>)
//    fun getNextScheduledTask(): ScheduledTaskItem?
//    fun isEmpty(): Boolean
//    fun getEstimatedTaskExecutionTime(taskId: UUID): LocalDateTime
////    suspend fun adjustTaskExecutionTime(taskId: UUID, newExecutionTime: LocalDateTime)
//}