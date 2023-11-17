package com.geogrind.geogrindbackend.services.sessions

import com.geogrind.geogrindbackend.dto.session.CreateSessionDto
import com.geogrind.geogrindbackend.dto.session.DeleteSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.GetSessionByIdDto
import com.geogrind.geogrindbackend.dto.session.UpdateSessionByIdDto
import com.geogrind.geogrindbackend.exceptions.sessions.SessionBadRequestException
import com.geogrind.geogrindbackend.exceptions.sessions.SessionConflictException
import com.geogrind.geogrindbackend.exceptions.sessions.SessionNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.courses.Courses
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.sessions.SessionsRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Optional
import java.util.UUID

@Service
@CacheConfig(cacheNames = ["sessionCache"])
class SessionServiceImpl(
    private val userAccountRepository: UserAccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val sessionsRepository: SessionsRepository
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

        waitSomeTime() // wait for Redis to get the user account

        // find the user account
        val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            requestDto.userAccountId!!
        )
        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException("Cannot find user profile with profile id: ${requestDto.userAccountId}")
        }

        // find the user profile
        waitSomeTime() // wait for Redis to get the user profile
        val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            findUserAccount.get()
        )

        if(findUserProfile.isEmpty) {
            throw UserProfileNotFoundException("Cannot find user profile with profile id: ${requestDto.userAccountId}")
        }

        // find the session according to this profile
        val findCurrentSession: Optional<Sessions> = sessionsRepository.findByProfile(
            userProfile = findUserProfile.get()
        )

        return findCurrentSession.orElse(null)
    }

    // create a session
    @CacheEvict(cacheNames = ["sessions"], allEntries = true)
    @Transactional
    override suspend fun createSession(
        @Valid
        requestDto: CreateSessionDto
    ): Sessions {
        val userAccountId = requestDto.userAccountId
        val course = requestDto.course
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

        val newSession = Sessions (
            course = course,
            profile = findUserProfile.get(),
            startTime = startTime,
            numberOfLikers = numberOfLikers,
            stopTime = stopTime,
            description = description,
        )

        findUserProfile.get().apply {
            this.session = newSession
        }

        userProfileRepository.save(findUserProfile.get())
        sessionsRepository.save(newSession)

        return newSession
    }

    @CacheEvict(cacheNames = ["sessions"], allEntries = true)
    @Transactional
    override suspend fun updateSessionById(
        @Valid
        requestDto: UpdateSessionByIdDto
    ): Sessions {
        val userAccountId = requestDto.userAccountId
        val updateCourse = requestDto.updateCourse
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
            this.course = updateCourse ?: this.course
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

        return currentSession
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["sessions"], key = " '#requestDto.user_account_id' "),
            CacheEvict(cacheNames = ["sessions"], allEntries = true)
        ]
    )
    @Transactional
    override suspend fun deleteSessionById(
        @Valid
        requestDto: DeleteSessionByIdDto
    ) {
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

        sessionsRepository.deleteById(currentSession.sessionId!!)
        findUserProfile.get().session = null
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