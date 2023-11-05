package com.geogrind.geogrindbackend.utils.Middleware

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountUnauthorizedException
import com.geogrind.geogrindbackend.exceptions.user_profile.CustomMissingCookieException
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
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

@Component
class JwtAuthenticationFilterImpl : OncePerRequestFilter() {

    private val dotenv: Dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey: String = dotenv["GEOGRIND_SECRET_KEY"]

    private val protected_resources: Set<String> = setOf(
        // user profiles
        "/geogrind/user_profile/get_all_profiles",
        "/geogrind/user_profile/get_profile", // get the user profile based on the user account id
        "/geogrind/user_profile/update_profile",

        // file upload to S3 bucket
//        "/geogrind/s3/"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val requestUri: String = request.requestURI

            log.info("$requestUri")

            if(shouldNotFilter(requestUri)) {
                log.info("The endpoint does not require authentication!")
                filterChain.doFilter(request, response)
                return
            }

            val jwt_token: String? = extractToken(
                request = request,
                cookieName = "JWT-TOKEN",
            )

            if(jwt_token == null) {
               throw CustomMissingCookieException(
                   cookieName = "JWT-TOKEN"
               )
            }

            val requiredPermissions: Set<PermissionName> = determineRequiredPermissions(
                requestUri = requestUri,
            )

            val decoded_token: Claims = decodeToken(
                token = jwt_token!!,
            )

            val match_permissions: Boolean = hasRequiredPermissions(
                decoded_token = decoded_token,
                permissionList = requiredPermissions,
            )

            if(!match_permissions) sendUnauthorizedResponse()

            log.info("Verified the endpoint successfully!")
            filterChain.doFilter(request, response)
            return
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

    fun extractToken(
        request: HttpServletRequest,
        cookieName: String,
    ): String? {
        try {
            log.info("Request: $request")
            val cookie = request.cookies
            val cookieValue = cookie?.find { it.name == cookieName }?.value
            log.info("Cookie extracted: $cookieValue")
            return cookieValue
        } catch (e: Exception) {
            log.info("${e.message}")
        }
        return null
    }

    fun decodeToken(
        token: String
    ): Claims {
        // decode the token to get the claims
        val decoded_token: Claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(geogrindSecretKey.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

        return decoded_token
    }

    private fun determineRequiredPermissions(requestUri: String): Set<PermissionName> {
        return when {
            requestUri == "/geogrind/user_profile/get_all_profiles" -> setOf(
                PermissionName.CAN_VIEW_PROFILE,
            )
            requestUri == "/geogrind/user_profile/get_profile" -> setOf(
                PermissionName.CAN_VIEW_PROFILE,
            )
            requestUri == "/geogrind/user_profile/update_profile" -> setOf(
                PermissionName.CAN_VIEW_PROFILE,
                PermissionName.CAN_EDIT_PROFILE,
            )
//            requestUri == "/geogrind/s3/download_all_files/{bucket}" -> setOf(
//                PermissionName.CAN_VIEW_PROFILE,
//                PermissionName.CAN_VIEW_PROFILE,
//            )
//            requestUri == "/geogrind/s3/download_file/{bucket}/{file}"
            else -> return setOf()
        }
    }

    private fun shouldNotFilter(requestUri: String): Boolean {
        return requestUri !in protected_resources
    }

    private fun hasRequiredPermissions(
        decoded_token: Claims,
        permissionList: Set<PermissionName>,
    ): Boolean {
        val permissionsInToken = decoded_token["permissions"] as ArrayList<LinkedHashMap<String, String>>

        log.info("$permissionsInToken")

        if (permissionsInToken != null) {
            // deserialize the linked hash map into the Permission object
            val all_permissions: MutableSet<PermissionName> = HashSet<PermissionName>()
            for(permission in permissionsInToken) {
                val permission_name = permission["permission_name"] as String
                all_permissions.add(enumValueOf<PermissionName>(permission_name))
            }

            permissionList.forEach { required_permission ->
                if (required_permission !in all_permissions) {
                    return false
                }
            }
        } else {
            return false
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