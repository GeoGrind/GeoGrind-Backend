package com.geogrind.geogrindbackend.dto.session

import java.util.UUID

data class DeleteSessionByIdDto(
    var userAccountId: UUID? = null,
)
