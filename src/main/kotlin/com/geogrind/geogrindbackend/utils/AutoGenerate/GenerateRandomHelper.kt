package com.geogrind.geogrindbackend.utils.AutoGenerate

import com.geogrind.geogrindbackend.models.user_account.UserAccount
import org.springframework.stereotype.Service

@Service
interface GenerateRandomHelper {
    fun generateRandomString(length: Int): String
    fun createFakeUserAccount(): UserAccount
    fun generateOTP(length: Int): String
}