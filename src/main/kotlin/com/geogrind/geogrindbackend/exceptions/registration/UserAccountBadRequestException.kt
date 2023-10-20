package com.geogrind.geogrindbackend.exceptions.registration

class UserAccountBadRequestException(registration_form_errors: MutableMap<String, String>): RuntimeException("Bad request for user account registration form") {
    val errors: Map<String, String> = registration_form_errors

    override fun getLocalizedMessage(): String {
        return errors.toString()
    }
}