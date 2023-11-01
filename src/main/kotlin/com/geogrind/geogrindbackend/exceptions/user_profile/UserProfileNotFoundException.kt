package com.geogrind.geogrindbackend.exceptions.user_profile

class UserProfileNotFoundException(field: String): RuntimeException("Could not find the user profile with $field")