package com.geogrind.geogrindbackend.utils.Middleware


import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountUnauthorizedException
import com.geogrind.geogrindbackend.exceptions.user_profile.CustomMissingCookieException
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
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilterImpl : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()

    private val dotenv: Dotenv = Dotenv.configure().directory(".").load()

    private val geogrindSecretKey: String = dotenv["GEOGRIND_SECRET_KEY"]

    private val protected_resources: Set<String> = setOf(
        // user profiles
        "/geogrind/user_profile/get_all_profiles",
        "/geogrind/user_profile/get_profile", // get the user profile based on the user account id
        "/geogrind/user_profile/update_profile",

        // file upload to S3 bucket
        "/geogrind/profile_image/download_all_profile_images",
        "/geogrind/profile_image/download_profile_image",
        "/geogrind/profile_image/delete_profile_image",
        "/geogrind/profile_image/upload_profile_image",

        // sessions
        "/geogrind/sessions/get_all_sessions",
        "/geogrind/sessions/get_session",
        "/geogrind/sessions/create_session",
        "/geogrind/sessions/update_session",
        "/geogrind/sessions/delete_session",
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val requestUri: String = request.requestURI

            log.info("$requestUri")

            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173")
            response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept")
            response.setHeader("Access-Control-Max-Age", "3600")

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
            // user profile
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

            // s3 and cloudfront
            requestUri == "/geogrind/profile_image/download_all_profile_images" -> setOf(
                PermissionName.CAN_VIEW_FILES,
            )
            requestUri == "/geogrind/profile_image/download_profile_image" -> setOf(
                PermissionName.CAN_VIEW_FILES,
            )
            requestUri == "/geogrind/profile_image/delete_profile_image" -> setOf(
                PermissionName.CAN_DELETE_FILES,
            )
            requestUri == "/geogrind/profile_image/upload_profile_image" -> setOf(
                PermissionName.CAN_UPLOAD_FILES,
            )

            // sessions
            requestUri == "/geogrind/sessions/get_all_sessions" -> setOf(
                PermissionName.CAN_VIEW_SESSION,
            )
            requestUri == "/geogrind/sessions/get_session" -> setOf(
                PermissionName.CAN_VIEW_SESSION,
            )
            requestUri == "/geogrind/sessions/create_session" -> setOf(
                PermissionName.CAN_CREATE_SESSION,
            )
            requestUri == "/geogrind/sessions/update_session" -> setOf(
                PermissionName.CAN_UPDATE_SESSION,
            )
            requestUri == "/geogrind/sessions/delete_sessions" -> setOf(
                PermissionName.CAN_STOP_SESSION,
            )
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
        val permissionsInToken = decoded_token["permissions"] as ArrayList<String>

        log.info("$permissionsInToken")

        if (permissionsInToken != null) {
            // deserialize the linked hash map into the Permission object
            val all_permissions: MutableSet<PermissionName> = HashSet<PermissionName>()
            for(permission in permissionsInToken) {
                all_permissions.add(enumValueOf<PermissionName>(permission))
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