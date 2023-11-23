package com.geogrind.geogrindbackend.dto.session

import java.util.UUID

data class GetSessionByIdDto (
    val userAccountId: UUID? = null,
)