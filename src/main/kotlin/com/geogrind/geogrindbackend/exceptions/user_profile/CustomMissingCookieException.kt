package com.geogrind.geogrindbackend.exceptions.user_profile

class CustomMissingCookieException(cookieName: String) : Exception("Cookie with name: $cookieName is missing in the request header") {
}