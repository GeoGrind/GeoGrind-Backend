package com.geogrind.geogrindbackend.utils.ScheduledTask.queue

import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfig
import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfigImpl
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.utils.ScheduledTask.proxy.TaskProxyHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.proxy.createKafkaTopicsProxy
import com.geogrind.geogrindbackend.utils.ScheduledTask.proxy.createTaskProxy
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.TaskHandler
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@Service
class TaskSchedulerQueueImpl(
    private val taskScheduler: TaskScheduler,
    private val taskHandler: TaskHandler,
    private val kafkaMessageProducerConfigImpl: MessageProducerConfigImpl,
) : TaskSchedulerQueue {

    companion object {
        private val queue = PriorityQueue<ScheduledTaskItem>(compareBy({ it.priority }, { it.executionTime }))
        private val taskIdCounter = java.util.concurrent.atomic.AtomicLong(0)
        private val log = LoggerFactory.getLogger(TaskSchedulerQueue::class.java)
    }

    override suspend fun scheduleTask(
        executionTime: LocalDateTime,
        dependencies: Set<String>,
        priority: Int
    ): ScheduledTaskItem {
        val taskId: UUID = UUID.fromString("Task-${taskIdCounter.incrementAndGet()}")

        // Create a task proxy instance
        val taskProxyInstance = createTaskProxy(
            TaskSchedulerQueueImpl::class.java,
            taskHandler,
            executionTime
        )

        val taskItem = ScheduledTaskItem(taskId, taskProxyInstance as ScheduledFuture<*>, executionTime, dependencies, priority)

        // send the task item to the appropriate Kafka topic using proxy
        createKafkaTopicsProxy(
            TaskSchedulerQueueImpl::class.java,
            taskItem,
            kafkaMessageProducerConfigImpl
        )

        log.info("Task scheduled: taskId={}, executionTime={}, dependencies={}, priority={}", taskId, executionTime, dependencies, priority)

        return taskItem
    }

    override suspend fun rescheduleTask(taskItem: ScheduledTaskItem, newExecutionTime: LocalDateTime) {
        taskItem.scheduledTask.cancel(true)
        val newScheduledTask = taskScheduler.schedule({ taskItem.scheduledTask.run {  } }, newExecutionTime.toInstant(java.time.ZoneOffset.UTC))
        taskItem.scheduledTask = newScheduledTask
        taskItem.executionTime = newExecutionTime
        queue.add(taskItem)

        log.info("Task rescheduled: taskId={}, newExecutionTime={}", taskItem.taskId, newExecutionTime)
    }

    override suspend fun cancelTask(taskItem: ScheduledTaskItem) {
        taskItem.scheduledTask.cancel(true)
        queue.remove(taskItem)

        log.info("Task canceled: taskId={}", taskItem.taskId)
    }

    override suspend fun adjustTaskPriority(taskId: UUID, newPriority: Int) {
        val taskItem = queue.find { it.taskId == taskId }
        if(taskItem != null) {
            taskItem.priority = newPriority
            // Re-sort the queue based on priority and execution time
            val tasks = ArrayList(queue)
            queue.clear()
            queue.addAll(tasks.sortedWith(compareBy({ it.priority }, { it.executionTime })))

            log.info("Task priority adjusted: taskId={}, newPriority={}", taskId, newPriority)
        }
    }

    override suspend fun taskCompleted(taskId: UUID) {
        // When a task is completed, remove it and adjust priorities of dependent tasks
        val completedTask = queue.find { it.taskId == taskId }
        if(completedTask != null) {
            queue.remove(completedTask)
            adjustPrioritiesForDependencies(completedTask.dependencies)

            log.info("Task completed: taskId={}", taskId)
        }
    }

    override suspend fun adjustPrioritiesForDependencies(dependencies: Set<String>) {
        for (taskId in dependencies) {
            val taskItem = queue.find { it.taskId == UUID.fromString(taskId) }
            taskItem?.let {
                // Increase priority of dependent tasks when their dependencies are completed
                it.priority++
                adjustPrioritiesForDependencies(it.dependencies)
            }
        }

        // Re-sort the queue based on priority and execution time
        val tasks = ArrayList(queue)
        queue.clear()
        queue.addAll(tasks.sortedWith(compareBy({ it.priority }, { it.executionTime })))
    }

    override fun getNextScheduledTask(): ScheduledTaskItem? {
        return queue.poll()
    }

    override fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    override fun getEstimatedTaskExecutionTime(taskId: UUID): LocalDateTime {
        val taskItem = queue.find { it.taskId == taskId }
        return taskItem?.let { it.executionTime } ?: LocalDateTime.MIN
    }

//    override suspend fun adjustTaskExecutionTime(taskId: UUID, newExecutionTime: LocalDateTime) {
//        val taskItem = queue.find { it.taskId == taskId }
//        taskItem?.let {
//            val durationUntilNewExecutionTime = Duration.between(LocalDateTime.now(), newExecutionTime)
//            it.scheduledTask.cancel(true)
//            val newScheduledTask = taskScheduler.schedule({ it.scheduledTask.run {  } }, durationUntilNewExecutionTime.seconds, TimeUnit.SECONDS)
//            it.scheduledTask = newScheduledTask
//            it.executionTime = newExecutionTime
//            queue.add(it)
//
//            log.info("Task execution time adjusted: taskId={}, newExecutionTime={}", taskId, newExecutionTime)
//        }
//    }
}
