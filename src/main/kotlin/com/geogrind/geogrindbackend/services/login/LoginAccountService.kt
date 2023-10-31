package com.geogrind.geogrindbackend.services.login

import com.geogrind.geogrindbackend.dto.login.ConfirmUserLoginResquestDto
import com.geogrind.geogrindbackend.dto.login.UserLoginRequestDto
import com.geogrind.geogrindbackend.dto.registration.sendgrid.SendGridResponseDto
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
interface LoginAccountService {
    // login routes
    @Transactional
    suspend fun login(@Valid requestDto: UserLoginRequestDto): SendGridResponseDto

    @Transactional
    suspend fun confirmLoginHandler(@Valid requestDto: ConfirmUserLoginResquestDto): Pair<UserAccount, Cookie>
}