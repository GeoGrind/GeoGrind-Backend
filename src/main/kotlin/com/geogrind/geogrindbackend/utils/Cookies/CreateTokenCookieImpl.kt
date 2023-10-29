package com.geogrind.geogrindbackend.utils.Cookies

import com.geogrind.geogrindbackend.models.permissions.Permission
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import java.time.Instant
import java.util.*

class CreateTokenCookieImpl : CreateTokenCookie {
    override fun generateJwtToken(expirationTime: Long, user_id: UUID, permissions: Set<Permission>, secret_key: String): String {
        val set_expired_time = Instant.now().plusSeconds(expirationTime)

        val key = Keys.hmacShaKeyFor(secret_key.toByteArray())

        val token = Jwts.builder()
            .claim("user_id", user_id)
            .claim("permissions", permissions)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(set_expired_time))
            .signWith(key)
            .compact()

        return token
    }

    override fun createTokenCookie(expirationTime: Int, token: String): Cookie {

        // create the cookie
        val cookie = Cookie("JWT-TOKEN", token)
        cookie.path = "/"
        cookie.maxAge = expirationTime
        cookie.isHttpOnly = false

        return cookie
    }
}