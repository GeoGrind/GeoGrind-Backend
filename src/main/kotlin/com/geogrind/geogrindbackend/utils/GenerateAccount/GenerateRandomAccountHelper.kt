package com.geogrind.geogrindbackend.utils.GenerateAccount

import com.geogrind.geogrindbackend.models.UserAccount

interface GenerateRandomAccountHelper {
    fun generateRandomString(length: Int): String
    fun createFakeUserAccount(): UserAccount
}