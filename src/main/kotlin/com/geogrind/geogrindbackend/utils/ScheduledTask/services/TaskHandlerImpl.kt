package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ScheduledFuture

//class TaskHandlerImpl(
//    private val taskScheduler: TaskScheduler
//) : TaskHandler {
//    override fun sessionDeletionTaskHandler(executionTime: LocalDateTime) : ScheduledFuture<*> {
//        val task = Runnable(
//            fun() {
//                println("Session deleted!!!!")
//            }
//        )
//        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
//        return toDoFeature
//
//        /*  Still need to be implemented!!!! */
//    }
//
//    override fun defaultTaskHandler(executionTime: LocalDateTime): ScheduledFuture<*> {
//        val task = Runnable(
//            fun() {
//                println("Default task handler called!!!!")
//            }
//        )
//        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
//        return toDoFeature
//    }
//}

// Implement different type of tasks
class SessionDeletionTask(
    private val taskScheduler: TaskScheduler
) : TaskHandler.SessionDeletionTask {
    @TaskType(TaskTypeEnum.SESSION_DELETION)
    override fun scheduleTask(executionTime: LocalDateTime): ScheduledFuture<*> {
        log.info("I'm inside here!!!!!")
        val task = Runnable(
            fun() {
                println("Session deleted!!!!")
            }
        )
        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
        return toDoFeature
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionDeletionTask::class.java)
    }
}

class DefaultDeletionTask(
    private val taskScheduler: TaskScheduler
) : TaskHandler.DefaultDeletionTask {
    @TaskType(TaskTypeEnum.DEFAULT)
    override fun scheduleTask(executionTime: LocalDateTime): ScheduledFuture<*> {
        val task = Runnable(
            fun() {
                println("Default task handler called!!!!")
            }
        )
        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
        return toDoFeature
    }
}