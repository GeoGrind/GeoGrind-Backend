package com.geogrind.geogrindbackend.utils.Validation.registration

interface UserAccountValidationHelper {
    fun validateEmail(email: String, registration_form_errors: MutableMap<String, String>)
    fun validateUsername(username: String, registration_form_errors: MutableMap<String, String>)
    fun validatePassword(password: String, username: String, registration_form_errors: MutableMap<String, String>)
    fun validateConfirmPassword(password: String, confirm_password: String, registration_form_errors: MutableMap<String, String>)
}