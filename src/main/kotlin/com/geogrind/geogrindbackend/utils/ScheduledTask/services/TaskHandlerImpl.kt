package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.ScheduledFuture

// Implement different type of tasks
class SessionDeletionTask(
    private val taskScheduler: TaskScheduler
) : TaskHandler {
    @TaskType(TaskTypeEnum.DEFAULT)
    override fun scheduleDefaultTask(executionTime: LocalDateTime): ScheduledFuture<*> {
        return taskScheduler.schedule({ println("Pass") }, executionTime.toInstant(java.time.ZoneOffset.UTC))
    }

    @TaskType(TaskTypeEnum.SESSION_DELETION)
    override fun scheduleSessionTask(executionTime: LocalDateTime): ScheduledFuture<*> {
//        waitSomeTime(executionTime)
        val task = Runnable(
            fun() {
                log.info("A session has been scheduled to be deleted at: $executionTime")
            }
        )
        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
        return toDoFeature
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionDeletionTask::class.java)

        // function to wait for a certain time
        private fun waitSomeTime(executionTime: LocalDateTime) {
            val timeFinal: Long = executionTime.toEpochSecond(ZoneOffset.UTC)
            val timeStart: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            val duration: Long = (timeFinal - timeStart) * 1000
            log.info("Long Wait Begin for: $duration seconds!!")
            try {
                Thread.sleep(duration)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            log.info("Long Wait End!")
        }
    }
}

class DefaultDeletionTask(
    private val taskScheduler: TaskScheduler
) : TaskHandler {
    @TaskType(TaskTypeEnum.DEFAULT)
    override fun scheduleDefaultTask(executionTime: LocalDateTime): ScheduledFuture<*> {
        val task = Runnable(
            fun() {
                println("Default task handler called!!!!")
            }
        )
        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
        return toDoFeature
    }

    @TaskType(TaskTypeEnum.SESSION_DELETION)
    override fun scheduleSessionTask(executionTime: LocalDateTime): ScheduledFuture<*> {
        return taskScheduler.schedule({ println("Pass") }, executionTime.toInstant(java.time.ZoneOffset.UTC))
    }
}