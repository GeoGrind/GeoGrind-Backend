package com.geogrind.geogrindbackend.utils.ScheduledTask.proxy

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.TaskHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.time.LocalDateTime
import java.util.concurrent.ScheduledFuture

// Invocation handler for the proxy to schedule the appropriate task
class TaskProxyHandler(
    private val target: Any,
    private val taskHandler: TaskHandler,
    private val executionTime: LocalDateTime,
) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        val taskTypeAnnotation = method.getAnnotation(TaskType::class.java)
        if (taskTypeAnnotation != null) {
            val taskType = taskTypeAnnotation.value
            when (taskType) {
                TaskTypeEnum.SESSION_DELETION -> {
                    return method.invoke(
                        taskHandler.sessionDeletionTaskHandler(
                            executionTime
                        ), *args.orEmpty()
                    ) as? ScheduledFuture<*> ?: throw RuntimeException("Unexpected return type while schedule a session deletion!")
                }
                else -> {
                    return method.invoke(taskHandler.defaultTaskHandler(
                        executionTime
                    ), *args.orEmpty()) as? ScheduledFuture<*> ?: throw RuntimeException("Unexpected return type while schedule a default task schedule!")
                }
            }
        }
        log.info("Create scheduled task successfully!!")
        // Default behavior if no annotation is present
        return method.invoke(target, *args.orEmpty())
    }

    companion object {
        private val log = LoggerFactory.getLogger(TaskProxyHandler::class.java)
    }
}

// Function to create a proxy
inline fun <reified T : Any> createTaskProxy(target: T, taskHandler: TaskHandler, executionTime: LocalDateTime): T {
    val proxyHandler = TaskProxyHandler(target, taskHandler, executionTime)
    return Proxy.newProxyInstance(
        target::class.java.classLoader,
        arrayOf(T::class.java),
        proxyHandler,
    ) as T
}