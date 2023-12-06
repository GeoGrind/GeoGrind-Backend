package com.geogrind.geogrindbackend.utils.ScheduledTask.proxy

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.TaskHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.time.LocalDateTime

// Invocation handler for the proxy to redirect the task to the appropriate task queue
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
                TaskTypeEnum.SESSION_DELETION -> return method.invoke(taskHandler.sessionDeletionTaskHandler(
                    executionTime
                ), *args.orEmpty())
                else -> createTaskProxy(target, taskHandler, executionTime)
            }
        }
        // Default behavior if no annotation is present
        return method.invoke(target, *args.orEmpty())
    }
}

// Function to create a proxy
inline fun <reified T : Any> createTaskProxy(target: T, taskHandler: TaskHandler, executionTime: LocalDateTime): T {
    val proxyHandler = TaskProxyHandler(target, taskHandler, executionTime)
    return Proxy.newProxyInstance(
        target::class.java.classLoader,
        arrayOf(T::class.java),
        proxyHandler
    ) as T
}