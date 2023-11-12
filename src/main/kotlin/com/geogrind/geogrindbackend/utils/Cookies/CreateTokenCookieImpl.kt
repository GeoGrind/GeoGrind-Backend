package com.geogrind.geogrindbackend.utils.Cookies

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class CreateTokenCookieImpl : CreateTokenCookie {
    override fun generateJwtToken(expirationTime: Long, user_id: UUID, permissionNames: Set<PermissionName>, secret_key: String, bucketName: String): String {
        val set_expired_time = Instant.now().plusSeconds(expirationTime)

        val key = Keys.hmacShaKeyFor(secret_key.toByteArray())

        val token = Jwts.builder()
            .claim("user_id", user_id)
            .claim("permissions", permissionNames)
            .claim("s3_profile_image_bucket_name", bucketName)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(set_expired_time))
            .signWith(key)
            .compact()
        println(permissionNames)
        return token
    }

    override fun createTokenCookie(expirationTime: Int, token: String): Cookie {

        // create the cookie
        val cookie = Cookie("JWT-TOKEN", token)
        cookie.path = "/"
        cookie.maxAge = expirationTime
        cookie.isHttpOnly = true

        log.info("Cookie set: $cookie, ${cookie.name}, ${cookie.path}")

        return cookie
    }

    companion object {
        private val log = LoggerFactory.getLogger(CreateTokenCookieImpl::class.java)
    }
}