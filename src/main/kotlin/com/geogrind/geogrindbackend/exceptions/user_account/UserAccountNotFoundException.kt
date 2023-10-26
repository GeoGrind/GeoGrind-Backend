package com.geogrind.geogrindbackend.exceptions.user_account

class UserAccountNotFoundException(field: String) : RuntimeException("Could not find user account with $field")
