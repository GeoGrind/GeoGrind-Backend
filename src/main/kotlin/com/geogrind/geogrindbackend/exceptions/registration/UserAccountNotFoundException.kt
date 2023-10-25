package com.geogrind.geogrindbackend.exceptions.registration

class UserAccountNotFoundException(field: String) : RuntimeException("Could not find user account with $field")
