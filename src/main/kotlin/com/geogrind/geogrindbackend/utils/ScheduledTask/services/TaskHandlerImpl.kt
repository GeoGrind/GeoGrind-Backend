package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ScheduledFuture

@Service
class TaskHandlerImpl(
    private val taskScheduler: TaskScheduler
) : TaskHandler {
    override fun sessionDeletionTaskHandler(executionTime: LocalDateTime) : ScheduledFuture<*> {
        val task = Runnable(
            fun() {
                println("Session deleted!!!!")
            }
        )
        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
        return toDoFeature

        /*  Still need to be implemented!!!! */
    }

    override fun defaultTaskHandler(executionTime: LocalDateTime): ScheduledFuture<*> {
        val task = Runnable(
            fun() {
                println("Default task handler called!!!!")
            }
        )
        val toDoFeature: ScheduledFuture<*> = taskScheduler.schedule(task, executionTime.toInstant(java.time.ZoneOffset.UTC))
        return toDoFeature
    }
}