package com.geogrind.geogrindbackend.exceptions.registration

class UserAccountNotFoundException(user_id: String) : RuntimeException("Could not find user account with $user_id")
