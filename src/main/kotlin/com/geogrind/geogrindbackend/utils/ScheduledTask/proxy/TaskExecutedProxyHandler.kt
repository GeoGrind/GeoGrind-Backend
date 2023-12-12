package com.geogrind.geogrindbackend.utils.ScheduledTask.proxy

import com.geogrind.geogrindbackend.controllers.sessions.SessionsController
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.repositories.sessions.SessionsRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.services.sessions.SessionService
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.SessionDeletionTaskExecution
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.TaskExecutedHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class TaskExecutedProxyHandler(
    private val taskExecutedType: Class<out TaskExecutedHandler>,
    private val task: ScheduledTaskItem,
    private val userAccountRepository: UserAccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val sessionsRepository: SessionsRepository,
    private val sessionService: SessionService,
) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        val taskToExecuteTypeAnnotation = method.getAnnotation(TaskType::class.java)
        log.info("I have been invoked!!!")
        if (taskToExecuteTypeAnnotation != null) {
            try {
                log.info("Execute scheduled task successfully!")
                val taskExecutedType = taskToExecuteTypeAnnotation.value
                return TaskExecutedFactory.executeTask(
                    taskType = taskExecutedType,
                    task = task,
                    userAccountRepository = userAccountRepository,
                    userProfileRepository = userProfileRepository,
                    sessionsRepository = sessionsRepository,
                    sessionService = sessionService,
                )
            } catch (e: Exception) {
                e.printStackTrace()
                log.error("Received annotation of task type, but failed to execute task with error: $e")
            }
        }
        return method.invoke(taskExecutedType.getDeclaredConstructor().newInstance(), *args.orEmpty())
    }

    companion object {
        private val log = LoggerFactory.getLogger(TaskExecutedProxyHandler::class.java)
    }
}

object TaskExecutedFactory {
    fun executeTask (
        taskType: TaskTypeEnum,
        task: ScheduledTaskItem,
        userAccountRepository: UserAccountRepository,
        userProfileRepository: UserProfileRepository,
        sessionsRepository: SessionsRepository,
        sessionService: SessionService,
    ) {
        return when(taskType) {
            TaskTypeEnum.SESSION_DELETION -> {
                SessionDeletionTaskExecution(
                    userAccountRepository = userAccountRepository,
                    userProfileRepository = userProfileRepository,
                    sessionRepository = sessionsRepository,
                    sessionService = sessionService,
                ).sessionDeletionTaskExecuted(
                    task = task,
                )
            }

            TaskTypeEnum.DEFAULT -> TODO("Not yet implemented!!!")
        }
    }

    // function to create a dynamic proxy
    inline fun <reified T : TaskExecutedHandler> createTaskExecutedProxy(
        task: ScheduledTaskItem,
        userAccountRepository: UserAccountRepository,
        userProfileRepository: UserProfileRepository,
        sessionsRepository: SessionsRepository,
        sessionService: SessionService,
    ) : T {
        val proxyHandler = TaskExecutedProxyHandler(
            taskExecutedType = T::class.java,
            task = task,
            userAccountRepository = userAccountRepository,
            userProfileRepository = userProfileRepository,
            sessionsRepository = sessionsRepository,
            sessionService = sessionService,
        )

        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            proxyHandler,
        ) as T
    }
}