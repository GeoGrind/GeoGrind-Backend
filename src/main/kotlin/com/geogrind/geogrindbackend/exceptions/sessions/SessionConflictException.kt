package com.geogrind.geogrindbackend.exceptions.sessions

class SessionConflictException(userAccountId: String) : RuntimeException("User with $userAccountId already has a current session!")