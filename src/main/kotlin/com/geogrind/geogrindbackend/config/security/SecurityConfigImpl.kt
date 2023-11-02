package com.geogrind.geogrindbackend.config.security

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.utils.Middleware.JwtAuthenticationFilterImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

// configure the Spring security for each endpoint
@Configuration
class SecurityConfigImpl : SecurityConfig {

    @Bean
    override fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/all",
                            HttpMethod.GET.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/{user_id}",
                            HttpMethod.GET.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/register",
                            HttpMethod.POST.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/change_password/{user_id}",
                            HttpMethod.PATCH.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/delete_account/{user_id}",
                            HttpMethod.DELETE.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/confirm-email/{token}",
                            HttpMethod.GET.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/confirm-password-change/{token}",
                            HttpMethod.GET.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/confirm-account-deletion/{token}",
                            HttpMethod.GET.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/login",
                            HttpMethod.POST.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/user_account/verify-login/{token}",
                            HttpMethod.GET.toString(),
                        )
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher(
                            "/geogrind/message/all",
                            HttpMethod.GET.toString(),
                        )
                    ).permitAll()
            }
            .addFilterBefore(JwtAuthenticationFilterImpl(), AnonymousAuthenticationFilter::class.java)
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint { _, response, _ ->
                        // Handle unauthorized requests
                    }
            }
            .csrf { it.disable() }

        return http.build()
    }
}