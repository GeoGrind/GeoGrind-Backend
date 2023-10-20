package com.geogrind.geogrindbackend.exceptions.registration

class UserAccountConflictException(conflict_field: String) : RuntimeException("User with $conflict_field has already been used!")