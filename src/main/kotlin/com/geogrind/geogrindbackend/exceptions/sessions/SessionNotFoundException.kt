package com.geogrind.geogrindbackend.exceptions.sessions

class SessionNotFoundException(userAccountId: String) : RuntimeException("Could not find the current session with user account id: $userAccountId")