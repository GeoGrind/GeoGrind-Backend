package com.geogrind.geogrindbackend.exceptions.sessions

class SessionBadRequestException(error: String) : RuntimeException("Bad request while creating new session for current user: $error")