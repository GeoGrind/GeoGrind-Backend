package com.geogrind.geogrindbackend.services.sessions

import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfigImpl
import com.geogrind.geogrindbackend.dto.session.CreateSessionDto
import com.geogrind.geogrindbackend.dto.session.DeleteSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.GetSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.UpdateSessionByIdDto
import com.geogrind.geogrindbackend.exceptions.sessions.SessionBadRequestException
import com.geogrind.geogrindbackend.exceptions.sessions.SessionConflictException
import com.geogrind.geogrindbackend.exceptions.sessions.SessionNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.Task
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.sessions.SessionsRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookie
import com.geogrind.geogrindbackend.utils.GrantPermissions.GrantPermissionHelper
import com.geogrind.geogrindbackend.utils.ScheduledTask.proxy.KafkaFactory
import com.geogrind.geogrindbackend.utils.ScheduledTask.proxy.TaskFactory
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.KafkaHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.TaskHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.KafkaTopicsType
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import org.springframework.scheduling.TaskScheduler
import java.time.ZoneId

@Service
@CacheConfig(cacheNames = ["sessionCache"])
class SessionServiceImpl(
    private val userAccountRepository: UserAccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val sessionsRepository: SessionsRepository,
    private val grantPermissionHelper: GrantPermissionHelper,
    private val createTokenCookie: CreateTokenCookie,
    private val taskScheduler: TaskScheduler,
    private val kafkaMessageProducer: MessageProducerConfigImpl,
) : SessionService {

    // get all current sessions
    @Cacheable(cacheNames = ["sessions"])
    @Transactional(readOnly = true)
    override suspend fun getAllSessions(): List<Sessions> {
        waitSomeTime()
        return sessionsRepository.findAll()
    }

    @Cacheable(cacheNames = ["sessions"], key = " '#requestDto.user_account_id' ", unless = " #result == null ")
    @Transactional(readOnly = true)
    override suspend fun getSessionById(
        @Valid
        requestDto: GetSessionByIdDto
    ): Sessions {

        // find the user account
        val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            requestDto.userAccountId!!
        )
        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException("Cannot find user profile with profile id: ${requestDto.userAccountId}")
        }

        // find the user profile
        val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            findUserAccount.get()
        )

        if(findUserProfile.isEmpty) {
            throw UserProfileNotFoundException("Cannot find user profile with profile id: ${requestDto.userAccountId}")
        }

        // find the session according to this profile
        // wait for redis
        waitSomeTime()

        val findCurrentSession: Optional<Sessions> = sessionsRepository.findByProfile(
            userProfile = findUserProfile.get()
        )

        if(findCurrentSession.isEmpty) throw SessionNotFoundException(findUserAccount.get().id.toString())
        return findCurrentSession.get()
    }

    // create a session
    @CacheEvict(cacheNames = ["sessions"], allEntries = true)
    @Transactional
    @TaskType(TaskTypeEnum.SESSION_DELETION)
    @KafkaTopicsType(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC)
    override suspend fun createSession(
        @Valid
        requestDto: CreateSessionDto
    ): Pair<Sessions, Cookie> {
        val userAccountId = requestDto.userAccountId
        val courseCode = requestDto.courseCode
        val startTime = requestDto.startTime
        val duration = requestDto.duration
        val numberOfLikers = requestDto.numberOfLikers
        val description = requestDto.description

        // find the user account
        val findUserAccount = userAccountRepository.findById(userAccountId!!)

        // find the user profile with the user account
        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException(userAccountId.toString())
        }

        // find the user profile
        val findUserProfile = userProfileRepository.findUserProfileByUserAccount(
            findUserAccount.get()
        )

        // create new session for the user profile
        val currentSession: Sessions? = findUserProfile.get().session

        if(currentSession != null) {
            throw SessionConflictException(findUserProfile.get().profile_id.toString())
        }

        val stopTime: Instant = startTime!!.plusMillis(duration)

        if(stopTime.isBefore(startTime)) throw SessionBadRequestException(
            error = "Stop time cannot be before start time!"
        )

        // find the course with the given course code
        val allCurrentCourses = findUserProfile.get().courses
        val sessionCourse = allCurrentCourses!!.find { currentCourse ->
            currentCourse.courseCode == courseCode
        }

        log.info("Session Course: $sessionCourse")

        val newSession = Sessions (
            course = sessionCourse!!,
            profile = findUserProfile.get(),
            startTime = startTime,
            numberOfLikers = numberOfLikers,
            stopTime = stopTime,
            description = description,
        )

        log.info("New Session: $newSession")

        findUserProfile.get().apply {
            this.session = newSession
        }

        userProfileRepository.save(findUserProfile.get())
        sessionsRepository.save(newSession)

        // Schedule the task to delete the session after the duration ends
        /*
        * Testing the kafka stream
        *
        * */

        @TaskType(TaskTypeEnum.SESSION_DELETION)
        val taskProxy = TaskFactory.createTaskProxy<TaskHandler>(taskScheduler = taskScheduler, session = newSession, executionTime = stopTime.atZone(ZoneId.systemDefault()).toLocalDateTime())
        taskProxy.scheduleSessionTask(newSession, stopTime.atZone(ZoneId.systemDefault()).toLocalDateTime())
        val sessionTask = ScheduledTaskItem(
            taskId = UUID.randomUUID(),
            scheduledTask = newSession.sessionId?.let {
                Task(
                    sessionId = it,
                )
            },
            executionTime = stopTime.atZone(ZoneId.systemDefault()).toLocalDateTime(),
        )

        // Send the message to kafka stream
        @KafkaTopicsType(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC)
        val kafkaProxy = KafkaFactory.createKafkaTopicsProxy<KafkaHandler>(
            task = sessionTask,
            kafkaMessageProducer = kafkaMessageProducer,
        )
        kafkaProxy.kafkaSendSessionDeletionMessage(
            task = sessionTask
        )

        log.info("Session has been scheduled to be deleted after $duration")

        // create a new jwt token and refresh a new cookie
        val newCookie: Cookie = createTokenCookie.refreshCookie(
            expirationTime = 3600,
            currentUserAccount = findUserAccount.get(),
        )

        return Pair(
            first = newSession,
            second = newCookie,
        )
    }

    @CacheEvict(cacheNames = ["sessions"], allEntries = true)
    @Transactional
    override suspend fun updateSessionById(
        @Valid
        requestDto: UpdateSessionByIdDto
    ): Pair<Sessions, Cookie> {
        val userAccountId = requestDto.userAccountId
        val updateCourseCode = requestDto.updateCourseCode
        val updateStartTime = requestDto.updateStartTime
        val updateDuration = requestDto.updateDuration
        val updateNumberOfLikers = requestDto.updateNumberOfLikers
        val updateDescription = requestDto.updateDescription

        // find the user account that is linked to this profile
        var findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            userAccountId!!
        )

        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException(userAccountId.toString())
        }

        // find the user profile using the one-to-one relationship with the user account
        var findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            user_account = findUserAccount.get()
        )

        if(findUserProfile.isEmpty) {
            throw UserProfileNotFoundException(userAccountId.toString())
        }

        // create new session for the user profile
        val currentSession: Sessions = findUserProfile.get().session
            ?: throw SessionNotFoundException(findUserProfile.get().profile_id.toString())

        currentSession.apply {
            // find the course with the given course code
            if(updateCourseCode != null) {
                val allCurrentCourses = findUserProfile.get().courses
                val sessionCourse = allCurrentCourses!!.find { currentCourse ->
                    currentCourse.courseCode == updateCourseCode
                }
                this.course = sessionCourse ?: this.course
            }
            this.startTime = updateStartTime ?: this.startTime
            this.stopTime = this.startTime!!.plusMillis(updateDuration ?: 0) ?: this.stopTime
            if(this.stopTime!!.isBefore(this.startTime)) throw SessionBadRequestException(
                error = "Stop time cannot be before start time!"
            )
            this.numberOfLikers = updateNumberOfLikers ?: this.numberOfLikers
            this.description = updateDescription ?: this.description
        }

        sessionsRepository.save(currentSession)
        findUserProfile.get().session = currentSession

        // create the new jwt token and reset token in cookie
        val newCookie: Cookie = createTokenCookie.refreshCookie(
            expirationTime = 3600,
            currentUserAccount = findUserAccount.get()
        )

        return Pair(
            first = currentSession,
            second = newCookie
        )
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["sessions"], key = " '#requestDto.user_account_id' "),
            CacheEvict(cacheNames = ["sessions"], allEntries = true)
        ]
    )
    @Transactional
    override fun deleteSessionById(
        @Valid
        requestDto: DeleteSessionByIdDto
    ) : Cookie {
        val userAccountId = requestDto.userAccountId

        // find the current user
        val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            userAccountId!!
        )

        if(findUserAccount.isEmpty) throw UserAccountNotFoundException(userAccountId.toString())

        val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            user_account = findUserAccount.get()
        )

        if(findUserProfile.isEmpty) throw UserProfileNotFoundException(userAccountId.toString())

        // find the current session
        val currentSession: Sessions = findUserProfile.get().session ?: throw SessionNotFoundException(userAccountId.toString())

        log.info("Current session: $currentSession")

        sessionsRepository.deleteById(currentSession.sessionId!!)
        findUserProfile.get().session = null
        userProfileRepository.save(findUserProfile.get())

        log.info("User Profile: ${findUserProfile.get()}")

        // refresh the token and issue a new cookie
        val newCookie: Cookie = createTokenCookie.refreshCookie(
            expirationTime = 3600,
            currentUserAccount = findUserAccount.get()
        )

        return newCookie
    }

    companion object {
        private val log = LoggerFactory.getLogger(SessionService::class.java)
        private fun waitSomeTime() {
            log.info("Long Wait Begin")
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            log.info("Long Wait End")
        }
    }
}