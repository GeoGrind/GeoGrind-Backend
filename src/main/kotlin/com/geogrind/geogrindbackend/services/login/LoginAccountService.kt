package com.geogrind.geogrindbackend.services.login

import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
interface LoginAccountService {
    // login routes
    suspend fun login(@Valid requestDto: UserLoginRequestDto): UserAccount
}