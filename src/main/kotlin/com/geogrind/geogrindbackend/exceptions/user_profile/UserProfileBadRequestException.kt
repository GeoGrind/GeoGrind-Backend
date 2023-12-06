package com.geogrind.geogrindbackend.exceptions.user_profile

class UserProfileBadRequestException(user_profile_form_errors: MutableMap<String, String>): RuntimeException("Bad request for user profile form") {
    val errors: Map<String, String> = user_profile_form_errors

    override fun getLocalizedMessage(): String {
        return errors.toString()
    }

}