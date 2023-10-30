package com.geogrind.geogrindbackend.utils.middleware

import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountUnauthorizedException
import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.IncorrectClaimException
import io.jsonwebtoken.InvalidClaimException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.MissingClaimException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

class JwtAuthenticationFilterImpl : OncePerRequestFilter() {

    private val dotenv: Dotenv = Dotenv.configure().directory("/Users/kenttran/Desktop/Desktop_Folders/side_projects/GeoGrind-Backend/.env").load()

    private val geogrindSecretKey: String = dotenv["GEOGRIND_SECRET_KEY"]

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt_token: String? = extractToken(
                request = request,
                cookieName = "JWT-TOKEN",
            )

            val requestUri: String = request.requestURI
            val requiredPermissions: Set<PermissionName> = determineRequiredPermissions(
                requestUri = requestUri,
            )

            // decode the token to get the claims
            val decoded_token: Claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray()))
                .build()
                .parseSignedClaims(jwt_token)
                .payload

            val match_permissions: Boolean = hasRequiredPermissions(
                decoded_token = decoded_token,
                permissionList = requiredPermissions,
            )

            if(!match_permissions) sendUnauthorizedResponse()

        } catch (e: ExpiredJwtException) {
            log.info("Token has expired: ${e.message}")
            sendExpiredTokenResponse(
                decoded_token = e.claims,
                message = e.message as String,
            )
        } catch (e: MalformedJwtException) {
            log.info("Malformed token: ${e.message}")
            sendMalformedJwtException(e.message as String)
        } catch (e: SignatureException) {
            log.info("Invalid token signature: ${e.message}")
            sendSignatureExceptionResponse(e.message as String)
        } catch (e: InvalidClaimException) {
            log.info("Invalid token claim: ${e.message}")
            sendInvalidTokenResponse(
                decoded_token = e.claims,
                message = e.message as String,
            )
        } catch (e: MissingClaimException) {
            log.info("Missing token claim: ${e.message}")
            sendMissingTokenResponse(
                decoded_token = e.claims,
                message = e.message as String,
            )
        } catch (e: JwtException) {
            log.info("Jwt token encountered: ${e.message}")
            throw RuntimeException(e.message)
        }
    }

    private fun extractToken(
        request: HttpServletRequest,
        cookieName: String,
    ): String? {
        try {
            val cookie: Cookie? = WebUtils.getCookie(
                request,
                cookieName,
            )
            return cookie!!.value
        } catch (e: RuntimeException) {
            // Handle the exception
            log.info("Cannot extract the token from the cookie!")
        }
        return null
    }

    private fun determineRequiredPermissions(requestUri: String): Set<PermissionName> {
        return when {
            requestUri == "/geogrind/user_profile/view_profile" -> setOf(
                PermissionName.CAN_VIEW_PROFILE
            )
            requestUri == "/geogrind/user_profile/edit_profile" -> setOf(
                PermissionName.CAN_EDIT_PROFILE
            )
            requestUri == "/geogrind/user_account/verify-login/{token}" -> setOf(
                PermissionName.CAN_VERIFY_OTP
            )
            else -> return setOf()
        }
    }

    private fun hasRequiredPermissions(
        decoded_token: Claims,
        permissionList: Set<PermissionName>,
    ): Boolean {
        val permissions: Set<Permission> = decoded_token["permissions"] as Set<Permission>

        for(permission in permissions) {
            if(permission.permission_name !in permissionList) {
                return false
            }
        }
        return true
    }

    private fun sendUnauthorizedResponse() {
        // unauthorized user
        throw UserAccountUnauthorizedException(
            message = "Unauthorized accessed!"
        )
    }

    private fun sendMissingTokenResponse(
        decoded_token: Claims,
        message: String
    ) {
        // missing token
        throw MissingClaimException(
            null,
            decoded_token,
            message,
            Any(),
            "Token is missing in the request header!"
        )
    }

    private fun sendMalformedJwtException(message: String) {
        // invalid structure
        throw MalformedJwtException(
            message
        )
    }

    private fun sendInvalidTokenResponse(
        decoded_token: Claims,
        message: String
    ) {
        // invalid claims
        throw IncorrectClaimException(
            null,
            decoded_token,
            "JWT-TOKEN",
            Any(),
            message
        )
    }

    private fun sendSignatureExceptionResponse(
        message: String
    ) {
        // invalid signature
        throw SignatureException(
            message
        )
    }

    private fun sendExpiredTokenResponse(
        decoded_token: Claims,
        message: String
    ) {
        // expired token
        throw ExpiredJwtException(
            null,
            decoded_token,
            message
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtAuthenticationFilterImpl::class.java)
    }
}