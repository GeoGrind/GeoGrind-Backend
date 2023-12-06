package com.geogrind.geogrindbackend.utils.Cookies

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.exp

@Service
class CreateTokenCookieImpl : CreateTokenCookie {

    // Load env variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey = dotenv["GEOGRIND_SECRET_KEY"]

    private val s3BucketName = dotenv["AWS_PFP_BUCKET_NAME"]

    override fun generateJwtToken(expirationTime: Long, user_id: UUID, permissionNames: Set<PermissionName>, secret_key: String, bucketName: String): String {

        val set_expired_time = Instant.now().plusSeconds(expirationTime)

        val key = Keys.hmacShaKeyFor(secret_key.toByteArray())

        val token = Jwts.builder()
            .claim("user_id", user_id)
            .claim("permissionNames", permissionNames)
            .claim("s3_profile_image_bucket_name", bucketName)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(set_expired_time))
            .signWith(key)
            .compact()
        println(permissionNames)
        return token
    }

    override fun createTokenCookie(expirationTime: Long, token: String): Cookie {

        // create the cookie
        val cookie = Cookie("JWT-TOKEN", token)
        cookie.path = "/"
        cookie.maxAge = expirationTime.toInt()
        cookie.isHttpOnly = true

        log.info("Cookie set: $cookie, ${cookie.name}, ${cookie.path}")

        return cookie
    }

    override fun refreshCookie(expirationTime: Long, currentUserAccount: UserAccount) : Cookie {
        val currentPermissions: MutableSet<Permissions>? = currentUserAccount.permissions
        val currentPermissionsName: MutableSet<PermissionName> = HashSet()
        currentPermissions!!.forEach { permissions ->
            currentPermissionsName.add(permissions.permission_name)
        }

        val newJwtToken = generateJwtToken(
            expirationTime = expirationTime,
            bucketName = s3BucketName,
            user_id = currentUserAccount.id!!,
            permissionNames = currentPermissionsName,
            secret_key = geogrindSecretKey,
        )

        val newCookie: Cookie = createTokenCookie(
            expirationTime = expirationTime,
            token = newJwtToken
        )

        log.info("New cookie is set: $newCookie")

        return newCookie
    }

    companion object {
        private val log = LoggerFactory.getLogger(CreateTokenCookieImpl::class.java)
    }
}