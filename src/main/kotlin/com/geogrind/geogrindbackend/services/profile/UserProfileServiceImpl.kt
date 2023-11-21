package com.geogrind.geogrindbackend.services.profile

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.geogrind.geogrindbackend.dto.profile.CreateUserProfileDto
import com.geogrind.geogrindbackend.dto.profile.GetUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.dto.profile.UpdateUserProfileByUserAccountIdDto
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileBadRequestException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.courses.Courses
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.courses.CoursesRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.utils.Validation.registration.UserAccountValidationHelper
import jakarta.validation.Valid
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.fasterxml.jackson.module.kotlin.readValue
import com.geogrind.geogrindbackend.dto.profile.DeleteCoursesDto
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookie
import com.geogrind.geogrindbackend.utils.GrantPermissions.GrantPermissionHelper
import com.geogrind.geogrindbackend.utils.Middleware.JwtAuthenticationFilterImpl
import io.github.cdimascio.dotenv.Dotenv
import jakarta.servlet.http.Cookie
import org.springframework.boot.autoconfigure.security.SecurityProperties.User
import org.springframework.cache.annotation.Caching
import java.io.File
import java.time.LocalDate
import java.util.*
import javax.swing.text.html.Option
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Service
@CacheConfig(cacheNames = ["userProfileCache"])
class UserProfileServiceImpl(
    private val userProfileRepository: UserProfileRepository,
    private val userAccountRepository: UserAccountRepository,
    private val coursesRepository: CoursesRepository,
    private val permissionsRepository: PermissionRepository,
    private val validationObj: UserAccountValidationHelper,
    private val grantPermissionHelper: GrantPermissionHelper,
    private val createTokenCookie: CreateTokenCookie,
) : UserProfileService {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    private val s3BucketName = dotenv["AWS_PFP_BUCKET_NAME"]

    // get all the users profiles
    @Cacheable(cacheNames = ["userProfiles"])
    @Transactional(readOnly = true)
    override suspend fun getAllUserProfile(): List<UserProfile> {
        waitSomeTime() // wait for redis
        return userProfileRepository.findAll()
    }

    // get user profile by user account id
    /**
     * Need to fix: [Request processing failed: org.springframework.data.redis.serializer.SerializationException: Could not read JSON:failed to lazily initialize a collection: could not initialize proxy - no Session (through reference chain: com.geogrind.geogrindbackend.models.user_profile.UserProfile["courses"]) ] with root cause
     *
     * org.hibernate.LazyInitializationException: failed to lazily initialize a collection: could not initialize proxy - no Session
     * Current temporary solution: Remove Redis caching for this endpoint
     */
//    @Cacheable(cacheNames = ["userProfiles"], key = " '#requestDto.user_account_id' ", unless = " #result == null ")
    @Transactional(readOnly = true)
    override suspend fun getUserProfileByUserAccountId(
        @Valid requestDto: GetUserProfileByUserAccountIdDto
    ): UserProfile {

//        waitSomeTime() // wait for Redis to get the user account

        // find the user account
        val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(requestDto.user_account_id)

        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException(requestDto.user_account_id.toString())
        }

//        waitSomeTime() // wait for Redis to get the user profile

        val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(findUserAccount.get())

        if(findUserProfile.isEmpty) {
            throw UserProfileNotFoundException("Cannot find user profile with profile id: ${requestDto.user_account_id}")
        }

        return findUserProfile.get()
    }

    // create an empty user profile
    @CacheEvict(cacheNames = ["userProfiles"], allEntries = true)
    @Transactional
    override suspend fun createEmptyUserProfile(
        @Valid requestDto: CreateUserProfileDto
    ): UserProfile {
        var username: String = requestDto.username
        var user_account: UserAccount = requestDto.user_account

        // check if this user_account has the profile before


        val new_user_profile: UserProfile = UserProfile(
            username = username,
            userAccount = user_account,
            createdAt = Date(),
            updatedAt = Date(),
        )

        return userProfileRepository.save(new_user_profile)
    }

    // update user profile by user account id
    @CacheEvict(cacheNames = ["userProfiles"], allEntries = true)
    @Transactional
    override suspend fun updateUserProfileByUserAccountId(
        @Valid requestDto: UpdateUserProfileByUserAccountIdDto
    ): Pair<UserProfile, Cookie> {
        var username: String? = requestDto.username
        var emoji: String? = requestDto.emoji
        var program: String? = requestDto.program
        var courseCodes: Array<String>? = requestDto.courseCodes
        var year_of_graduation: Int? = requestDto.year_of_graduation
        var university: String? = requestDto.university

        log.info("Course codes: ${courseCodes!!.joinToString(", ")}")

        // find the user account that is linked to this profile
        var findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            requestDto.user_account_id!! // the user account id will always be presented as it is extracted from cookies
        )

        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException(requestDto.user_account_id.toString())
        }

        log.info("User id: ${requestDto.user_account_id}")

        // find the user profile using the one-to-one relationship with the user account
        var findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            user_account = findUserAccount.get()
        )

        if(findUserProfile.isEmpty) {
            throw UserProfileNotFoundException(requestDto.user_account_id.toString())
        }

        var update_profile_errors: MutableMap<String, String> = HashMap()

        if(username != null) {
            validationObj.validateUsername(
                username,
                update_profile_errors,
            )
        }

        if(update_profile_errors.isNotEmpty()) {
            throw UserProfileBadRequestException(update_profile_errors)
        }

        log.info("Profile: ${findUserProfile.get()}, Username: $username, Emoji: $emoji, Program: $program, Year of grad: $year_of_graduation, University: $university, CourseCodes: $courseCodes")

        // map the course code with the course name and create the course object
        val courses: MutableSet<Courses>? = findUserProfile.get().courses

        val currentCourses: Set<Courses> = coursesRepository.findAll().toSet()
        val currentCourseCode: MutableSet<String> = HashSet()
        currentCourses.forEach { courses: Courses ->
            currentCourseCode.add(courses.courseCode)
        }

        if(courseCodes!!.isNotEmpty()) {
            val filePath = "src/main/kotlin/com/geogrind/geogrindbackend/utils/CoursesMap/UWCourses.json"

            courseCodes.forEach { courseCode ->
                val courseName: String? = getCourseName(
                    courseCode = courseCode,
                    filePath = filePath
                )
                log.info("Course Name: $courseName")
                val course = Courses(
                    profile = findUserProfile.get(),
                    courseCode = courseCode,
                    courseName = courseName!!,
                    createdAt = Date(),
                    updatedAt = Date(),
                )
                log.info("Course: $course")
                if(course.courseCode !in currentCourseCode) {
                    coursesRepository.save(course)
                    courses!!.add(course)
                }
            }
        }

        val newPermissions: Set<Permissions> = setOf(
            Permissions(
                permission_name = PermissionName.CAN_CREATE_SESSION,
                userAccount = findUserAccount.get(),
                createdAt = Date(),
                updatedAt = Date(),
            ),
        )

        // save the user profile to the database
        findUserProfile.get().apply {
            this.username = username ?: findUserAccount.get().username
            this.emoji = emoji ?: this.emoji
            this.program = program ?: this.program
            this.courses = courses ?: this.courses
            if(this.courses != null) {
                // give the user the permissions to create and update the session
                grantPermissionHelper.grant_permission_helper(
                    newPermissions = newPermissions,
                    currentUserAccount = findUserAccount.get(),
                )
            }
            this.year_of_graduation = year_of_graduation ?: this.year_of_graduation
            this.university = university ?: this.university
            this.updatedAt = Date()
        }

        val allCurrentPermissions: MutableSet<Permissions>? = findUserAccount.get().permissions
        val allCurrentPermissionsName: MutableSet<PermissionName> = HashSet()
        allCurrentPermissions!!.forEach {permissions ->
            allCurrentPermissionsName.add(permissions.permission_name)
        }

        // create the new jwt token and reset token in cookie
        val newJwtToken = createTokenCookie.generateJwtToken(
            expirationTime = 3600,
            bucketName = s3BucketName,
            user_id = findUserAccount.get().id!!,
            permissionNames = allCurrentPermissionsName,
            secret_key = geogrindSecretKey,
        )

        val newCookie: Cookie = createTokenCookie.createTokenCookie(
            expirationTime = 3600,
            token = newJwtToken,
        )

        userProfileRepository.save(findUserProfile.get())

        return Pair(
            first = findUserProfile.get(),
            second = newCookie,
        )
    }

    // delete course from the user profile
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["userProfiles"], key = " '#requestDto.user_account_id' "),
            CacheEvict(cacheNames = ["userProfiles"], allEntries = true)
        ]
    )
    @Transactional
    override suspend fun deleteCourseFromProfile(
        @Valid
        requestDto: DeleteCoursesDto
    ) : Cookie? {
        var courseCodesDelete: Array<String> = requestDto.coursesDelete

        // find the user account that is linked to this profile
        var findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            requestDto.user_account_id!!
        )

        if(findUserAccount.isEmpty) {
            throw UserAccountNotFoundException(requestDto.user_account_id.toString())
        }

        var findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            user_account = findUserAccount.get()
        )

        if(findUserProfile.isEmpty) {
            throw UserProfileNotFoundException(requestDto.user_account_id.toString())
        }

        val currentCourses: MutableSet<Courses>? = findUserProfile.get().courses

        // find the courses need to be deleted -> then delete it from the database
        val coursesToDelete = currentCourses!!.filter { courses: Courses ->
            courses.courseCode in courseCodesDelete
        }

        log.info("Course to delete: $coursesToDelete")
        coursesToDelete.forEach { courses: Courses ->
            coursesRepository.deleteById(courses.courseId!!)
        }

        currentCourses.removeIf { courses: Courses ->
            courses.courseName in courseCodesDelete
        }

        // remove the permission to create and update session if all the courses are deleted
        // get all the current permissions
        if(currentCourses.isEmpty()) {
            grantPermissionHelper.takeAwayPermissionHelper(
                permissionToDelete = setOf(
                    PermissionName.CAN_CREATE_SESSION,
                    PermissionName.CAN_UPDATE_SESSION,
                    PermissionName.CAN_STOP_SESSION,
                ),
                currentUserAccount = findUserAccount.get(),
            )

            // create new jwt token
            val allCurrentPermissions: MutableSet<Permissions>? = findUserAccount.get().permissions
            val allCurrentPermissionsName: MutableSet<PermissionName> = HashSet()
            allCurrentPermissions!!.forEach { permissions ->
                allCurrentPermissionsName.add(permissions.permission_name)
            }

            val newJwtToken = createTokenCookie.generateJwtToken(
                expirationTime = 3600,
                bucketName = s3BucketName,
                user_id = findUserAccount.get().id!!,
                permissionNames = allCurrentPermissionsName,
                secret_key = geogrindSecretKey,
            )

            val newCookie: Cookie = createTokenCookie.createTokenCookie(
                expirationTime = 3600,
                token = newJwtToken,
            )

            return newCookie
        }

        userProfileRepository.save(findUserProfile.get())

        return null
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserProfileServiceImpl::class.java)
        private fun waitSomeTime() {
            log.info("Long Wait Begin")
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            println("Long Wait End")
        }

        private fun getCourseName(courseCode: String, filePath: String): String? {
            return try {
                val objectMapper = ObjectMapper().registerKotlinModule()
                val coursesMap: Map<String, CoursesMapping> = objectMapper.readValue(File(filePath).readText())
                coursesMap[courseCode]?.name
            } catch (e: Exception) {
                log.info("Error while reading json file: $e")
                null
            }
        }

        @Serializable
        data class CoursesMapping(
            val id: String,
            val name: String,
            val university: String,
        )
    }
}