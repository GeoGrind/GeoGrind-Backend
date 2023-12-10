package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.Task
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.ScheduledFuture

// Implement different type of tasks
class SessionDeletionTask : TaskHandler {
    @TaskType(TaskTypeEnum.DEFAULT)
    override fun scheduleDefaultTask(executionTime: LocalDateTime): ScheduledTaskItem {
        return ScheduledTaskItem(
            taskId = UUID.randomUUID(),
            executionTime = executionTime,
        )
    }

    @TaskType(TaskTypeEnum.SESSION_DELETION)
    override fun scheduleSessionTask(session: Sessions, executionTime: LocalDateTime): ScheduledTaskItem {
        return ScheduledTaskItem(
            taskId = UUID.randomUUID(),
            scheduledTask = session.sessionId?.let {
                Task(
                    sessionId = it
                )
            },
            executionTime = executionTime,
        )
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
    override fun scheduleDefaultTask(executionTime: LocalDateTime): ScheduledTaskItem {
        return ScheduledTaskItem(
            taskId = UUID.randomUUID(),
            executionTime = executionTime,
        )
    }

    @TaskType(TaskTypeEnum.SESSION_DELETION)
    override fun scheduleSessionTask(session: Sessions, executionTime: LocalDateTime): ScheduledTaskItem {
        return ScheduledTaskItem(
            taskId = UUID.randomUUID(),
            executionTime = executionTime,
        )
    }
}