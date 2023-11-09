package com.geogrind.geogrindbackend.utils.Validation.registration

import org.springframework.stereotype.Service

@Service
class UserAccountValidationHelperImpl : UserAccountValidationHelper {

    override fun validateEmail(email: String, registration_form_errors: MutableMap<String, String>) {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        if(!email.matches(emailRegex.toRegex())) {
            registration_form_errors["email"] = "Invalid email address"
        }
    }

    override fun validateUsername(username: String, registration_form_errors: MutableMap<String, String>) {
        when {
            username.isEmpty() -> registration_form_errors["username"] = "Please enter your username"
            username.length < 3 -> registration_form_errors["username"] = "Username must be at least 3 characters long"
        }
    }

    override fun validatePassword(
        password: String,
        username: String,
        registration_form_errors: MutableMap<String, String>
    ) {
        val errorMessages = mutableListOf<String>()

        fun validate(condition: Boolean, message: String) {
            if(!condition) {
                errorMessages.add(message)
            }
        }

        // Define validation functions
        val isLengthValid = password.length >= 8
        val hasDigit = password.any { it.isDigit() }
        val hasAlpha = password.any { it.isLetter() }
        val hasUpperChar = password.any { it.isUpperCase() }
        val hasLowerChar = password.any { it.isLowerCase() }
        val hasSpecialChar = password.any { it in "!@#\$%^&*():,.?/<>[]" }
        val isDifferentFromUsername = password != username

        // Perform validation
        validate(isLengthValid, "The length of the password must be longer than 8 characters!")
        validate(hasDigit, "Password must contain at least 1 number!")
        validate(hasAlpha, "Password must contain at least 1 character!")
        validate(hasUpperChar, "Password must contain at least 1 uppercase letter!")
        validate(hasLowerChar, "Password must contain at least 1 lowercase letter!")
        validate(hasSpecialChar, "Password must contain at least 1 special character: !@#\$%^&*():,.?/<>[]")
        validate(isDifferentFromUsername, "Password must be different from the username!")

        if (errorMessages.isNotEmpty()) {
            registration_form_errors["password"] = errorMessages.joinToString("\n")
        }
    }

    override fun validateConfirmPassword(
        password: String,
        confirm_password: String,
        registration_form_errors: MutableMap<String, String>
    ) {
        if(password != confirm_password) {
            registration_form_errors["confirm_password"] = "Password and confirm password do not match!"
        }
    }
}