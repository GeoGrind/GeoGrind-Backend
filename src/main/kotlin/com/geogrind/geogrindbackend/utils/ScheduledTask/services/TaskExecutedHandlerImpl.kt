package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.controllers.sessions.SessionsController
import com.geogrind.geogrindbackend.dto.session.DeleteSessionByIdDto
import com.geogrind.geogrindbackend.exceptions.sessions.SessionNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.repositories.sessions.SessionsRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.services.sessions.SessionService
import com.geogrind.geogrindbackend.utils.Middleware.JwtAuthenticationFilterImpl
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping

// Implement different type of tasks execution
class SessionDeletionTaskExecution(
    private val userAccountRepository: UserAccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val sessionRepository: SessionsRepository,
    private val sessionService: SessionService,
) : TaskExecutedHandler {
    @TaskType(TaskTypeEnum.DEFAULT)
    override fun defaultTaskExecuted(task: ScheduledTaskItem) {
        TODO("Not yet implemented")
    }

    @TaskType(TaskTypeEnum.SESSION_DELETION)
    override fun sessionDeletionTaskExecuted(
        task: ScheduledTaskItem,
    ) {
        // proceed to delete the session
        try {
            val sessionId = task.scheduledTask!!.sessionId
            val session = sessionRepository.findById(
                sessionId
            )

            if(session.isEmpty) {
                // the session has been manually deleted by the user -> skip this task
                log.info("Session with ID: $sessionId is empty. This task will be skipped!!")
                return
            }

//            // find the user profile
            val findUserProfile = userProfileRepository.findUserProfileBySession(
                session.get()
            )

            // find the user account
            val findUserAccount = userAccountRepository.findUserAccountByUsername(
                findUserProfile.get().username
            )

//             delete the session
            sessionService.deleteSessionById(
                DeleteSessionByIdDto(
                    userAccountId = findUserAccount.get().id
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
            log.info("Error while executing the session deletion task: $e")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionDeletionTaskExecution::class.java)
    }
}