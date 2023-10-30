package com.geogrind.geogrindbackend.config.security

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.utils.middleware.JwtAuthenticationFilterImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

// configure the Spring security for each endpoint
@Configuration
class SecurityConfigImpl : SecurityConfig {

    @Bean
    override fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/user_account/all",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/user_account/{user_id}",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/user_account/register",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(), "http://localhost:8080/geogrind/user_account/change_password/{user_id}",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/user_account/delete_account/{user_id}",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/confirm-email/{token}",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/confirm-password-change/{token}",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/confirm-account-deletion/{token}",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/user_account/login",
                        )
                    ).permitAll()
                    .requestMatchers(
                        MvcRequestMatcher(
                            HandlerMappingIntrospector(),
                            "http://localhost:8080/geogrind/user_account/verify-login/{token}",
                        )
                    ).hasAnyAuthority(PermissionName.CAN_VERIFY_OTP.toString())
            }
            .addFilterBefore(JwtAuthenticationFilterImpl(), AnonymousAuthenticationFilter::class.java)
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint { _, response, _ ->
                        // Handle unauthorized requests
                    }
            }
            .csrf(Customizer.withDefaults())

        return http.build()
    }
}