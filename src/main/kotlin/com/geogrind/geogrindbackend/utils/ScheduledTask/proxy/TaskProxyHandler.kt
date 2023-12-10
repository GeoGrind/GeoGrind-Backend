package com.geogrind.geogrindbackend.utils.ScheduledTask.proxy

import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.DefaultDeletionTask
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.SessionDeletionTask
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.TaskHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.time.LocalDateTime

// Invocation handler for the proxy to schedule the appropriate task
class TaskProxyHandler(
    private val taskType: Class<out TaskHandler>,
    private val taskScheduler: TaskScheduler,
    private val session: Sessions?,
    private val executionTime: LocalDateTime,
) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        val taskTypeAnnotation = method.getAnnotation(TaskType::class.java)
        log.info("I have been invoked!!!!!!")
        if (taskTypeAnnotation != null) {
            try {
                log.info("Create scheduled task successfully!!")
                val taskType = taskTypeAnnotation.value
                return TaskFactory.createTask(taskType, taskScheduler, session, executionTime)
            } catch (error: Exception) {
                error.printStackTrace()
                log.error("Received annotation of task type, but failed to schedule task with error: $error")
            }
        }
        // Default behavior if no annotation is present
        return method.invoke(taskType.getDeclaredConstructor().newInstance(), *args.orEmpty())
    }

    companion object {
        private val log = LoggerFactory.getLogger(TaskProxyHandler::class.java)
    }
}

object TaskFactory {
    fun createTask(taskType: TaskTypeEnum, taskScheduler: TaskScheduler, session: Sessions?, executionTime: LocalDateTime): ScheduledTaskItem {
        return when(taskType) {
            TaskTypeEnum.SESSION_DELETION -> {
                SessionDeletionTask().scheduleSessionTask(session!!, executionTime) // delete the session task
            }
            TaskTypeEnum.DEFAULT -> {
                DefaultDeletionTask(taskScheduler).scheduleDefaultTask(executionTime) // delete the default task
            }
        }
    }

    // Function to create a proxy
    inline fun <reified T : TaskHandler> createTaskProxy(taskScheduler: TaskScheduler, session: Sessions?, executionTime: LocalDateTime): T {
        val proxyHandler = TaskProxyHandler(T::class.java, taskScheduler, session, executionTime)
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            proxyHandler,
        ) as T
    }
}